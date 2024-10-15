package com.chaos.hyperstar.ui.module.systemui.controlcenter.devicecenter

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ColorPickerTool
import com.chaos.hyperstar.ui.base.ModuleNavPagers
import com.chaos.hyperstar.ui.base.ModulePagers
import com.chaos.hyperstar.ui.base.classes
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.utils.Utils

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