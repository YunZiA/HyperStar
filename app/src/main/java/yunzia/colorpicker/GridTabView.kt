package yunzia.colorpicker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
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
fun GridTabView(
    modifier: Modifier = Modifier,
    color: MutableState<HsvColor>,
    onColorChanged: (HsvColor) -> Unit

) {

    val updatedOnColorChanged by rememberUpdatedState(onColorChanged)

    Column() {
        Text(
            text = stringResource(R.string.Hue),
            color = Color(0xFF74777F),
            fontSize = TextUnit(13f, TextUnitType.Sp),
            modifier = Modifier.padding(bottom = 14.dp, top = 2.5.dp)
        )

        ColorGrid(
            modifier = Modifier
                .height(200.dp),
            color = color.value.copy(alpha = 1f).toColor()

        ){
            //color.value.copy(saturation = saturation)
            updatedOnColorChanged(color.value.copy(hue = it.hue, saturation = it.saturation, value = it.value))
        }
    }
}