package com.yunzia.hyperstar.ui.screen.pagers

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Text
import kotlin.math.roundToInt

// This is a simple FPS monitor that displays the current frames per second.
@Composable
fun FPSMonitor(modifier: Modifier = Modifier) {
    var fps by remember { mutableStateOf(0) }
    var lastFrameTime by remember { mutableStateOf(0L) }
    var frameCount by remember { mutableStateOf(0) }
    var totalFrameTime by remember { mutableStateOf(0L) }
    var offset by remember { mutableStateOf(Offset.Zero) }


    Box(
        modifier = Modifier
            .statusBarsPadding()
            .padding(top = 100.dp,start = 28.dp)
            .offset {
                IntOffset(offset.x.roundToInt(), offset.y.roundToInt())
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { /* 可选：开始拖动时触发 */ },
                    onDragEnd = { /* 可选：结束拖动时触发 */ },
                    onDragCancel = { /* 可选：拖动取消时触发 */ },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offset += dragAmount
                    }
                )
            }.then(modifier)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioHighBouncy,
                    stiffness = 30f,
                )
            ),
        contentAlignment = Alignment.Center,
    ) {

        Text(
            text = "FPS: $fps",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(20.dp),
            color = if (fps < 57) Color.Red else Color.Green
        )
    }



    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            while (true) {
                withFrameMillis { frameTimeMillis ->
                    if (lastFrameTime != 0L) {
                        val frameDuration = frameTimeMillis - lastFrameTime
                        totalFrameTime += frameDuration
                        frameCount++
                        if (totalFrameTime >= 1000L) {
                            fps = frameCount
                            frameCount = 0
                            totalFrameTime = 0L
                        }
                    }
                    lastFrameTime = frameTimeMillis
                }
            }
        }
    }
}