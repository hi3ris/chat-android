/*
 * Copyright (c) 2025 CYBER DEFENSE AFRICA
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.libraries.matrix.api.screencapture

import io.element.android.libraries.matrix.api.timeline.Timeline

interface ScreenCaptureNotifier {
    /**
     * Set the currently active room timeline.
     * Call with null when leaving a room.
     */
    fun setActiveTimeline(timeline: Timeline?)

    /**
     * Send a screen capture notice to the currently active room.
     * Should be called when a screen recording attempt is detected.
     */
    suspend fun onScreenCaptureDetected()
}
