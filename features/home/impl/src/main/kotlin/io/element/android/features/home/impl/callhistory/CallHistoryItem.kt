/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.callhistory

import androidx.compose.runtime.Immutable
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.UserId

@Immutable
data class CallHistoryItem(
    val roomId: RoomId,
    val roomName: String?,
    val avatarData: AvatarData,
    val initiatorId: UserId,
    val initiatorDisplayName: String?,
    val timestamp: Long,
    val isOngoing: Boolean,
    val isDirect: Boolean,
)
