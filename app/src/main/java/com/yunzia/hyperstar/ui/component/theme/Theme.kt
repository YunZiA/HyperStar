package com.yunzia.hyperstar.ui.component.theme

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.yunzia.hyperstar.ui.component.BaseActivity
import com.yunzia.hyperstar.utils.LanguageHelper.Companion.getIndexLanguage
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

@SuppressLint("LocalContextConfigurationRead")
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
        }
    ){

        val context = LocalContext.current
        val newContext = context.createConfigurationContext(Configuration(context.resources.configuration).apply {
            setLocale(getIndexLanguage(activity.language.intValue))
        })
        CompositionLocalProvider(
            LocalConfiguration provides newContext.resources.configuration
        ) {

            content()
        }
    }

}