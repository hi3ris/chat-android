/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.callhistory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.components.avatar.Avatar
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.matrix.api.core.RoomId
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun CallHistoryView(
    state: CallHistoryState,
    onRoomClick: (RoomId) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    if (state.callItems.isEmpty() && !state.isLoading) {
        CallHistoryEmptyState(modifier = modifier.fillMaxSize())
        return
    }

    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = contentPadding,
    ) {
        items(state.callItems, key = { it.roomId.value }) { item ->
            CallHistoryItemRow(
                item = item,
                onClick = { onRoomClick(item.roomId) },
            )
        }
    }
}

@Composable
private fun CallHistoryEmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                modifier = Modifier.size(48.dp),
                imageVector = CompoundIcons.VoiceCall(),
                tint = ElementTheme.colors.iconSecondary,
                contentDescription = null,
            )
            Text(
                text = "Aucun appel récent",
                style = ElementTheme.typography.fontBodyLgRegular,
                color = ElementTheme.colors.textSecondary,
            )
        }
    }
}

@Composable
private fun CallHistoryItemRow(
    item: CallHistoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(0.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Avatar(
            avatarData = item.avatarData,
            avatarType = if (item.isDirect) AvatarType.User else AvatarType.Room(),
        )

        Column(modifier = Modifier.weight(1f)) {
            val displayName = item.roomName ?: item.initiatorDisplayName ?: item.initiatorId.value
            Text(
                text = displayName,
                style = ElementTheme.typography.fontBodyLgMedium,
                color = ElementTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!item.isDirect && item.initiatorDisplayName != null) {
                Text(
                    text = item.initiatorDisplayName,
                    style = ElementTheme.typography.fontBodyMdRegular,
                    color = ElementTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            if (item.isOngoing) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = CompoundIcons.VideoCallSolid(),
                        tint = ElementTheme.colors.iconSuccessPrimary,
                        contentDescription = null,
                    )
                    Text(
                        text = "En cours",
                        style = ElementTheme.typography.fontBodySmMedium,
                        color = ElementTheme.colors.iconSuccessPrimary,
                    )
                }
            } else {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = CompoundIcons.VoiceCall(),
                    tint = ElementTheme.colors.iconSecondary,
                    contentDescription = null,
                )
            }
        }
    }
}

// Preview helper
internal fun aCallHistoryState(
    callItems: kotlinx.collections.immutable.ImmutableList<CallHistoryItem> = persistentListOf(),
    isLoading: Boolean = false,
) = CallHistoryState(
    callItems = callItems,
    isLoading = isLoading,
)
