package com.yunzia.hyperstar.ui.screen.pagers.main.home

import com.yunzia.hyperstar.ui.navigation.Route
import com.yunzia.hyperstar.utils.AppInfo

data class AppEntry(
    val packageName: String,
    val route: Route,
    val visible: (AppInfo) -> Boolean = { true }
) {
    fun isVisible(appInfo: AppInfo): Boolean = visible(appInfo)
}