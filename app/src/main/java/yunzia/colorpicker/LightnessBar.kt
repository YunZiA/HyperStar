package yunzia.colorpicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush


@Composable
internal fun LightnessBar(
    modifier: Modifier = Modifier,
    currentColor: HsvColor,
    onLightnessChanged: (Float) -> Unit
) {
    SeekBar(
        modifier = modifier,
        currentColor = currentColor, colorBrush = remember(currentColor) {
            Brush.horizontalGradient(
                listOf(
                    currentColor.copy(saturation = 1.0f , value = 1.0f, alpha = 1.0f).toColor(),
                    currentColor.copy(saturation = 1.0f , value = 0.0f, alpha = 1.0f).toColor(),
                )
            )
        },
        getPoint = {
            color -> getPositionFromLightness(color = color)
        }
    ) { progress ->
        onLightnessChanged(progress)

    }
}

private fun getPositionFromLightness(color: HsvColor): Float {

    return 1 - color.value
}





