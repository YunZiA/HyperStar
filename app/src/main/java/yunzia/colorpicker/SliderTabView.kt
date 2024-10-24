package yunzia.colorpicker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.R
import top.yukonga.miuix.kmp.basic.Text

@Composable
fun SliderTabView(
    modifier: Modifier = Modifier,
    color: MutableState<HsvColor>,
    onColorChanged: (HsvColor) -> Unit
) {

    val paddingBetweenBars = 12.dp
    val updatedOnColorChanged by rememberUpdatedState(onColorChanged)
    Column() {
        Text(
            text = stringResource(R.string.Hue),
            color = Color(0xFF74777F),
            fontSize = TextUnit(13f, TextUnitType.Sp),
            modifier = Modifier.padding(bottom = paddingBetweenBars)
        )
        HueBar(
            modifier = Modifier,
            currentColor = color.value,
            onHueChanged = { newHue ->
                updatedOnColorChanged(color.value.copy(hue = newHue))
            }
        )
        Text(
            text = stringResource(R.string.Saturation),
            color = Color(0xFF74777F),
            fontSize = TextUnit(13f, TextUnitType.Sp),
            modifier = Modifier.padding(vertical = paddingBetweenBars)
        )
        SaturationBar(
            modifier = Modifier,
            currentColor = color.value,
            onSaturationChanged = { saturation ->
                updatedOnColorChanged(color.value.copy(saturation = saturation))
            }
        )
        Text(
            text = stringResource(R.string.Lightness),
            color = Color(0xFF74777F),
            fontSize = TextUnit(13f, TextUnitType.Sp),
            modifier = Modifier.padding(vertical = paddingBetweenBars)
        )
        LightnessBar(
            modifier = Modifier,
            currentColor = color.value,
            onLightnessChanged = { lightness ->
                updatedOnColorChanged(color.value.copy(value = lightness))
            }
        )
    }
}