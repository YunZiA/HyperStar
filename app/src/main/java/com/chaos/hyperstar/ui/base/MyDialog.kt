package com.chaos.hyperstar.ui.base

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val animatedAlpha = animateFloatAsState(if (showDialog) 1f else 0f)
    val animatedOffset = animateDpAsState(if (showDialog) 0.dp else 300.dp)
    val animatedScale = animateFloatAsState(if (showDialog) 1f else 0.8f)

    if (showDialog) {
        Box(
            modifier = Modifier
                //.alpha(animatedAlpha.value)
                //.offset(y = animatedOffset.value)
                //.scale(animatedScale.value)
                .background(Color.Black.copy(alpha = 0.3f))
                .fillMaxSize()
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}