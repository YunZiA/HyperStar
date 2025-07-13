package com.yunzia.hyperstar.ui.screen.pagers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.kyant.liquidglass.GlassMaterial
import com.kyant.liquidglass.InnerRefraction
import com.kyant.liquidglass.LiquidGlassStyle
import com.kyant.liquidglass.RefractionValue
import com.kyant.liquidglass.liquidGlass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Text
import kotlin.math.roundToInt

// This is a simple FPS monitor that displays the current frames per second.
@Composable
fun FPSMonitor(visible: Boolean) {
    var fps by remember { mutableStateOf(0) }
    var lastFrameTime by remember { mutableStateOf(0L) }
    var frameCount by remember { mutableStateOf(0) }
    var totalFrameTime by remember { mutableStateOf(0L) }
    var offset by remember { mutableStateOf(Offset.Zero) }

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

    AnimatedVisibility(
        visible,
        modifier = Modifier
            .statusBarsPadding()
            .padding(top = 100.dp, start = 28.dp)
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
            }.liquidGlass(
                LiquidGlassStyle(
                    RoundedCornerShape(26.dp),
                    innerRefraction = InnerRefraction(
                        height = RefractionValue(8.dp),
                        amount = RefractionValue.Full
                    ),
                    material = GlassMaterial(
                        blurRadius = 0.8.dp,
                        whitePoint = 0.1f,
                        chromaMultiplier = 1.5f
                    )
                )
            ),
        enter = expandIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = 60f,
            ),
            expandFrom = Alignment.TopStart
        ) + scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = 60f,
            ),
            transformOrigin = TransformOrigin(0f, 0f)
        ),
        exit = shrinkOut(
            shrinkTowards = Alignment.TopStart,
        ) + scaleOut(
            transformOrigin = TransformOrigin(0f, 0f)
        )
    ) {

        Box(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioHighBouncy,
                        stiffness = 30f,
                    )
                )
        ) {


            Text(
                text = "FPS: $fps",
                fontWeight = FontWeight(550),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(18.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = CircleShape,
                        clip = false,
                        ambientColor = Color.Black.copy(alpha = 0.1f),
                        spotColor = Color.Black.copy(alpha = 0.3f)
                    ),
                color = if (fps < 57) Color.Red else Color.Green,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.3f), // 第一层阴影颜色
                        blurRadius = 15f, // 模糊半径
                        offset = Offset(0f, 3f) // 阴影偏移量
                    )
                ),
            )
        }
    }



}