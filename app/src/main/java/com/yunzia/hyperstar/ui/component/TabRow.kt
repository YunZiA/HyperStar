package com.yunzia.hyperstar.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.basic.TabRowDefaults
import top.yukonga.miuix.kmp.basic.TabRowWithContour
import kotlin.String

@Composable
fun TabRow(
    modifier: Modifier = Modifier,
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
) {

    TabRowWithContour(
        tabs = tabs,
        selectedTabIndex = selectedTabIndex,
        onTabSelected = onTabSelected,
        modifier = modifier,
        colors = TabRowDefaults.tabRowColors(
            backgroundColor = MiuixTheme.colorScheme.surfaceContainerHigh,
            selectedBackgroundColor = MiuixTheme.colorScheme.background
        ),
        itemSpacing = 9.dp,
        contentAlignment = Alignment.Center,
    )

}
