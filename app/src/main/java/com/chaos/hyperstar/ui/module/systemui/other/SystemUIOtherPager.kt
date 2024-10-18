package com.chaos.hyperstar.ui.module.systemui.other

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ModuleNavPagers
import com.chaos.hyperstar.ui.base.ModulePagers
import com.chaos.hyperstar.ui.base.XSuperSwitch
import com.chaos.hyperstar.ui.base.classes
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.utils.Utils

@Composable
fun SystemUIOtherPager(
    navController: NavController
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
            XSuperSwitch(
                title = stringResource(R.string.is_use_power_menu_plus_title),
                key = "is_use_power_menu_plus"
            )
        }
    }
}