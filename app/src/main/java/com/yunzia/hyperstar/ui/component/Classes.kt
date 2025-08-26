package com.yunzia.hyperstar.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardColors
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

fun LazyListScope.emptyClasses(
    top : Dp = 0.dp,
    bottom : Dp = 0.dp,
    content: @Composable (() -> Unit),
){
    item{
        Card(
            Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = top, bottom = bottom)
        ) {
            content()
        }
    }
}

fun LazyListScope.itemGroup(
    title : Any ? = null,
    position: SuperGroupPosition = SuperGroupPosition.DEFAULT,
    content: @Composable (ColumnScope.() -> Unit)
){

    item{
        SuperGroup(
            title =  when (title){
                is String -> title
                is Int -> stringResource(id = title)
                else -> null
            },
            position = position,
            content = content
        )
    }

}