package yunzia.colorpicker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.R
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme


@Composable
fun ClassicColorPicker(
    modifier: Modifier = Modifier,
    color: MutableState<HsvColor>,
    onColorChanged: (HsvColor) -> Unit
) {

    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState(pageCount = { 2 })

    val updatedOnColorChanged by rememberUpdatedState(onColorChanged)

    Column(modifier = modifier) {
        TabCard(
            Modifier
                .height(65.dp)
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            listOf(stringResource(R.string.Grid), stringResource(R.string.Slider)),
            pagerState.currentPage,
            tabColor = colorScheme.surfaceVariant,
            tabBgColor = colorResource(R.color.tab_bg_color),
            titleColor = colorScheme.onSurfaceSecondary,
            titleSelectColor = colorScheme.onSurface

        ){
            coroutineScope.launch {
                pagerState.animateScrollToPage(it)
            }
        }
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .height(215.dp),
            state = pagerState,
            userScrollEnabled = false
        ){ page ->
            when (page) {
                0 ->{
                    GridTabView(
                        modifier = Modifier,
                        color = color
                    ) {
                        color.value = it
                        updatedOnColorChanged(color.value)
                    }
                }
                1 ->{
                    SliderTabView(
                        modifier = Modifier,
                        color = color
                    ) {
                        color.value = it
                        updatedOnColorChanged(color.value)
                    }
                }
            }

        }


        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                //.padding(bottom = 10.dp)
        ) {
            Text(
                text = stringResource(R.string.Opacity),
                color = Color(0xFF74777F),
                fontSize = TextUnit(13f, TextUnitType.Sp),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${(color.value.alpha*100).toInt()}%",
                color = Color(0xFF74777F),
                fontSize = TextUnit(13f, TextUnitType.Sp),
            )
        }
        AlphaBar(
            modifier = Modifier,
            currentColor = color.value,
            onAlphaChanged = { alpha ->
                color.value = color.value.copy(alpha = alpha)
                updatedOnColorChanged(color.value)
            }
        )

    }


}
