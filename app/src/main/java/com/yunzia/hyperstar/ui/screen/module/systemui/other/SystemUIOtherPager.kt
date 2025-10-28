package com.yunzia.hyperstar.ui.screen.module.systemui.other

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.SystemUIMoreList
import com.yunzia.hyperstar.ui.component.ColorPickerTool
import com.yunzia.hyperstar.ui.component.SuperGroupPosition
import com.yunzia.hyperstar.ui.component.SuperNavHostArrow
import com.yunzia.hyperstar.ui.component.XSuperSwitch
import com.yunzia.hyperstar.ui.component.itemGroup
import com.yunzia.hyperstar.ui.component.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.utils.getSettingChannel
import com.yunzia.hyperstar.utils.isOS2Settings
import top.yukonga.miuix.kmp.basic.ScrollBehavior

@Composable
fun SystemUIOtherPager(
    navController: NavHostController,
    scrollBehavior: ScrollBehavior,
    paddingValue: PaddingValues
) {
    Log.d("SystemUIScreen", "SystemUIOtherPager: init")
    LazyColumn(
        modifier = Modifier.fillMaxSize().nestedOverScrollVertical(scrollBehavior.nestedScrollConnection),
        contentPadding = PaddingValues(bottom = paddingValue.calculateBottomPadding())
    ) {

        itemGroup(
            title = R.string.status_bar,
            position = SuperGroupPosition.FIRST
        ) {
            XSuperSwitch(
                title = stringResource(R.string.transparent_statusBar_background),
                summary = stringResource(R.string.transparent_statusBar_background_summary),
                key = "is_transparent_statusBar_background"
            )
        }
        itemGroup(
            title = R.string.navigation_bar,
        ) {
            XSuperSwitch(
                title = stringResource(R.string.transparent_navigationBar_background),
                summary = stringResource(R.string.transparent_statusBar_background_summary),
                key = "is_transparent_navigationBar_background"
            )
        }
        if (getSettingChannel() >= 2 ){
            itemGroup(
                title = R.string.classic_noy_type
            ) {
                SuperNavHostArrow(
                    title = stringResource(R.string.icon_stacking_whitelist),
                    navController = navController,
                    route = SystemUIMoreList.NOTIFICATIONOFIM

                )

            }
        }
        itemGroup(
            title = R.string.power_menu
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
        itemGroup(
            title = R.string.other_settings,
            position = SuperGroupPosition.LAST
        ) {
            ColorPickerTool(
                title = stringResource(R.string.low_device_qc_background_color),
                key = "low_device_qc_background_color"
            )
            if (isOS2Settings()){
                ColorPickerTool(
                    title = stringResource(R.string.notification_expansion_overlay_color_on_low_end_devices),
                    key = "low_device_not_second_background_color"
                )
            }


        }
    }
}