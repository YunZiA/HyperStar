package com.yunzia.hyperstar.ui.module.systemui.other.powermenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
fun PowerMenuStyleA() {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier.width(100.dp)
                .height(50.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(colorScheme.secondary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Recovery",
                fontWeight = FontWeight(450),
                fontSize = 11.sp
                )

        }

        Box(
            modifier = Modifier.width(100.dp)
                .padding(horizontal = 10.dp)
                .height(300.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(colorScheme.secondary),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "样式1")

        }

        Box(
            modifier = Modifier.width(100.dp)
                .height(50.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(colorScheme.secondary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Bootloader",
                fontWeight = FontWeight(450),
                fontSize = 11.sp
            )

        }


    }
}