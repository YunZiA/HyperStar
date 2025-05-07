package com.yunzia.hyperstar.ui.module.systemui.other.powermenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.AutoSizeText

@Composable
fun BoxWithConstraintsScope.PowerMenuStyleB(
    titleSize: MutableState<TextUnit>
) {
    val height = this.maxHeight
    val width = this.maxWidth
    val smallSize = width * 0.19f
    val space = smallSize*0.4f

    val numList = (0..7).toList()

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(15.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ExpandButton(
            smallSize,
            numList.subList(0,4)
        )

        Spacer(
            modifier = Modifier.height(space)
        )

        SliderBarPreview(
            text = stringResource(R.string.menu_style_2),
            fontSize = titleSize,
            maxWidth = width,
            maxHeight = height
        )

        Spacer(
            modifier = Modifier.height(space)
        )

        ExpandButton(
            smallSize,
            numList.subList(4,8)
        )

    }

}

@Composable
private fun ExpandButton(
    size: Dp,
    numList: List<Int>
){

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        userScrollEnabled = false
    ) {
        numList.forEach { index ->
            item(index) {
                Box(
                    modifier = Modifier.size(size),
                    contentAlignment = Alignment.Center
                ){
                    Box(
                        modifier = Modifier
                            .size(size)
                            .clip(RoundedCornerShape(size))
                            .background(Color(0xA099DAF0)),
                        contentAlignment = Alignment.Center
                    ){
                        AutoSizeText(
                            text = index.toString(),
                            fontSize = 13.sp,
                            color = Color(0XffCCF4FB)
                        )
                    }
                }

            }
        }

    }


}