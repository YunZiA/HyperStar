package com.yunzia.hyperstar.ui.module.systemui.other

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.SystemUIMoreList
import com.yunzia.hyperstar.ui.component.ColorPickerTool
import com.yunzia.hyperstar.ui.component.SuperNavHostArrow
import com.yunzia.hyperstar.ui.component.XSuperSwitch
import com.yunzia.hyperstar.ui.component.classes
import com.yunzia.hyperstar.ui.component.firstClasses
import com.yunzia.hyperstar.ui.component.pager.ModuleNavPagers
import com.yunzia.hyperstar.utils.Helper
import com.yunzia.hyperstar.utils.isOS2Settings

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
        if (isOS2Settings()){
            classes(
                title = R.string.classic_noy_type
            ) {
                SuperNavHostArrow(
                    title = stringResource(R.string.icon_stacking_whitelist),
                    navController = navController,
                    route = SystemUIMoreList.NOTIFICATIONOFIM

                )

            }
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
        classes(
            title = R.string.other_settings,
        ) {
            ColorPickerTool(
                title = stringResource(R.string.low_device_qc_background_color),
                key = "low_device_qc_background_color"
            )


        }
    }
}