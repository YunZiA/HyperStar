package com.chaos.hyperstar.ui.module.controlcenter

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.chaos.hyperstar.ui.module.controlcenter.ui.ControlCenterPager
import com.chaos.hyperstar.ui.module.ui.theme.HyperStarTheme
import com.chaos.hyperstar.utils.PreferencesUtil

class ControlCenterSettings : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val colorMode = remember { mutableIntStateOf(PreferencesUtil.getInt("color_mode",0)) }
            val darkMode = colorMode.intValue == 2 || (isSystemInDarkTheme() && colorMode.intValue == 0)
            DisposableEffect(darkMode) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkMode },
                    navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkMode },)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false // Xiaomi moment, this code must be here
                }
                onDispose {}
            }
            HyperStarTheme(colorMode.intValue) {
                Greeting(this)
            }
        }
    }


}

@Composable
private fun Greeting(
    activity: ComponentActivity?
) {
    activity?.let { ControlCenterPager(it) }
}

@Preview(showBackground = true)
@Composable
private fun GreetingPreview() {
    HyperStarTheme {
        Greeting(null)
    }
}