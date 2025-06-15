package com.yunzia.hyperstar.ui.screen.module.systemui.other.powermenu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.R

@Composable
fun BoxWithConstraintsScope.PowerMenuStyleDefault(
    titleSize: MutableState<TextUnit>
) {
    val height = this.maxHeight
    val width = this.maxWidth
    Row(
        modifier = Modifier.fillMaxSize().padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        SliderBarPreview(
            text = stringResource(R.string.default_it),
            fontSize = titleSize,
            maxWidth = width,
            maxHeight = height
        )

    }


}
