@file:OptIn(ExperimentalWearMaterialApi::class)

package yunzia.ui

//import androidx.compose.material.ExperimentalMaterialApi
import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.SwipeableState
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import kotlinx.coroutines.launch
import kotlin.math.min

/**
 * IOS样式的SwitchButton(可点击，可滑动)
 * @author fuqiang
 * email developer_melody@163.com
 * created 2022/7/16 10:09
 */
//@OptIn(ExperimentalMaterialApi::class)
@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun IosSwitchButton(
    modifier: Modifier,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit),
    width: Dp = 49.dp,
    height: Dp = 30.dp,
    checkedTrackColor: Color = Color(0xFF4D7DEE),
    uncheckedTrackColor: Color = Color(0xFFC7C7C7),
    gapBetweenThumbAndTrackEdge: Dp = 2.dp
) {
    val scope = rememberCoroutineScope()
    val thumbRadius = (height / 2) - gapBetweenThumbAndTrackEdge
    val endAnchor = with(LocalDensity.current) {
        (width - thumbRadius - gapBetweenThumbAndTrackEdge).toPx()
    }
    val startAnchor = with(LocalDensity.current) {
        (thumbRadius + gapBetweenThumbAndTrackEdge).toPx()
    }
    val anchors = mapOf(startAnchor to 0, endAnchor to 1)
    // FIX：初始化设置checked为true或者false,始终显示的值为false的情况，问题来自：https://github.com/TheMelody/ComposeIOSSwitchButton/issues/1
    val swipeableState = rememberSwipeableState(initialValue = if(checked) 1 else 0, animationSpec = tween())
    val unCheckedTrackScale = rememberSaveable { mutableStateOf(1F) }
    val checkedTrackLerpColor by remember {
        derivedStateOf {
            lerp(
                // 开始的颜色
                uncheckedTrackColor,
                // 结束的颜色
                checkedTrackColor,
                // 选中的Track颜色透明度，根据缩放值计算颜色转换的进度
                min((1F - unCheckedTrackScale.value) * 2, 1F)
            )
        }
    }
    val switchONState = remember{ mutableStateOf(checked) }

    LaunchedEffect(swipeableState.offset.value) {
        val swipeOffset = swipeableState.offset.value
        // 未选中的Track缩放大小
        var trackScale: Float
        ((swipeOffset - startAnchor) / endAnchor).also {
            trackScale = if (it < 0F) 0F else it
        }
        // 未选中的Track缩放大小更新
        unCheckedTrackScale.value = 1F - trackScale
        // 更新开关状态
        switchONState.value = swipeOffset >= endAnchor
        // 回调状态
        onCheckedChange.invoke(switchONState.value)
    }

    Canvas(
        modifier = modifier
            .size(width = width, height = height)
            .swipeTrack(
                anchors = anchors,
                swipeableState = swipeableState,
                onClick = {
                    scope.launch {
                        swipeableState.animateTo(if (!switchONState.value) 1 else 0)
                    }
                }
            )
    ) {
        // 选中状态下的背景
        drawRoundRect(
            //这种的不再使用：Color(ArgbEvaluator().evaluate(t, AndroidColor.RED, AndroidColor.BLUE) as Int)
            color = checkedTrackLerpColor,
            cornerRadius = CornerRadius(x = height.toPx(), y = height.toPx()),
        )
        // 未选中状态下的背景，随着滑动或者点击切换了状态，进行缩放
        scale(
            scaleX = unCheckedTrackScale.value,
            scaleY = unCheckedTrackScale.value,
            pivot = Offset(size.width * 1.0F / 2F + startAnchor, size.height * 1.0F / 2F)
        ) {
            drawRoundRect(
                color = uncheckedTrackColor,
                cornerRadius = CornerRadius(x = height.toPx(), y = height.toPx()),
            )
        }
        // Thumb
        drawCircle(
            color = Color.White,
            radius = thumbRadius.toPx(),
            center = Offset(swipeableState.offset.value, size.height / 2)
        )
    }
}

//ModifierExtensions
@SuppressLint("UnnecessaryComposedModifier")
//@ExperimentalMaterialApi
internal fun Modifier.swipeTrack(
    anchors: Map<Float, Int>,
    swipeableState: SwipeableState<Int>,
    onClick: () -> Unit
) = composed {
    this.then(Modifier
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    onClick.invoke()
                }
            )
        }
        .swipeable(
            state = swipeableState,
            anchors = anchors,
            // 默认情况下，在弹回锚点之前，将允许滑动稍微超出边界, 这里设置为 null 时，滑动不能超出边界
            resistance = null,
            thresholds = { _, _ ->
                FractionalThreshold(0.3F)
            },
            orientation = Orientation.Horizontal
        )
    )
}

