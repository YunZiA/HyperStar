package yunzia.colorpicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import kotlin.math.ceil


@Composable
internal fun AlphaBar(
    modifier: Modifier = Modifier,
    currentColor: HsvColor,
    onAlphaChanged: (Float) -> Unit
) {
    SeekBar(
        modifier = modifier,
        backgroundDrawer =  {
            clipRect {
                drawCheckeredBackground()
            }
        },
        currentColor = currentColor, colorBrush = remember(currentColor) {
            Brush.horizontalGradient(
                listOf(
                    currentColor.copy(alpha = 1.0f).toColor(),
                    Color(0x00ffffff)
                )
            )
        },
        getPoint = {
            color ->  getPositionFromAlpha(color)
        }
    ) { progress ->
        onAlphaChanged(progress)

    }
}



fun DrawScope.drawCheckeredBackground() {
    val darkColor = Color.LightGray
    val lightColor = Color.White
    val gridSizePx = 4.dp.toPx()
    val cellCountX = ceil(this.size.width / gridSizePx).toInt()
    val cellCountY = ceil(this.size.height / gridSizePx).toInt()

    // 缓存颜色交替
    val colors = listOf(darkColor, lightColor)

    repeat(cellCountX) { i ->
        repeat(cellCountY) { j ->
            val color = colors[(i + j) % 2]
            with(color) {
                drawRect(
                    color = this,
                    topLeft = Offset(i * gridSizePx, j * gridSizePx),
                    size = Size(gridSizePx, gridSizePx)
                )
            }
        }
    }
}

private fun getPositionFromAlpha(color: HsvColor): Float {
    return 1 - color.alpha
}



