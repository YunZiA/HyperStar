package com.yunzia.hyperstar.ui.module.systemui.other.powermenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.component.AutoSizeText

@Composable
fun SliderBarPreview(
    maxWidth: Dp,
    maxHeight: Dp,
    text: String,
    fontSize: MutableState<TextUnit>
) {
    Box(
        modifier = Modifier
            .width(maxWidth * 0.25f)
            .height(maxHeight * 0.48f)
            .clip(RoundedCornerShape(40.dp))
            .background(Color(0xA099DAF0)),
        contentAlignment = Alignment.Center
    ) {
        AutoSizeText(
            modifier = Modifier.padding(horizontal = 5.dp),
            text = text,
            fontSize = fontSize,
            color = Color(0XffCCF4FB)
        )

    }

}
