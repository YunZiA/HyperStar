package com.yunzia.hyperstar.ui.module.systemui.other

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.SystemUIMoreList
import com.yunzia.hyperstar.ui.base.pager.ModuleNavPagers
import com.yunzia.hyperstar.ui.base.SuperNavHostArrow
import com.yunzia.hyperstar.ui.base.XSuperSwitch
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.utils.Helper

@Composable
fun SystemUIOtherPager(
    navController: NavHostController,
    currentStartDestination: MutableState<String>
) {
    ModuleNavPagers(
        activityTitle = stringResource(R.string.more),
        navController = navController,
        parentRoute = currentStartDestination,
        endClick = {
            Helper.rootShell("killall com.android.systemui")
        },
    ) {
        firstClasses(
            title = R.string.status_bar,
        ) {
            XSuperSwitch(
                title = stringResource(R.string.transparent_statusBar_background),
                summary = stringResource(R.string.transparent_statusBar_background_summary),
                key = "is_transparent_statusBar_background"
            )
        }
        classes(
            title = R.string.navigation_bar,
        ) {
            XSuperSwitch(
                title = stringResource(R.string.transparent_navigationBar_background),
                summary = stringResource(R.string.transparent_statusBar_background_summary),
                key = "is_transparent_navigationBar_background"
            )
        }
        classes(
            title = "经典通知样式"
        ) {
            SuperNavHostArrow(
                title = "图标优化白名单",
                navController = navController,
                route = SystemUIMoreList.NOTIFICATIONOFIM

            )

        }
        classes(
            title = R.string.power_menu,
        ) {
            XSuperSwitch(
                title = stringResource(R.string.is_power_menu_nav_show_title),
                key = "is_power_menu_nav_show"
            )
            SuperNavHostArrow(
                title = stringResource(R.string.power_menu_extra),
                navController = navController,
                route = SystemUIMoreList.POWERMENU

            )
        }
    }
}