package com.yunzia.hyperstar.ui.base

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.yunzia.hyperstar.ui.base.dialog.SuperXPopupUtil.Companion.SuperXPopupHost
import com.yunzia.hyperstar.ui.base.dialog.CTPopupUtil.Companion.CTPopupHost
import com.yunzia.hyperstar.ui.base.dialog.SuperNotificationUtil.Companion.SuperNotificationHost
import top.yukonga.miuix.kmp.basic.MiuixFabPosition
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.MiuixPopupHost

@Composable
fun XScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: MiuixFabPosition = MiuixFabPosition.End,
    snackbarHost: @Composable () -> Unit = {},
    popupHost: @Composable () -> Unit = {
        SuperNotificationHost()
        MiuixPopupHost()
        CTPopupHost()
        SuperXPopupHost()

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


}