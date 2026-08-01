/*
 * Copyright (c) 2025 CYBER DEFENSE AFRICA
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.matrix.impl.screencapture

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.element.android.libraries.matrix.api.screencapture.ScreenCaptureNotifier
import io.element.android.libraries.matrix.api.timeline.Timeline
import dev.zacsweers.metro.Inject

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultScreenCaptureNotifier @Inject constructor() : ScreenCaptureNotifier {

    @Volatile
    private var activeTimeline: Timeline? = null

    override fun setActiveTimeline(timeline: Timeline?) {
        activeTimeline = timeline
    }

    override suspend fun onScreenCaptureDetected() {
        activeTimeline?.sendMessage(
            body = "⚠️ Tentative d'enregistrement d'écran détectée",
            htmlBody = "<p>⚠️ <strong>Tentative d'enregistrement d'écran détectée</strong></p>",
            intentionalMentions = emptyList(),
        )
    }
}
