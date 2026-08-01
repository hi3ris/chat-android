/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.callhistory

import androidx.datastore.preferences.core.stringPreferencesKey
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.preferences.api.store.PreferenceDataStoreFactory
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber

private val PREF_CALL_HISTORY = stringPreferencesKey("call_history_json")

@Inject
@SingleIn(SessionScope::class)
class CallHistoryStore(
    preferenceDataStoreFactory: PreferenceDataStoreFactory,
) {
    private val dataStore = preferenceDataStoreFactory.create("call_history_store")

    suspend fun load(): Map<String, CallHistoryItem> {
        return try {
            val json = dataStore.data.first()[PREF_CALL_HISTORY] ?: return emptyMap()
            val array = JSONArray(json)
            val result = mutableMapOf<String, CallHistoryItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val key = obj.getString("key")
                val roomIdValue = obj.getString("roomId")
                result[key] = CallHistoryItem(
                    roomId = RoomId(roomIdValue),
                    roomName = obj.optString("roomName").ifEmpty { null },
                    avatarData = AvatarData(
                        id = roomIdValue,
                        name = obj.optString("roomName").ifEmpty { null },
                        url = obj.optString("avatarUrl").ifEmpty { null },
                        size = AvatarSize.RoomListItem,
                    ),
                    initiatorId = UserId(obj.getString("initiatorId")),
                    initiatorDisplayName = obj.optString("initiatorDisplayName").ifEmpty { null },
                    timestamp = obj.getLong("timestamp"),
                    isOngoing = false, // reset on load, will be updated by live scan
                    isDirect = obj.getBoolean("isDirect"),
                )
            }
            Timber.d("CallHistoryStore: loaded ${result.size} items")
            result
        } catch (e: Exception) {
            Timber.w(e, "CallHistoryStore: failed to load")
            emptyMap()
        }
    }

    suspend fun save(items: Map<String, CallHistoryItem>) {
        try {
            val array = JSONArray()
            items.forEach { (key, item) ->
                val obj = JSONObject().apply {
                    put("key", key)
                    put("roomId", item.roomId.value)
                    put("roomName", item.roomName ?: "")
                    put("avatarUrl", item.avatarData.url ?: "")
                    put("initiatorId", item.initiatorId.value)
                    put("initiatorDisplayName", item.initiatorDisplayName ?: "")
                    put("timestamp", item.timestamp)
                    put("isDirect", item.isDirect)
                }
                array.put(obj)
            }
            dataStore.updateData { prefs ->
                prefs.toMutablePreferences().apply {
                    set(PREF_CALL_HISTORY, array.toString())
                }
            }
        } catch (e: Exception) {
            Timber.w(e, "CallHistoryStore: failed to save")
        }
    }
}
