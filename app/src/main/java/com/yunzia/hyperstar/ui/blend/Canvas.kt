package com.yunzia.hyperstar.ui.blend

import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import dev.chrisbanes.haze.HazeLogger

fun ContentDrawScope.drawContentSafely() {
    try {
        drawContent()
    } catch (e: Exception) {
        val message = e.message.orEmpty()
        // Issues: 641 and 706
        if ("mViewFlags" in message || "LayoutNode" in message) {
            HazeLogger.d("ContentDrawScope", e) { "Error whilst drawing content" }
        } else {
            throw e
        }
    }
}