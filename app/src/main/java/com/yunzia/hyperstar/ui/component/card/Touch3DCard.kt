package com.yunzia.hyperstar.ui.component.card

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.shapes.SmoothRoundedCornerShape

data class TiltAnimationState(
    val rotationX: Animatable<Float, AnimationVector1D> = Animatable(0f),
    val rotationY: Animatable<Float, AnimationVector1D> = Animatable(0f),
    val pivotX: Animatable<Float, AnimationVector1D> = Animatable(0.5f),
    val pivotY: Animatable<Float, AnimationVector1D> = Animatable(0.5f),
    val scale: Animatable<Float, AnimationVector1D> = Animatable(1f)
) {

    var noAnim = false

    private val setAnimationSpec = tween<Float>(
        durationMillis = 700,
        easing = CubicBezierEasing(0.17f, 0.17f, 0.23f, 0.96f)
    )
    private val updateAnimationSpec = tween<Float>(
        durationMillis = 500,
        easing = LinearEasing
    )

    internal fun setTilt(coroutineScope: CoroutineScope, offset: Offset, size: IntSize) {
        animateTilt(coroutineScope, offset, size, setAnimationSpec)
    }

    internal fun updateTilt(coroutineScope: CoroutineScope, offset: Offset, size: IntSize) {
        animateTilt(coroutineScope, offset, size, updateAnimationSpec)
    }

    internal fun reset(coroutineScope: CoroutineScope) {
        if (noAnim) return
        coroutineScope.launch {
            launch { scale.animateTo(1f, setAnimationSpec) }
            launch { pivotX.animateTo(0.5f, setAnimationSpec) }
            launch { pivotY.animateTo(0.5f, setAnimationSpec) }
            launch { rotationX.animateTo(0f, setAnimationSpec) }
            launch { rotationY.animateTo(0f, setAnimationSpec) }
        }
    }

    internal fun recovery(coroutineScope: CoroutineScope, animationSpec: TweenSpec<Float>? = null) {
        if (animationSpec == null) {
            coroutineScope.launch {
                launch { scale.snapTo(1f) }
                launch { pivotX.snapTo(0.5f) }
                launch { pivotY.snapTo(0.5f) }
                launch { rotationX.snapTo(0f) }
                launch { rotationY.snapTo(0f) }
            }
        } else {
            coroutineScope.launch {
                launch { scale.animateTo(1f, animationSpec) }
                launch { pivotX.animateTo(0.5f, animationSpec) }
                launch { pivotY.animateTo(0.5f, animationSpec) }
                launch { rotationX.animateTo(0f, animationSpec) }
                launch { rotationY.animateTo(0f, animationSpec) }
            }
        }
    }

    private fun animateTilt(
        coroutineScope: CoroutineScope,
        offset: Offset,
        size: IntSize,
        spec: TweenSpec<Float>
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
            launch { scale.animateTo(lerp(0.9f, 1f, distanceRatio), spec) }
            launch { pivotX.animateTo(1 - relativeX, spec) }
            launch { pivotY.animateTo(1 - relativeY, spec) }
            launch { rotationX.animateTo(lerp(3f, -3f, relativeY), spec) }
            launch { rotationY.animateTo(lerp(-3f, 3f, relativeX), spec) }
        }
    }

    companion object {
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
        shape = SmoothRoundedCornerShape(radius)
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
    longPressTimeThreshold: Long = 100L,
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
        shape = SmoothRoundedCornerShape(radius)
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
                    val longPressJob = coroutineScope.launch {
                        delay(longPressTimeThreshold)
                        isLongPressDetected = true
                        stop(true)
                    }

                    do {
                        val event = awaitPointerEvent()
                        val position = event.changes.first().position
                        val distance = (position - downPosition).getDistance()

                        if (distance > moveThreshold) {
                            longPressJob.cancel()
                            if (isLongPressDetected) {
                                isLongPressDetected = false
                                stop(false)
                            }
                        }

                        if (event.changes.any { it.positionChanged() }) {
                            tiltState.updateTilt(coroutineScope, position, size)
                        }
                    } while (event.changes.any { it.pressed })

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
