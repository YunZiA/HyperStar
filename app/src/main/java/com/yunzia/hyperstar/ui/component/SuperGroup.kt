package com.yunzia.hyperstar.ui.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardColors
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun SuperGroup(
    title: String? = null,
    modifier: Modifier = Modifier,
    position: SuperGroupPosition = SuperGroupPosition.DEFAULT,
    titleColor: Color = MiuixTheme.colorScheme.onBackgroundVariant,
    cardColor: CardColors = CardDefaults.defaultColors(),
    content: @Composable ColumnScope.() -> Unit
) {
    val cardTopSpace = if (position == SuperGroupPosition.FIRST) 12.dp else 6.dp

    Spacer(modifier= Modifier.fillMaxWidth().height(cardTopSpace))
    title?.let {
        SmallTitle(
            text = it,
            textColor = titleColor
        )
    }
    val cardBottomPadding = if (position == SuperGroupPosition.LAST) 12.dp else 6.dp
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = cardBottomPadding),
        colors = cardColor,
        content = content
    )
}

enum class SuperGroupPosition{
    FIRST,DEFAULT,LAST
}