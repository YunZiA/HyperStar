package com.chaos.hyperstar
import androidx.activity.ComponentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.chaos.hyperstar.ui.module.ui.theme.HyperStarTheme
import com.chaos.hyperstar.ui.pagers.UITest
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

@Composable
fun App(
    activity: ComponentActivity?,
        colorMode: MutableState<Int>,
) {
    HyperStarTheme(
        colorMode = colorMode.value
    ) {
        activity?.let { UITest(it,colorMode) }
    }
}