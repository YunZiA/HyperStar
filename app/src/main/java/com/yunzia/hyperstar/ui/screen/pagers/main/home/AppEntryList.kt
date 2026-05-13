package com.yunzia.hyperstar.ui.screen.pagers.main.home

import com.yunzia.hyperstar.ui.navigation.MainRoutes
import com.yunzia.hyperstar.utils.AppInfo
import com.yunzia.hyperstar.utils.getSettingChannel

object AppEntryList {
    val entries = listOf(
        AppEntry(
            packageName = "com.android.systemui",
            route = MainRoutes.SystemUI
        ),
        AppEntry(
            packageName = "com.miui.home",
            route = MainRoutes.Home
        ) {
            getSettingChannel() > 1
        },
        AppEntry(
            packageName = "com.android.thememanager",
            route = MainRoutes.ThemeManager
        ) {
            it.versionCode >= 7180
        },
        AppEntry(
            packageName = "com.android.mms",
            route = MainRoutes.MMS
        ),
        AppEntry(
            packageName = "com.xiaomi.barrage",
            route = MainRoutes.Barrage
        ) {
            it.versionName?.startsWith("3") == true
        },
        AppEntry(
            packageName = "com.miui.screenshot",
            route = MainRoutes.Screenshot
        ) {
            getSettingChannel() > 1
        }
    )
}


fun buildVisibleCache(
    appInScope: Map<String, AppInfo?>
): Map<String, Boolean> {

    return AppEntryList.entries.associate { entry ->
        val appInfo = appInScope[entry.packageName]
        entry.packageName to (appInfo != null && entry.isVisible(appInfo))
    }

}