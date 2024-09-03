package com.chaos.hyperstar.ui.base

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaos.hyperstar.R
import top.yukonga.miuix.kmp.HorizontalDivider
import top.yukonga.miuix.kmp.basic.MiuixText

@Composable
fun XMiuixClass(
    title : String,
    bottom : Dp = 0.dp,
    useLine : Boolean
){
    if (useLine){
        HorizontalDivider(
            modifier = Modifier
                .padding(horizontal = 27.dp).padding(top = 18.dp, bottom = 12.dp),
            thickness = 0.8.dp,
            color = colorResource(R.color.sec_color)
        )

    }
    MiuixText(
        text = title,
        modifier = Modifier.padding(horizontal = 28.dp).padding(top = 14.dp, bottom = 6.dp+bottom),
        fontSize = 13.sp,
        color = colorResource(R.color.class_name_color),
        fontWeight = FontWeight.Medium
    )
}