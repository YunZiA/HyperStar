package com.yunzia.hyperstar.ui.component.card

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import top.yukonga.miuix.kmp.basic.Text


@Composable
fun Pressable3DCard() {
    // 定义状态变量
    var targetRotationX by remember { mutableFloatStateOf(0f) }
    var targetRotationY by remember { mutableFloatStateOf(0f) }
    var pivotFractionX by remember { mutableFloatStateOf(0f) }
    var pivotFractionY by remember { mutableFloatStateOf(0f) }
    var targetScale by remember { mutableFloatStateOf(1f) }
    var targetShadowElevation by remember { mutableFloatStateOf(16f) }

    val animatedRotationX by animateFloatAsState(
        targetValue = targetRotationX,
        animationSpec = tween(durationMillis = 300)
    )
    val animatedRotationY by animateFloatAsState(
        targetValue = targetRotationY,
        animationSpec = tween(durationMillis = 300)
    )
    val animatedPivotX by animateFloatAsState(
        targetValue = pivotFractionX,
        animationSpec = tween(durationMillis = 300)
    )
    val animatedPivotY by animateFloatAsState(
        targetValue = pivotFractionY,
        animationSpec = tween(durationMillis = 300)
    )
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = 300)
    )
    val shadowElevations by animateFloatAsState(
        targetValue = targetShadowElevation,
        animationSpec = tween(durationMillis = 300)
    )

    fun PointerInputScope.updatePivotAndRotation(offset: Offset) {
        val relativeX = (offset.x / size.width).coerceIn(0f, 1f)
        val relativeY = (offset.y / size.height).coerceIn(0f, 1f)

        // 计算到中心点的距离
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val distanceFromCenter = Offset(offset.x - centerX, offset.y - centerY).getDistance()
        val maxDistance = Offset(centerX, centerY).getDistance()
        val distanceRatio = (distanceFromCenter / maxDistance).coerceIn(0f, 1f)

        // 更新变换原点
        pivotFractionX = 1 - relativeX
        pivotFractionY = 1 - relativeY

        // 更新旋转角度
        targetRotationX = lerp(4f, -4f, relativeY)
        targetRotationY = lerp(-4f, 4f, relativeX)

        // 根据到中心点的距离更新缩放
        targetScale = lerp(0.9f, 1f, distanceRatio)
        targetShadowElevation = lerp(24f, 16f, distanceRatio)
    }

    Box(
        modifier = Modifier
            .size(300.dp)
            .graphicsLayer {
                rotationX = animatedRotationX
                rotationY = animatedRotationY
                scaleX = scale
                scaleY = scale
                shadowElevation = shadowElevations
                shape = RoundedCornerShape(16.dp)
                clip = true
                transformOrigin = TransformOrigin(
                    pivotFractionX = animatedPivotX,
                    pivotFractionY = animatedPivotY
                )
            }
            .background(Color.White)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        // 按压开始时更新状态
                        updatePivotAndRotation(offset)

                        // 等待按压释放
                        tryAwaitRelease()

                        // 按压释放后恢复初始状态
                        targetRotationX = 0f
                        targetRotationY = 0f
                        targetScale = 1f
                        targetShadowElevation = 16f
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Card",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
