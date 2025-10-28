package com.yunzia.hyperstar.ui.component

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedService.OnScopeEventListener
import io.github.libxposed.service.XposedServiceHelper
import com.yunzia.hyperstar.ui.theme.HyperStarTheme
import com.yunzia.hyperstar.utils.LanguageHelper
import com.yunzia.hyperstar.utils.PreferencesUtil
import kotlin.system.exitProcess


abstract class BaseActivity : ComponentActivity() {

    private var mService: XposedService? = null

    private val mCallback = object : OnScopeEventListener {
        override fun onScopeRequestPrompted(packageName: String) {
            runCatching {
                Toast.makeText(this@BaseActivity, "onScopeRequestPrompted: $packageName", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onScopeRequestApproved(packageName: String) {
            runCatching {
                Toast.makeText(this@BaseActivity, "onScopeRequestApproved: $packageName", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onScopeRequestDenied(packageName: String) {
            runCatching {
                Toast.makeText(this@BaseActivity, "onScopeRequestDenied: $packageName", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onScopeRequestTimeout(packageName: String) {
            runCatching {
                Toast.makeText(this@BaseActivity, "onScopeRequestTimeout: $packageName", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onScopeRequestFailed(packageName: String, message: String) {
            runCatching {
                Toast.makeText(this@BaseActivity, "onScopeRequestFailed: $packageName, $message", Toast.LENGTH_SHORT).show()
            }
        }
    }
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

        var serviceInfo = ""

        XposedServiceHelper.registerListener(object : XposedServiceHelper.OnServiceListener {
            override fun onServiceBind(service: XposedService) {
                //Log.d("ggc", "onServiceBind: \n$service")
                mService = service
                serviceInfo += "Binder acquired"
                serviceInfo += "\nAPI " + service.apiVersion
                serviceInfo += "\nFramework " + service.frameworkName
                serviceInfo += "\nFramework version " + service.frameworkVersion
                serviceInfo += "\nFramework version code " + service.frameworkVersionCode
                serviceInfo += "\nScope: " + service.scope
                Log.d("ggc", "onServiceBind: \n$serviceInfo")

//                binding.requestScope.setOnClickListener {
//                    service.requestScope("com.android.settings", mCallback)
//                }
//                binding.randomPrefs.setOnClickListener {
//                    val prefs = service.getRemotePreferences("test")
//                    val old = prefs.getInt("test", -1)
//                    val new = Random.nextInt()
//                    Toast.makeText(this@BaseActivity, "$old -> $new", Toast.LENGTH_SHORT).show()
//                    prefs.edit().putInt("test", new).apply()
//                }
//                binding.remoteFile.setOnClickListener {
//                    service.openRemoteFile("test.txt").use { pfd ->
//                        FileWriter(pfd.fileDescriptor).use {
//                            it.append("Hello World!")
//                        }
//                    }
//                }
            }

            override fun onServiceDied(service: XposedService) {
                Log.d("ggc", "onServiceDied: \n$service")

            }
        })

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            if (mService == null) {
                Log.d("BaseActivity", "onCreate: Binder is null")
            }
        }, 5000)
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


