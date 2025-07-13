package com.yunzia.hyperstar.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.yunzia.hyperstar.ui.component.dialog.SuperNotificationUtil.SuperNotificationHost
import top.yukonga.miuix.kmp.basic.FabPosition
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtils.Companion.MiuixPopupHost

@Composable
fun XScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    floatingPagerButton: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    popupHost: @Composable () -> Unit = {
        MiuixPopupHost()
        SuperNotificationHost()
        },
    containerColor: Color = MiuixTheme.colorScheme.background,
    contentWindowInsets: WindowInsets = WindowInsets.statusBars,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        snackbarHost = snackbarHost,
        popupHost = popupHost,
        containerColor = containerColor,
        contentWindowInsets = contentWindowInsets,
        content = content
    )
    floatingPagerButton()



}