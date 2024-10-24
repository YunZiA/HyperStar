package yunzia.colorpicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.github.ajalt.colormath.model.HSV


@Composable
internal fun SaturationValueArea(
    modifier: Modifier = Modifier,
    currentColor: HsvColor,
    onSaturationValueChanged: (saturation: Float, value: Float) -> Unit
) {
    val blackGradientBrush = remember {
        Brush.verticalGradient(listOf(Color(0xffffffff), Color(0xff000000)))
    }
    val cornerRadius = mCornerRadius(20f)

    val currentColorGradientBrush = remember(currentColor.hue) {
        val hsv = HSV(currentColor.hue, 1.0f, 1.0f)
        val rgb = hsv.toSRGB()
        Brush.horizontalGradient(
            listOf(
                Color(0xffffffff),
                Color(rgb.redInt, rgb.greenInt, rgb.blueInt, rgb.alphaInt)
            )
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    val (s, v) = getSaturationPoint(down.position, size)
                    onSaturationValueChanged(s, v)
                    drag(down.id) { change ->
                        if (change.positionChange() != Offset.Zero) change.consume()
                        val (newSaturation, newValue) = getSaturationPoint(
                            change.position,
                            size
                        )
                        onSaturationValueChanged(newSaturation, newValue)
                    }
                }
            }
    ) {
        drawRoundRect(blackGradientBrush,cornerRadius = cornerRadius)
        drawRoundRect(currentColorGradientBrush,cornerRadius = cornerRadius, blendMode = BlendMode.Modulate)
        drawRoundRect(Color.Gray,cornerRadius = cornerRadius, style = Stroke(0.5.dp.toPx()))

        drawCircleSelector(currentColor)
    }
}

private fun DrawScope.drawCircleSelector(currentColor: HsvColor) {
    val radius = 6.dp
    val point = getSaturationValuePoint(currentColor, size = size,radius.toPx()+10)
    val circleStyle = Stroke(5.dp.toPx())

    drawCircle(
        color = Color.White,
        radius = radius.toPx(),
        center = point,
        style = circleStyle
    )
}

private fun getSaturationPoint(
    offset: Offset,
    size: IntSize
): Pair<Float, Float> {
    val (saturation, value) = getSaturationValueFromPosition(
        offset,
        size.toSize()
    )
    return saturation to value
}

private fun getSaturationValuePoint(color: HsvColor, size: Size, radius: Float): Offset {
    val height: Float = size.height
    val width: Float = size.width

    // 添加一个安全边距来防止圆形超出边界
    val safeMargin = radius * 2f // 假设我们想要确保圆形完全在画布内

    // 调整饱和度和亮度的映射，以便它们不会映射到画布的边缘
    val adjustedSaturation = (color.saturation * (width - safeMargin)) + (safeMargin / 2f)
    val adjustedValue = (1f - color.value) * (height - safeMargin) + (safeMargin / 2f)

    // 返回调整后的中心点
    return Offset(adjustedSaturation, adjustedValue)
}

/**
 * Given an offset and size, this function calculates a saturation and value amount based on that.
 *
 * @return new saturation and value
 */
private fun getSaturationValueFromPosition(offset: Offset, size: Size): Pair<Float, Float> {
    val width = size.width
    val height = size.height

    val newX = offset.x.coerceIn(0f, width)

    val newY = offset.y.coerceIn(0f, size.height)
    val saturation = 1f / width * newX
    val value = 1f - 1f / height * newY

    return saturation.coerceIn(0f, 1f) to value.coerceIn(0f, 1f)
}

private fun mCornerRadius(cornerRadius : Float): CornerRadius {
    return CornerRadius(cornerRadius,cornerRadius)

}
