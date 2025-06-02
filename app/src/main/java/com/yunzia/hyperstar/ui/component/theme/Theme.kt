package com.yunzia.hyperstar.ui.component.theme

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
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
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

@SuppressLint("LocalContextConfigurationRead")
@Composable
fun BaseActivity.HyperStarTheme(
    content: @Composable () -> Unit
) {
    isDarkMode = colorMode.intValue == 2 || (isSystemInDarkTheme() && colorMode.intValue == 0)
    DisposableEffect(isDarkMode) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                { isDarkMode }
            ) ,
            navigationBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                { false }
            )
        )
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        }
        window.isNavigationBarContrastEnforced = false
        onDispose {}
    }


    return MiuixTheme(
        colors = if (isDarkMode) {
            darkColorScheme()
        }else{
            lightColorScheme()
        }
    ){

        val context = LocalContext.current
        val newContext = context.createConfigurationContext(Configuration(context.resources.configuration).apply {
            setLocale(getIndexLanguage(language.intValue))
        })
        CompositionLocalProvider(
            LocalConfiguration provides newContext.resources.configuration
        ) {
            content()
        }
    }

}