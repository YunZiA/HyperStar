package com.chaos.hyperstar
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.chaos.hyperstar.ui.base.theme.HyperStarTheme
import com.chaos.hyperstar.ui.pagers.UITest

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