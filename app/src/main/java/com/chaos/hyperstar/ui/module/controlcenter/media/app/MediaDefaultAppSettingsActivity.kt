package com.chaos.hyperstar.ui.module.controlcenter.media.app

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

class MediaDefaultAppSettingsActivity : ComponentActivity() {

    var appList: ArrayList<AppInfo>? = null

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

       // appList = getAllAppInfo(this,true)


    }

    @SuppressLint("QueryPermissionsNeeded")
    fun getAllAppInfo(ctx: Context, isFilterSystem: Boolean): ArrayList<AppInfo> {
        val appBeanList: ArrayList<AppInfo> = ArrayList<AppInfo>()
        val packageManager = ctx.packageManager
        val list = packageManager.getInstalledPackages(0)

        //appListDB.resetTable()

        for (p in list) {
            val applicationInfo = p.applicationInfo


            // 检查是否是系统应用以及是否应该过滤系统应用
            val isSystemApp =
                isFilterSystem && ((applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0)

            // 检查是否是特定的应用包名
            if ("com.miui.player" == applicationInfo.packageName) {
                processAppInfo(applicationInfo, packageManager, appBeanList)
            } else if (!isSystemApp) {  // 非系统应用
                processAppInfo(applicationInfo, packageManager, appBeanList)
            }
        }

        return appBeanList
    }

    fun processAppInfo(applicationInfo: ApplicationInfo?, packageManager: PackageManager?, appBeanList: ArrayList<AppInfo>) {
        run {
            val app_name = applicationInfo?.let { packageManager?.getApplicationLabel(it).toString() }
            val package_name = applicationInfo?.packageName
            val app_icon = applicationInfo?.let { packageManager?.getApplicationIcon(it) }

            val bean = AppInfo()
            bean.label = app_name.toString()
            bean.package_name = package_name.toString()
            bean.icon = app_icon

            val values = ContentValues()
            values.put("package_name", package_name)
            values.put("app_name", app_name)

            appBeanList.add(bean)

            values.clear()
        }
    }
}

@Composable
private fun Greeting(activity: MediaDefaultAppSettingsActivity?) {
    if (activity != null) {
        MediaSettingsPager(activity)
    }

}

@Preview(showBackground = true)
@Composable
private fun GreetingPreview() {
    HyperStarTheme {
        Greeting(null)
    }
}