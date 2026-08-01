/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.callhistory

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.di.annotations.SessionCoroutineScope
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.api.roomlist.LatestEventValue
import io.element.android.libraries.matrix.api.roomlist.RoomSummary
import io.element.android.libraries.matrix.api.timeline.MatrixTimelineItem
import io.element.android.libraries.matrix.api.timeline.Timeline
import io.element.android.libraries.matrix.api.timeline.item.event.CallNotifyContent
import io.element.android.libraries.matrix.api.timeline.item.event.LegacyCallInviteContent
import io.element.android.libraries.matrix.api.timeline.item.event.ProfileDetails
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber

private const val PARALLEL_ROOMS = 8
private const val MAX_PAGINATE_ROUNDS = 3
private const val INITIAL_TIMEOUT_MS = 5_000L
private const val PAGINATE_TIMEOUT_MS = 3_000L

@Inject
@SingleIn(SessionScope::class)
class CallHistoryPresenter(
    private val client: MatrixClient,
    private val dispatchers: CoroutineDispatchers,
    private val store: CallHistoryStore,
    @SessionCoroutineScope private val sessionCoroutineScope: CoroutineScope,
) : Presenter<CallHistoryState> {

    private val _callHistory = MutableStateFlow<Map<String, CallHistoryItem>>(emptyMap())
    private val _isScanning = MutableStateFlow(false)

    init {
        sessionCoroutineScope.launch {
            // A – Load persisted call history immediately before any scan
            val persisted = store.load()
            if (persisted.isNotEmpty()) {
                _callHistory.value = persisted
            }

            // B (Phase 1) – reactive scan: latestEvent per room updates in real-time via sync
            launch {
                client.roomListService.allRooms.summaries.collect { summaries ->
                    val found = mutableMapOf<String, CallHistoryItem>()
                    summaries.forEach { summary ->
                        summary.toCallHistoryItemOrNull()?.let { item ->
                            found["${item.roomId}:${item.timestamp}"] = item
                        }
                    }
                    if (found.isNotEmpty()) {
                        val hadNew = found.keys.any { !_callHistory.value.containsKey(it) }
                        _callHistory.update { it + found }
                        if (hadNew) store.save(_callHistory.value)
                    }
                }
            }

            // D + B (Phase 2) – deep scan using rawTimelineItems + pagination + live watchers
            launch {
                _isScanning.value = true
                try {
                    deepScan()
                } catch (e: Exception) {
                    Timber.w(e, "CallHistory: deep scan failed")
                } finally {
                    _isScanning.value = false
                }
            }
        }
    }

    @Composable
    override fun present(): CallHistoryState {
        val callHistory by _callHistory.collectAsState()
        val isScanning by _isScanning.collectAsState()
        val sortedItems = remember(callHistory) {
            callHistory.values
                .sortedByDescending { it.timestamp }
                .toImmutableList()
        }
        return CallHistoryState(
            callItems = sortedItems,
            isLoading = isScanning && sortedItems.isEmpty(),
        )
    }

    private suspend fun deepScan() = withContext(dispatchers.io) {
        val roomIds = client.getJoinedRoomIds().getOrElse {
            Timber.w(it, "CallHistory: failed to get room IDs")
            return@withContext
        }
        Timber.d("CallHistory: deep scan starting, ${roomIds.size} rooms")

        // Process in parallel batches (D: use rawTimelineItems)
        roomIds.chunked(PARALLEL_ROOMS).forEach { batch ->
            batch.map { roomId ->
                async { scanRoom(roomId) }
            }.awaitAll()
        }
        Timber.d("CallHistory: deep scan done, ${_callHistory.value.size} total calls")

        // B – after deep scan, start live watchers for all rooms
        roomIds.forEach { roomId ->
            launch { watchRoomForLiveCalls(roomId) }
        }
    }

    /**
     * D: Scan a single room using rawTimelineItems (SharedFlow replay=1, no combine overhead).
     * Paginates backwards to find calls older than the initial batch.
     */
    private suspend fun scanRoom(roomId: RoomId) {
        val room = client.getJoinedRoom(roomId) ?: return
        try {
            val timeline = room.liveTimeline

            // D: use rawTimelineItems instead of timelineItems
            val initialItems = withTimeoutOrNull(INITIAL_TIMEOUT_MS) {
                timeline.rawTimelineItems.first { it.isNotEmpty() }
            }
            if (initialItems == null) {
                Timber.d("CallHistory: timeout on initial items for $roomId")
                return
            }

            val hadNew = mergeCallItems(room, initialItems)
            var lastItemCount = initialItems.size

            // Paginate backwards to find older calls
            repeat(MAX_PAGINATE_ROUNDS) { round ->
                if (!timeline.backwardPaginationStatus.value.canPaginate) return@repeat

                timeline.paginate(Timeline.PaginationDirection.BACKWARDS).getOrElse { return@repeat }

                val updatedItems = withTimeoutOrNull(PAGINATE_TIMEOUT_MS) {
                    timeline.rawTimelineItems.first { it.size > lastItemCount }
                } ?: return@repeat

                Timber.d("CallHistory: $roomId page ${round + 1}, ${updatedItems.size} items")
                mergeCallItems(room, updatedItems)
                lastItemCount = updatedItems.size
            }

            if (hadNew) store.save(_callHistory.value)
        } catch (e: Exception) {
            Timber.w(e, "CallHistory: failed to scan $roomId")
        }
    }

    /**
     * B: Keep a live watcher on each room's timeline.
     * When a new event is synced, re-check rawTimelineItems for new call events.
     */
    private suspend fun watchRoomForLiveCalls(roomId: RoomId) {
        val room = client.getJoinedRoom(roomId) ?: return
        try {
            // onSyncedEventReceived fires each time the Rust SDK receives a new timeline event
            room.liveTimeline.onSyncedEventReceived.collect {
                val items = withTimeoutOrNull(2_000L) {
                    room.liveTimeline.rawTimelineItems.first { it.isNotEmpty() }
                } ?: return@collect

                val hadNew = mergeCallItems(room, items)
                if (hadNew) store.save(_callHistory.value)
            }
        } catch (e: Exception) {
            Timber.w(e, "CallHistory: live watcher failed for $roomId")
        }
    }

    /** Returns true if at least one new call item was added. */
    private suspend fun mergeCallItems(room: JoinedRoom, items: List<MatrixTimelineItem>): Boolean {
        val callEvents = items.filterIsInstance<MatrixTimelineItem.Event>()
            .filter { it.event.content is CallNotifyContent || it.event.content is LegacyCallInviteContent }

        if (callEvents.isEmpty()) return false

        val existing = _callHistory.value
        val found = mutableMapOf<String, CallHistoryItem>()
        val roomId = room.roomId
        val info = room.info()

        callEvents.forEach { timelineItem ->
            val event = timelineItem.event
            val key = "$roomId:${event.timestamp}"
            if (!existing.containsKey(key) && !found.containsKey(key)) {
                val displayName = (event.senderProfile as? ProfileDetails.Ready)?.displayName
                found[key] = CallHistoryItem(
                    roomId = roomId,
                    roomName = info.name,
                    avatarData = AvatarData(
                        id = roomId.value,
                        name = info.name,
                        url = info.avatarUrl,
                        size = AvatarSize.RoomListItem,
                    ),
                    initiatorId = event.sender,
                    initiatorDisplayName = displayName,
                    timestamp = event.timestamp,
                    isOngoing = info.hasRoomCall,
                    isDirect = info.isDirect,
                )
            }
        }

        if (found.isEmpty()) return false

        Timber.d("CallHistory: +${found.size} call(s) in $roomId")
        _callHistory.update { it + found }
        return true
    }

    private fun RoomSummary.toCallHistoryItemOrNull(): CallHistoryItem? {
        val remote = latestEvent as? LatestEventValue.Remote ?: return null
        val isCallEvent = remote.content is CallNotifyContent || remote.content is LegacyCallInviteContent
        if (!isCallEvent) return null
        val displayName = (remote.senderProfile as? ProfileDetails.Ready)?.displayName
        return CallHistoryItem(
            roomId = roomId,
            roomName = info.name,
            avatarData = AvatarData(
                id = roomId.value,
                name = info.name,
                url = info.avatarUrl,
                size = AvatarSize.RoomListItem,
            ),
            initiatorId = remote.senderId,
            initiatorDisplayName = displayName,
            timestamp = remote.timestamp,
            isOngoing = info.hasRoomCall,
            isDirect = info.isDirect,
        )
    }
}
