package com.yunzia.hyperstar.ui.theme

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.yunzia.hyperstar.ui.component.BaseActivity
import com.yunzia.hyperstar.utils.LanguageHelper.Companion.getIndexLanguage
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

@SuppressLint("LocalContextConfigurationRead")
@Composable
fun BaseActivity.HyperStarTheme(
    content: @Composable () -> Unit
) {

    val isDark = isSystemInDarkTheme()
    DisposableEffect(colorMode.intValue) {
        isDarkMode = when(colorMode.intValue){
            2, 5 -> true
            0, 3 -> isDark
            else -> false
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ) { isDarkMode },
            navigationBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ) { isDarkMode },
        )
        window.isNavigationBarContrastEnforced = false
        onDispose {  }
    }

    val controller = when (colorMode.intValue) {
        1 -> ThemeController(ColorSchemeMode.Light)
        2 -> ThemeController(ColorSchemeMode.Dark)
        3 -> ThemeController(
            ColorSchemeMode.MonetSystem,
//            keyColor = keyColor,
            isDark = isDark
        )

        4 -> ThemeController(
            ColorSchemeMode.MonetLight,
//            keyColor = keyColor,
        )

        5 -> ThemeController(
            ColorSchemeMode.MonetDark,
//            keyColor = keyColor,
        )

        else -> ThemeController(ColorSchemeMode.System)
    }

    val context = LocalContext.current
    val newContext = context.createConfigurationContext(Configuration(context.resources.configuration).apply {
        setLocale(getIndexLanguage(language.intValue))
    })

    CompositionLocalProvider(
        LocalConfiguration provides newContext.resources.configuration
    ) {
        Log.d("HyperStarTheme", "HyperStarTheme: LocalConfiguration")
        MiuixTheme(
            controller = controller
        ){
            content()
        }
    }

}