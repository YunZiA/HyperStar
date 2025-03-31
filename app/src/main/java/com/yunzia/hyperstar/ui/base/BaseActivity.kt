package com.yunzia.hyperstar.ui.base

import android.content.Context
import android.content.Intent
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.yunzia.hyperstar.ui.base.theme.HyperStarTheme
import com.yunzia.hyperstar.utils.LanguageHelper
import com.yunzia.hyperstar.utils.PreferencesUtil
import kotlin.system.exitProcess


abstract class BaseActivity : ComponentActivity() {
    val colorMode by lazy {
        mutableIntStateOf(PreferencesUtil.getInt("color_mode",0))
    }
    val showFPSMonitor by lazy {
        mutableStateOf(PreferencesUtil.getBoolean("show_FPS_Monitor",false))
    }

    val language by lazy {
        mutableIntStateOf(PreferencesUtil.getInt("app_language",0))
    }
    var isDarkMode by mutableStateOf(false)
    var updateUI by mutableIntStateOf(0)

    @Composable abstract fun InitView()

    @Composable abstract fun InitData(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

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


            HyperStarTheme() {
                InitData(savedInstanceState)
                InitView()
            }
        }


    }


    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        PreferencesUtil.getInstance().init(newBase)

    }

    override fun onStart() {
        super.onStart()
        setLocale()

    }

    fun updateUI(){
        updateUI += 1
        setLocale()
    }

    fun setLocale(
        index:Int = PreferencesUtil.getInt("app_language",0)
    ){
        val res = this.resources;
        val configuration = res.configuration;
        val metrics = res.displayMetrics

        configuration.setLocale(LanguageHelper.getIndexLanguage(index))
        res.updateConfiguration(configuration,metrics)
    }

    fun reStart() {
        val intent = this.packageManager.getLaunchIntentForPackage(this.packageName)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        this.startActivity(intent)
        exitProcess(0)
    }




}


