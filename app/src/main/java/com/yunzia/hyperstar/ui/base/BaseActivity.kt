package com.yunzia.hyperstar.ui.base

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.yunzia.hyperstar.ui.base.theme.HyperStarTheme
import com.yunzia.hyperstar.utils.LanguageHelper
import com.yunzia.hyperstar.utils.LanguageHelper.Companion.getIndexLanguage
import com.yunzia.hyperstar.utils.PreferencesUtil

val colorMode = mutableIntStateOf(0)
val showFPSMonitor =  mutableStateOf(false)

abstract class BaseActivity : ComponentActivity() {


    @Composable abstract fun InitView()

    @Composable abstract fun InitData(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            InitData(savedInstanceState)
            colorMode.intValue = PreferencesUtil.getInt("color_mode",0)
            showFPSMonitor.value = PreferencesUtil.getBoolean("show_FPS_Monitor",false)
//
            val darkMode = colorMode.intValue == 2 || (isSystemInDarkTheme() && colorMode.intValue == 0)

            val systemUiController = rememberSystemUiController()

            SideEffect {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = !darkMode,
                    isNavigationBarContrastEnforced = false
                )

            }

            HyperStarTheme(colorMode.intValue) {
                InitView()
            }

        }
    }




    override fun attachBaseContext(newBase: Context?) {
        PreferencesUtil.getInstance().init(newBase)
        if(newBase == null) {
            super.attachBaseContext(newBase)
            return
        }
        super.attachBaseContext(LanguageHelper.wrap(newBase));

    }

    override fun onStart() {
        super.onStart()
        val res = this.resources;
        val configuration = res.configuration;
        val metrics: DisplayMetrics = res.displayMetrics
        val index = PreferencesUtil.getInt("app_language",0)

        configuration.setLocale(getIndexLanguage(index))
        res.updateConfiguration(configuration,metrics)

    }


}


