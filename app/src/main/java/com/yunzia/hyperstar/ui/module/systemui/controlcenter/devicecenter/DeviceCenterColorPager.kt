package com.yunzia.hyperstar.ui.module.systemui.controlcenter.devicecenter

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.ColorPickerTool
import com.yunzia.hyperstar.ui.base.ModuleNavPagers
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.utils.Utils

@Composable
fun DeviceCenterColorPager(
    navController: NavController
) {
    ModuleNavPagers(
        activityTitle = stringResource(R.string.smart_hub_color),
        navController = navController,
        endClick = {
            Utils.rootShell("killall com.android.systemui")
        },
    ) {

        firstClasses(
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
        classes(
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