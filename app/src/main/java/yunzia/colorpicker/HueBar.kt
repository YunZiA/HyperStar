package yunzia.colorpicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Hue side bar Component that invokes onHueChanged when the value is mutated.
 *
 * @param modifier modifiers to set to the hue bar.
 * @param currentColor the initial color to set on the hue bar.
 * @param onHueChanged the callback that is invoked when hue value changes. Hue is between 0 - 360.
 */
@Composable
internal fun HueBar(
    modifier: Modifier = Modifier,
    currentColor: HsvColor,
    onHueChanged: (Float) -> Unit
) {

    SeekBar(
        modifier = modifier,
        currentColor = currentColor, colorBrush = remember {
            Brush.horizontalGradient(getRainbowColors())
        },
        getPoint = {
            color -> getPointFromHue(color = color,)
        }
    ) { progress ->
        onHueChanged(360f * progress)

    }

}



private fun getRainbowColors(): List<Color> {
    return listOf(
        Color(0xFFFF0040),
        Color(0xFFFF00FF),
        Color(0xFF8000FF),
        Color(0xFF0000FF),
        Color(0xFF0080FF),
        Color(0xFF00FFFF),
        Color(0xFF00FF80),
        Color(0xFF00FF00),
        Color(0xFF80FF00),
        Color(0xFFFFFF00),
        Color(0xFFFF8000),
        Color(0xFFFF0000)
    )
}


private fun getPointFromHue(color: HsvColor): Float {
    return 1 - color.hue/360f
}





