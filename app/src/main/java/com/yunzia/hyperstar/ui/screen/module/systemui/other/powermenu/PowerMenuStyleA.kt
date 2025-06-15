package com.yunzia.hyperstar.ui.screen.module.systemui.other.powermenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.AutoSizeText

@Composable
fun BoxWithConstraintsScope.PowerMenuStyleA(
    titleSize: MutableState<TextUnit>
) {
    val height = this.maxHeight
    val width = this.maxWidth
    val smallWidth = width * 0.28f
    val smallHeight = height * 0.06f
    val smallFontSize = remember { mutableStateOf(12.sp) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        ExpandButton(
            width = smallWidth,
            height = smallHeight,
            text = "Recovery",
            fontSize = smallFontSize
        )

        Spacer(Modifier.weight(1f))

        SliderBarPreview(
            text = stringResource(R.string.menu_style_1),
            fontSize = titleSize,
            maxWidth = width,
            maxHeight = height
        )

        Spacer(Modifier.weight(1f))

        ExpandButton(
            width = smallWidth,
            height = smallHeight,
            text = "Bootloader",
            fontSize = smallFontSize
        )

    }
}


@Composable
private fun ExpandButton(
    width: Dp,
    height: Dp,
    fontSize: MutableState<TextUnit>,
    text: String
){
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xA099DAF0)),
        contentAlignment = Alignment.Center
    ) {
        AutoSizeText(
            text = text,
            modifier = Modifier.padding(horizontal = 5.dp),
            fontSize = fontSize,
            fontWeight = FontWeight(450),
            color = Color(0XffCCF4FB)
        )

    }

}