package com.chaos.hyperstar.ui.module.systemui.controlcenter.media.app

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.chaos.hyperstar.ui.base.BaseActivity

class MediaDefaultAppSettingsActivity : BaseActivity() {

    var appList: ArrayList<AppInfo>? = null

    var appListDB : AppListDB? = null

    var appIconlist = mutableMapOf<String, Drawable>()

    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        MediaSettingsPager(this)
    }

    override fun initData(savedInstanceState: Bundle?) {
        appListDB = AppListDB(this)
    }


    fun searchApp(label: String): ArrayList<AppInfo>? {
        var result: ArrayList<AppInfo>? = null
        Thread {
            result = appListDB?.searchAPPlist(label,appIconlist)
        }.start()

        return appListDB?.searchAPPlist(label,appIconlist)
    }


    @SuppressLint("QueryPermissionsNeeded")
    fun getAllAppInfo( isFilterSystem: Boolean): ArrayList<AppInfo> {
        val appBeanList: ArrayList<AppInfo> = ArrayList<AppInfo>()
        val packageManager = this.packageManager
        val list = packageManager.getInstalledPackages(0)

        appListDB?.resetTable()

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

            appIconlist.plus(package_name to app_icon)
            if (app_icon != null && package_name != null) {
                appIconlist.put(package_name,app_icon)
                if (appIconlist[package_name] == null){
                    Log.d("ggc","appIconlist[package_name]  == null")

                }
            }


            appBeanList.add(bean)
            appListDB?.add(values)

            values.clear()
        }
    }
}
