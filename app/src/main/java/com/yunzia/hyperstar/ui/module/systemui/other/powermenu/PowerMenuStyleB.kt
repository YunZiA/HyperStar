package com.yunzia.hyperstar.ui.module.systemui.other.powermenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
fun PowerMenuStyleB() {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val items=(1..100).toList()
        LazyVerticalGrid(
            modifier = Modifier.padding(horizontal = 20.dp),
            columns = GridCells.Fixed(4),
            userScrollEnabled = false,
            contentPadding =  PaddingValues(10.dp),

            ) {
            items(4){
                Box(modifier = Modifier
                    .size(70.dp),
                    contentAlignment = Alignment.Center
                ){
                    Box(modifier = Modifier
                        .size(70.dp) // 设置为正方形大小
                        .clip(RoundedCornerShape(70.dp))
                        .background(colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ){
                        Text(text = it.toString())
                    }
                }
            }

        }
        Box(
            modifier = Modifier.width(80.dp)
                .height(300.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(colorScheme.secondary),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "样式2")

        }
        LazyVerticalGrid(
            modifier = Modifier.padding(horizontal = 20.dp),
            columns = GridCells.Fixed(4),
            userScrollEnabled = false,
            contentPadding =  PaddingValues(10.dp),

            ) {
            items(4){
                Box(modifier = Modifier
                    .size(70.dp),
                    contentAlignment = Alignment.Center
                ){
                    Box(modifier = Modifier
                        .size(70.dp) // 设置为正方形大小
                        .clip(RoundedCornerShape(70.dp))
                        .background(colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ){
                        Text(text = it.toString())
                    }
                }
            }

        }

    }

}