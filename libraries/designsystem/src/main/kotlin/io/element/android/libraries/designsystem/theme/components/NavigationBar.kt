/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.designsystem.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.preview.ElementThemedPreview
import io.element.android.libraries.designsystem.preview.PreviewGroup

private val CdaGreen = Color(0xFF006A4E)
private val CdaYellow = Color(0xFFFFCE00)
private val CdaRed = Color(0xFFD21034)

@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color = ElementNavigationBarDefaults.containerColor,
    contentColor: Color = MaterialTheme.colorScheme.contentColorFor(containerColor),
    tonalElevation: Dp = ElementNavigationBarDefaults.tonalElevation,
    windowInsets: WindowInsets = ElementNavigationBarDefaults.windowInsets,
    content: @Composable RowScope.() -> Unit
) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth().height(2.dp)) {
            Box(modifier = Modifier.weight(1f).height(2.dp).background(CdaRed))
            Box(modifier = Modifier.weight(1f).height(2.dp).background(CdaGreen))
            Box(modifier = Modifier.weight(1f).height(2.dp).background(CdaYellow))
        }
        androidx.compose.material3.NavigationBar(
            containerColor = containerColor,
            contentColor = contentColor,
            tonalElevation = tonalElevation,
            windowInsets = windowInsets,
            content = content
        )
    }
}

object ElementNavigationBarDefaults {
    val containerColor: Color
        @Composable get() = if (ElementTheme.isLightTheme) {
            ElementTheme.colors.bgSubtlePrimary
        } else {
            ElementTheme.colors.textOnSolidPrimary
        }

    val tonalElevation: Dp = NavigationBarDefaults.Elevation

    val windowInsets: WindowInsets
        @Composable get() = NavigationBarDefaults.windowInsets
}

@Preview(group = PreviewGroup.AppBars)
@Composable
internal fun NavigationBarPreview() = ElementThemedPreview {
    NavigationBar {
        NavigationBarItem(
            icon = {
                NavigationBarIcon(
                    imageVector = CompoundIcons.ChatSolid(),
                    count = 5,
                    isCritical = false,
                )
            },
            label = {
                NavigationBarText(
                    text = "Chats"
                )
            },
            selected = true,
            onClick = {},
        )
        NavigationBarItem(
            icon = {
                NavigationBarIcon(
                    imageVector = CompoundIcons.ChatSolid(),
                    count = 5,
                    isCritical = true,
                )
            },
            label = {
                NavigationBarText(
                    text = "Teams"
                )
            },
            selected = false,
            onClick = {},
        )
        NavigationBarItem(
            icon = {
                NavigationBarIcon(
                    imageVector = CompoundIcons.ChatSolid(),
                    count = 0,
                    isCritical = false,
                )
            },
            label = {
                NavigationBarText(
                    text = "Other"
                )
            },
            selected = false,
            onClick = {},
        )
    }
}
