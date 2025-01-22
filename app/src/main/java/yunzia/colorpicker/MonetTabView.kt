package yunzia.colorpicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier

@Composable
fun MonetTabView(
    modifier: Modifier = Modifier,
    color: MutableState<HsvColor>,
    onColorChanged: (HsvColor) -> Unit

) {

    val updatedOnColorChanged by rememberUpdatedState(onColorChanged)

}