package com.yunzia.hyperstar.ui.component

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.yunzia.hyperstar.ui.theme.HyperStarTheme
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
        isDarkMode = colorMode.intValue == 2 || (isNightMode() && colorMode.intValue == 0)


    }

    fun isNightMode(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    fun updateUI(){
        updateUI += 1
    }

    fun setLocale(
        index:Int = PreferencesUtil.getInt("app_language",0)
    ){
        val res = this.resources;
        val metrics = res.displayMetrics
        val configuration = res.configuration.apply {
            setLocale(LanguageHelper.getIndexLanguage(index))
        }

        res.updateConfiguration(configuration,metrics)
    }

    fun reStart() {
        val intent = this.packageManager.getLaunchIntentForPackage(this.packageName)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        this.startActivity(intent)
        exitProcess(0)
    }




}


