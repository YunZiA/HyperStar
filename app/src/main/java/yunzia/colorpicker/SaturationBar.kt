package yunzia.colorpicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush


@Composable
internal fun SaturationBar(
    modifier: Modifier = Modifier,
    currentColor: HsvColor,
    onSaturationChanged: (Float) -> Unit
) {
    SeekBar(
        modifier = modifier,
        currentColor = currentColor, colorBrush = remember(currentColor) {
            Brush.horizontalGradient(
                listOf(
                    currentColor.copy(saturation = 1.0f, value = 1.0f, alpha = 1.0f).toColor(),
                    currentColor.copy(saturation = 0.0f, value = 1.0f, alpha = 1.0f).toColor(),

                )
            )
        },
        getPoint = {
            color -> getPositionFromSaturation(color)
        }
    ) { progress ->
        onSaturationChanged(progress)

    }
}

private fun getPositionFromSaturation(color: HsvColor): Float {

    return 1 - color.saturation
}




