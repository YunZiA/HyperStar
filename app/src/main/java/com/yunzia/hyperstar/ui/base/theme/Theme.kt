package com.yunzia.hyperstar.ui.base.theme

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import com.yunzia.hyperstar.ui.base.BaseActivity
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

@Composable
fun HyperStarTheme(
    content: @Composable () -> Unit
) {

    val activity = LocalActivity.current as BaseActivity

    return MiuixTheme(
        colors = if (activity.isDarkMode) {
            darkColorScheme()
        }else{
            lightColorScheme()
             },
        content = content
    )

}