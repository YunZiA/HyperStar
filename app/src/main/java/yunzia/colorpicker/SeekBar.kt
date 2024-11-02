package yunzia.colorpicker

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.utils.squircleshape.SquircleShape

val strokeThickness = 8.dp

@Composable
internal fun SeekBar (

    modifier: Modifier = Modifier,
    currentColor: HsvColor,
    colorBrush: Brush,
    backgroundDrawer:  DrawScope.() -> Unit = {},
    getPoint: (HsvColor) -> Float,
    onProgressChanged: (Float) -> Unit

) {
    val view = LocalView.current
    var hapticTriggered by remember { mutableStateOf(false) }
    val barThickness = 32.dp
    Canvas(
        modifier = modifier
            .height(barThickness)
            .fillMaxWidth()
            .clip(SquircleShape(30.dp))
            .border(0.2.dp, Color.Gray, SquircleShape(30.dp))
            .pointerInput(Unit) {

                awaitEachGesture {
                    val down = awaitFirstDown()
                    onProgressChanged(getPointFromPosition(down.position.x, size.width.toFloat()))
                    drag(down.id) { change ->
                        if (change.positionChange() != Offset.Zero) change.consume()

                        val progress = getPointFromPosition(change.position.x, size.width.toFloat()).coerceIn(0f, 1f)
                        if ((progress == 0f || progress == 1f) && !hapticTriggered){
                            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            hapticTriggered = true

                        }else if (progress != 0f && progress != 1f){
                            hapticTriggered = false
                        }
                        onProgressChanged(progress)
                    }
                }

            }
    ) {

        backgroundDrawer()
        drawRect(colorBrush)

        val huePoint = (size.width - strokeThickness.toPx()*4)*getPoint(currentColor)
        drawVerticalSelector(huePoint)

    }
}

private fun getPointFromPosition(x: Float, maxWidth: Float): Float {
    return 1 - x / maxWidth
}

internal fun DrawScope.drawVerticalSelector(amount: Float) {

    val offset =
        Offset(
            x = amount,
            y = 0f
        )

    drawSelectorIndicator(
        offset = offset,
        selectionSize = this.size.height,
        strokeThicknessPx = strokeThickness.toPx()
    )

}

internal fun DrawScope.drawSelectorIndicator(
    offset: Offset,
    selectionSize: Float,
    strokeThicknessPx: Float
) {

    val radius = selectionSize/2-strokeThicknessPx
    val circleStyle = Stroke(strokeThicknessPx)

    val point = getSaturationValuePoint(size = offset, selectionSize/ 2)

    drawCircle(
        color = Color.White,
        radius = radius,
        center = point,
        style = circleStyle
    )

}


private fun getSaturationValuePoint(size: Offset, radius: Float): Offset {

    val height: Float = size.y
    val width: Float = size.x

    // 调整饱和度和亮度的映射，以便它们不会映射到画布的边缘
    val adjustedSaturation = width + radius
    val adjustedValue =  height  + radius

    // 返回调整后的中心点
    return Offset(adjustedSaturation, adjustedValue)
}
