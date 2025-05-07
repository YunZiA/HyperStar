package com.yunzia.hyperstar.ui.component.card

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
fun Touch3DCard() {
    // 定义状态变量
    var targetRotationX by remember { mutableFloatStateOf(0f) }
    var targetRotationY by remember { mutableFloatStateOf(0f) }
    var pivotFractionX by remember { mutableFloatStateOf(0f) }
    var pivotFractionY by remember { mutableFloatStateOf(0f) }
    var targetScale by remember { mutableFloatStateOf(1f) }
    var targetShadowElevation by remember { mutableFloatStateOf(16f) }

    var transformOrigins by remember { mutableStateOf(TransformOrigin(1f, 1f)) }


    val fractionX by animateFloatAsState(targetValue = pivotFractionX, animationSpec = tween(durationMillis = 300))
    val fractionY by animateFloatAsState(targetValue = pivotFractionY, animationSpec = tween(durationMillis = 300))
    val rotationXX by animateFloatAsState(targetValue = targetRotationX, animationSpec = tween(durationMillis = 300))
    val rotationYY by animateFloatAsState(targetValue = targetRotationY, animationSpec = tween(durationMillis = 300))
    val scale by animateFloatAsState(targetValue = targetScale, animationSpec = tween(durationMillis = 300))
    val shadowElevations by animateFloatAsState(targetValue = targetShadowElevation, animationSpec = tween(durationMillis = 300))

    var pivot by remember { mutableStateOf(PivotPosition.BOTTOM_RIGHT) }


    fun  AwaitPointerEventScope.updatePivotAndRotation(offset: Offset) {
        pivot = when {
            offset.x < size.width / 2f && offset.y < size.height / 2f ->
                PivotPosition.TOP_LEFT
            offset.x >= size.width / 2f && offset.y < size.height / 2f ->
                PivotPosition.TOP_RIGHT
            offset.x < size.width / 2f && offset.y >= size.height / 2f ->
                PivotPosition.BOTTOM_LEFT
            else -> PivotPosition.BOTTOM_RIGHT
        }
        val relativeX = (offset.x / size.width).coerceIn(0f, 1f)
        val relativeY = (offset.y / size.height).coerceIn(0f, 1f)

        pivotFractionX = 1-relativeX
        pivotFractionY = 1-relativeY

        targetRotationX = lerp(2f, -2f, relativeY)
        targetRotationY = lerp(-2f, 2f, relativeX)

    }
    Box(
        modifier = Modifier
            .size(300.dp) // 卡片大小
            .graphicsLayer {
                rotationX = targetRotationX // 沿 X 轴旋转
                rotationY = targetRotationY // 沿 Y 轴旋转
                shadowElevation = shadowElevations
                shape = RoundedCornerShape(16.dp) // 圆角形状
                clip = true // 裁剪内容
                transformOrigin = TransformOrigin(pivotFractionX, pivotFractionY)
            }
            .background(Color.White) // 卡片背景颜色
            .pointerInput(Unit) {

                awaitPointerEventScope {
                    while (true) {
                        // Wait for the first touch
                        val down = awaitFirstDown(requireUnconsumed = false)

                        updatePivotAndRotation(down.position)

                        // Handle move events
                        do {
                            val event = awaitPointerEvent()
                            val position = event.changes.first().position
                            if (event.changes.any { it.positionChanged() }) {
                                updatePivotAndRotation(position)
                            }
                        } while (event.changes.any { it.pressed })
                        targetShadowElevation = 16f
                        targetRotationX = 0f // 恢复旋转角度
                        targetRotationY = 0f // 恢复旋转角度
                    }
                }

            },
        contentAlignment = Alignment.Center
    ) {
        // 卡片内容
        Text(
            text = "3D Pullable Card",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

data class TiltAnimationState(
    val rotationX: Animatable<Float, AnimationVector1D> = Animatable(0f),
    val rotationY: Animatable<Float, AnimationVector1D> = Animatable(0f),
    val pivotX: Animatable<Float, AnimationVector1D> = Animatable(0f),
    val pivotY: Animatable<Float, AnimationVector1D> = Animatable(0f),
    val scale: Animatable<Float, AnimationVector1D> = Animatable(1f)
) {

    var noAnim = false

    // 动画规格
    private val setAnimationSpec = tween<Float>(
        durationMillis = 1000,
        easing = CubicBezierEasing(0.17f, 0.17f, 0.23f, 0.96f)
    )
    private val updateAnimationSpec = tween<Float>(
        durationMillis = 500,
        easing = LinearEasing
        // CubicBezierEasing(0.17f, 0.17f, 0.23f, 0.96f)
    )

    internal fun setTilt(
        coroutineScope: CoroutineScope,
        offset: Offset,
        size: IntSize
    ) {
        if (noAnim) return
        val relativeX = (offset.x / size.width).coerceIn(0f, 1f)
        val relativeY = (offset.y / size.height).coerceIn(0f, 1f)
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val distanceFromCenter = Offset(offset.x - centerX, offset.y - centerY).getDistance()
        val maxDistance = Offset(centerX, centerY).getDistance()
        val distanceRatio = (distanceFromCenter / maxDistance).coerceIn(0f, 1f)

        coroutineScope.launch {
            launch { scale.animateTo(lerp(0.9f, 1f, distanceRatio), setAnimationSpec) }
            launch { pivotX.animateTo(1 - relativeX, setAnimationSpec) }
            launch { pivotY.animateTo(1 - relativeY, setAnimationSpec) }
            launch { rotationX.animateTo(lerp(3f, -3f, relativeY), setAnimationSpec) }
            launch { rotationY.animateTo(lerp(-3f, 3f, relativeX), setAnimationSpec) }
        }
    }

    internal fun updateTilt(
        coroutineScope: CoroutineScope,
        offset: Offset,
        size: IntSize
    ) {
        if (noAnim) return
        val relativeX = (offset.x / size.width).coerceIn(0f, 1f)
        val relativeY = (offset.y / size.height).coerceIn(0f, 1f)
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val distanceFromCenter = Offset(offset.x - centerX, offset.y - centerY).getDistance()
        val maxDistance = Offset(centerX, centerY).getDistance()
        val distanceRatio = (distanceFromCenter / maxDistance).coerceIn(0f, 1f)

        coroutineScope.launch {
            launch { scale.animateTo(lerp(0.9f, 1f, distanceRatio), updateAnimationSpec) }
            launch { pivotX.animateTo(1 - relativeX, updateAnimationSpec) }
            launch { pivotY.animateTo(1 - relativeY, updateAnimationSpec) }
            launch { rotationX.animateTo(lerp(3f, -3f, relativeY), updateAnimationSpec) }
            launch { rotationY.animateTo(lerp(-3f, 3f, relativeX), updateAnimationSpec) }
        }
    }

    internal fun reset(
        coroutineScope: CoroutineScope
    ) {

        if (noAnim) return
        coroutineScope.launch {
            launch { scale.animateTo(1f, setAnimationSpec) }
            launch { pivotX.animateTo(0f, setAnimationSpec) }
            launch { pivotY.animateTo(0f, setAnimationSpec) }
            launch { rotationX.animateTo(0f, setAnimationSpec) }
            launch { rotationY.animateTo(0f, setAnimationSpec) }

        }

    }


    internal fun recovery(
        coroutineScope: CoroutineScope,
        animationSpec: TweenSpec<Float>? = null
    ) {
        if (animationSpec == null){

            coroutineScope.launch {
                launch { scale.snapTo(1f) }
                launch { pivotX.snapTo(0f) }
                launch { pivotY.snapTo(0f) }
                launch { rotationX.snapTo(0f) }
                launch { rotationY.snapTo(0f) }
            }
        }else{

            coroutineScope.launch {
                launch { scale.animateTo(1f,animationSpec) }
                launch { pivotX.animateTo(0f,animationSpec) }
                launch { pivotY.animateTo(0f,animationSpec) }
                launch { rotationX.animateTo(0f,animationSpec) }
                launch { rotationY.animateTo(0f,animationSpec) }
            }
        }
    }



    companion object {
        // 用于保存状态的 Saver
        val Saver = listSaver(
            save = { state ->
                listOf(
                    state.rotationX.value,
                    state.rotationY.value,
                    state.pivotX.value,
                    state.pivotY.value,
                    state.scale.value
                )
            },
            restore = { values ->
                TiltAnimationState(
                    rotationX = Animatable(values[0]),
                    rotationY = Animatable(values[1]),
                    pivotX = Animatable(values[2]),
                    pivotY = Animatable(values[3]),
                    scale = Animatable(values[4])
                )
            }
        )
    }
}

fun Modifier.withTiltEffect(
    tiltState: TiltAnimationState,
    radius: Dp = 16.dp,
): Modifier = this
    .graphicsLayer {
        rotationX = tiltState.rotationX.value
        rotationY = tiltState.rotationY.value
        scaleX = tiltState.scale.value
        scaleY = tiltState.scale.value
        shape = SmoothRoundedCornerShape(radius,1f)
        clip = true
        transformOrigin = TransformOrigin(
            pivotFractionX = tiltState.pivotX.value,
            pivotFractionY = tiltState.pivotY.value
        )
    }


fun Modifier.withTiltEffect(
    tiltState: TiltAnimationState,
    coroutineScope: CoroutineScope,
    radius: Dp = 16.dp,
    longPressTimeThreshold: Long = 100L, // 长按时间阈值，单位毫秒
    moveThreshold: Float = 10f,
    stop: (Boolean) -> Unit = {},
): Modifier = this
    .graphicsLayer {
        rotationX = tiltState.rotationX.value
        rotationY = tiltState.rotationY.value
        scaleX = tiltState.scale.value
        scaleY = tiltState.scale.value
        transformOrigin = TransformOrigin(
            pivotFractionX = tiltState.pivotX.value,
            pivotFractionY = tiltState.pivotY.value
        )
        shape = SmoothRoundedCornerShape(radius,1f)
        clip = true
    }
    .pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val down = awaitFirstDown(requireUnconsumed = false)
                val downPosition = down.position
                var isLongPressDetected = false

                stop(false)
                tiltState.setTilt(coroutineScope, downPosition, size)

                try {
                    // 启动长按检测协程
                    val longPressJob = coroutineScope.launch {
                        delay(longPressTimeThreshold)
                        isLongPressDetected = true
                        stop(true)
                    }

                    do {
                        val event = awaitPointerEvent()
                        val position = event.changes.first().position
                        val movement = position - downPosition
                        val distance = movement.getDistance()

                        // 如果移动超过阈值，取消长按检测
                        if (distance > moveThreshold) {
                            longPressJob.cancel()
                            if (isLongPressDetected) {
                                isLongPressDetected = false
                                stop(false)
                            }
                        }

                        // 更新倾斜效果
                        if (event.changes.any { it.positionChanged() }) {
                            tiltState.updateTilt(coroutineScope, position, size)
                        }

                    } while (event.changes.any { it.pressed })

                    // 手指抬起时取消长按检测
                    longPressJob.cancel()

                } finally {
                    stop(false)
                    tiltState.reset(coroutineScope)

                }
            }
        }
    }

@Composable
fun rememberTiltAnimationState(): TiltAnimationState {

    return rememberSaveable(saver = TiltAnimationState.Saver) {
        TiltAnimationState()
    }
}
