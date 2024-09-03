package com.chaos.hyperstar.ui.module.volume

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.chaos.hyperstar.ui.module.ui.theme.HyperStarTheme
import com.chaos.hyperstar.utils.PreferencesUtil

class VolumeSettings : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val colorMode = remember { mutableIntStateOf(PreferencesUtil.getInt("color_mode",0)) }
            DisposableEffect(isSystemInDarkTheme()) {
                enableEdgeToEdge()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false // Xiaomi moment, this code must be here
                }
                onDispose {}
            }
            HyperStarTheme(colorMode = colorMode.intValue) {
                Greeting(this)
            }
        }
    }


}

@Composable
fun Greeting(activity: ComponentActivity?) {
    activity?.let { VolumePager(it) }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    HyperStarTheme {
        Greeting(null)
    }
}