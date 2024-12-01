package com.yunzia.hyperstar.ui.module.systemui.other

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.SystemUIPagerList
import com.yunzia.hyperstar.ui.base.ModuleNavPagers
import com.yunzia.hyperstar.ui.base.SuperNavHostArrow
import com.yunzia.hyperstar.ui.base.XSuperSwitch
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.utils.Utils

@Composable
fun SystemUIOtherPager(
    navController: NavHostController
) {
    ModuleNavPagers(
        activityTitle = stringResource(R.string.more),
        navController = navController,
        endClick = {
            Utils.rootShell("killall com.android.systemui")
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
            title = R.string.power_menu,
        ) {
            XSuperSwitch(
                title = stringResource(R.string.is_power_menu_nav_show_title),
                key = "is_power_menu_nav_show"
            )
            SuperNavHostArrow(
                title = "电源菜单样式",
                navController = navController,
                route = SystemUIPagerList.POWERMENU

            )
        }
    }
}