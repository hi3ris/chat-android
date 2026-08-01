/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.designsystem.components.avatar

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.libraries.designsystem.components.avatar.internal.RoomAvatar
import io.element.android.libraries.designsystem.components.avatar.internal.SpaceAvatar
import io.element.android.libraries.designsystem.components.avatar.internal.UserAvatar
import io.element.android.libraries.designsystem.preview.ElementThemedPreview
import io.element.android.libraries.designsystem.preview.PreviewGroup
import io.element.android.libraries.designsystem.utils.CommonDrawables
import kotlinx.collections.immutable.persistentListOf

private val cdaBorderColor = Color(0xFF006A4E)
private val cdaTricolorBrush = Brush.sweepGradient(
    colors = listOf(
        Color(0xFFD21034),
        Color(0xFF006A4E),
        Color(0xFFFFCE00),
        Color(0xFFD21034),
    )
)

@Composable
fun Avatar(
    avatarData: AvatarData,
    avatarType: AvatarType,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    // If not null, will be used instead of the size from avatarData
    forcedAvatarSize: Dp? = null,
    // If true, will show initials even if avatarData.url is not null
    hideImage: Boolean = false,
    showTricolorBorder: Boolean = false,
) {
    val size = forcedAvatarSize ?: avatarData.size.dp
    val shape = avatarType.avatarShape(size)
    // Bordure uniquement sur les avatars utilisateur
    val userBorderedModifier = when {
        showTricolorBorder -> modifier.border(width = 2.dp, brush = cdaTricolorBrush, shape = shape)
        else -> modifier.border(width = 2.dp, color = cdaBorderColor, shape = shape)
    }
    when (avatarType) {
        is AvatarType.Room -> RoomAvatar(
            avatarData = avatarData,
            avatarType = avatarType,
            modifier = modifier,
            hideAvatarImage = hideImage,
            forcedAvatarSize = forcedAvatarSize,
            contentDescription = contentDescription,
        )
        AvatarType.User -> UserAvatar(
            avatarData = avatarData,
            modifier = userBorderedModifier,
            contentDescription = contentDescription,
            forcedAvatarSize = forcedAvatarSize,
            hideImage = hideImage,
        )
        is AvatarType.Space -> SpaceAvatar(
            avatarData = avatarData,
            avatarType = avatarType,
            modifier = modifier,
            hideAvatarImage = hideImage,
            forcedAvatarSize = forcedAvatarSize,
            contentDescription = contentDescription,
        )
    }
}

@Preview(group = PreviewGroup.Avatars)
@Composable
internal fun AvatarPreview() = ElementThemedPreview(
    drawableFallbackForImages = CommonDrawables.sample_background,
) {
    Column(
        modifier = Modifier.padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        listOf(
            anAvatarData(size = AvatarSize.UserListItem),
            anAvatarData(size = AvatarSize.UserListItem, name = null),
            anAvatarData(size = AvatarSize.UserListItem, url = "aUrl"),
        ).forEach { avatarData ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Avatar(
                    avatarData = avatarData,
                    avatarType = AvatarType.User,
                )
                Avatar(
                    avatarData = avatarData,
                    avatarType = AvatarType.Room(isTombstoned = false),
                )
                Avatar(
                    avatarData = avatarData,
                    avatarType = AvatarType.Room(
                        heroes = persistentListOf(
                            anAvatarData("@carol:server.org", "Carol", size = AvatarSize.UserListItem),
                            anAvatarData("@david:server.org", "David", size = AvatarSize.UserListItem),
                            anAvatarData("@eve:server.org", "Eve", size = AvatarSize.UserListItem),
                            anAvatarData("@justin:server.org", "Justin", size = AvatarSize.UserListItem),
                        )
                    )
                )
                Avatar(
                    avatarData = avatarData,
                    avatarType = AvatarType.Room(isTombstoned = true),
                )
                Avatar(
                    avatarData = avatarData,
                    avatarType = AvatarType.Space(isTombstoned = false),
                )
                Avatar(
                    avatarData = avatarData,
                    avatarType = AvatarType.Space(isTombstoned = true),
                )
            }
        }
    }
}
