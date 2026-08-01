/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.designsystem.atomic.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAbsoluteAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.element.android.compound.annotations.CoreColorToken
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.internal.LightColorTokens
import io.element.android.libraries.designsystem.R
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.text.withColoredPeriod
import io.element.android.libraries.designsystem.theme.components.CircularProgressIndicator
import io.element.android.libraries.designsystem.theme.components.Text

@Composable
fun SunsetPage(
    isLoading: Boolean,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    overallContent: @Composable () -> Unit,
) {
    ElementTheme(
        // Always use the opposite value of the current theme
        darkTheme = ElementTheme.isLightTheme,
        applySystemBarsUpdate = false,
    ) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            SunsetBackground()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = BiasAbsoluteAlignment(
                        horizontalBias = 0f,
                        verticalBias = -0.05f
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = ElementTheme.colors.iconPrimary
                            )
                        } else {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = withColoredPeriod(title),
                            style = ElementTheme.typography.fontHeadingXlBold,
                            textAlign = TextAlign.Center,
                            color = ElementTheme.colors.textPrimary,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            modifier = Modifier.widthIn(max = 360.dp),
                            text = subtitle,
                            style = ElementTheme.typography.fontBodyLgRegular,
                            textAlign = TextAlign.Center,
                            color = ElementTheme.colors.textPrimary,
                        )
                    }
                }
                overallContent()
            }
        }
    }
}

@OptIn(CoreColorToken::class)
@Composable
private fun SunsetBackground() {
    val bottomBackgroundColor = if (ElementTheme.isLightTheme) {
        LightColorTokens.colorThemeBg
    } else {
        Color(0xFF121418)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        // Fond : dégradé multi-stop CDA vert → fond thème
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color(0xFF006A4E),
                            0.35f to Color(0xFF005C43),
                            0.65f to bottomBackgroundColor.copy(alpha = 0.6f),
                            1.0f to bottomBackgroundColor,
                        )
                    )
                )
        )
        // Logo CDA centré en haut
        Image(
            painter = painterResource(id = R.drawable.element_logo),
            contentDescription = null,
            modifier = Modifier
                .size(72.dp)
                .align(Alignment.TopCenter)
                .padding(top = 48.dp),
        )
    }
}

@PreviewsDayNight
@Composable
internal fun SunsetPagePreview() = ElementPreview {
    SunsetPage(
        isLoading = true,
        title = "Title with a green period.",
        subtitle = "Subtitle",
        overallContent = {}
    )
}
