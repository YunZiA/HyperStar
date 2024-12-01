package com.yunzia.hyperstar.ui.module.systemui.other.powermenu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunzia.hyperstar.R
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
fun PowerMenuStyleB() {

    Column(
        modifier = Modifier.height(488.dp).width(218.dp).bg(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val items=(1..100).toList()
        LazyVerticalGrid(
            modifier = Modifier.padding(horizontal = 20.dp),
            columns = GridCells.Fixed(4),
            userScrollEnabled = false
        ) {
            items(4){
                Box(modifier = Modifier
                    .size(40.dp),
                    contentAlignment = Alignment.Center
                ){
                    Box(modifier = Modifier
                        .size(40.dp) // 设置为正方形大小
                        .clip(RoundedCornerShape(40.dp))
                        .background(colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ){
                        Text(text = it.toString(),fontSize = 13.sp)
                    }
                }
            }

        }
        Box(
            modifier = Modifier.width(60.dp)
                .padding(vertical = 10.dp)
                .height(235.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(colorScheme.secondary),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(R.string.menu_style_2))

        }
        LazyVerticalGrid(
            modifier = Modifier.padding(horizontal = 20.dp),
            columns = GridCells.Fixed(4),
            userScrollEnabled = false
        ) {
            items(4){
                Box(modifier = Modifier
                    .size(40.dp),
                    contentAlignment = Alignment.Center
                ){
                    Box(modifier = Modifier
                        .size(40.dp) // 设置为正方形大小
                        .clip(RoundedCornerShape(40.dp))
                        .background(colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ){
                        Text(text = (it+4).toString(),fontSize = 13.sp)
                    }
                }
            }

        }

    }

}