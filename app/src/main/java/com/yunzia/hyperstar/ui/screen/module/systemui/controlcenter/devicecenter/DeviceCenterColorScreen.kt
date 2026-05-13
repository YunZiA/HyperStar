package com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.devicecenter

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.preference.PreferenceScreen
import com.yunzia.hyperstar.ui.component.preference.preferenceGroup
import com.yunzia.hyperstar.ui.component.preference.sp.SpColorPickerPreference
import com.yunzia.hyperstar.ui.navigation.ColorEditRoutes
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.utils.Helper
import SearchRoute
import androidx.activity.compose.LocalActivity
import com.yunzia.hyperstar.MainActivity

@SearchRoute(route = ColorEditRoutes.DeviceCenterColor::class)
@Composable
fun DeviceCenterColorScreen() {
    val navController = LocalNavigator.current
    val activity = LocalActivity.current as MainActivity
    PreferenceScreen(
        title = stringResource(R.string.smart_hub_color),
        navController = navController,
        endClick = {
            Helper.rootShell("killall com.android.systemui")
        },
        scrollToKey = activity.appViewModel.scrollToKey.value,
        onScrollComplete = { activity.appViewModel.scrollToKey.value = null },
    ) { _, _ ->
        preferenceGroup("空设备状态") {
            SpColorPickerPreference(
                title = stringResource(R.string.icon),
                key = "device_center_icon_color"
            )
            SpColorPickerPreference(
                title = stringResource(R.string.title),
                key = "device_center_title_color"
            )
        }
        preferenceGroup("设备项") {
            SpColorPickerPreference(
                title = stringResource(R.string.background),
                key = "device_center_item_background_color"
            )
            SpColorPickerPreference(
                title = stringResource(R.string.device_center_detail_icon),
                key = "device_center_detail_icon_color"
            )
        }
    }
}
