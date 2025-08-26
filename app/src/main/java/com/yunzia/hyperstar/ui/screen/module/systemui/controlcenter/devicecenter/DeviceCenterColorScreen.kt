package com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.devicecenter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.ColorPickerTool
import com.yunzia.hyperstar.ui.component.itemGroup
import com.yunzia.hyperstar.ui.component.pager.ModuleNavPagers
import com.yunzia.hyperstar.utils.Helper

@Composable
fun DeviceCenterColorScreen(
    navController: NavController,
    currentStartDestination: MutableState<String>
) {
    ModuleNavPagers(
        activityTitle = stringResource(R.string.smart_hub_color),
        navController = navController,
        parentRoute = currentStartDestination,
        endClick = {
            Helper.rootShell("killall com.android.systemui")
        },
    ) {

        itemGroup(
            title = "空设备状态"
        ) {
            ColorPickerTool(
                title = stringResource(R.string.icon),
                key = "device_center_icon_color"
            )
            ColorPickerTool(
                title = stringResource(R.string.title),
                key = "device_center_title_color"
            )

        }
        this.itemGroup(
            title = "设备项"
        ) {
            ColorPickerTool(
                title = stringResource(R.string.background),
                key = "device_center_item_background_color"
            )
            ColorPickerTool(
                title = stringResource(R.string.device_center_detail_icon),
                key = "device_center_detail_icon_color"
            )
        }

    }
}