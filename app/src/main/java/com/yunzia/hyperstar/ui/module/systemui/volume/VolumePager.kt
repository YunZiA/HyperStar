package com.yunzia.hyperstar.ui.module.systemui.volume

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.ModuleNavPagers
import com.yunzia.hyperstar.ui.base.XMiuixSuperSliderSwitch
import com.yunzia.hyperstar.ui.base.XSuperDropdown
import com.yunzia.hyperstar.ui.base.XSuperSwitch
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.utils.Utils

@Composable
fun VolumePager(
    navController: NavController
) {
    ModuleNavPagers(
        activityTitle = stringResource(R.string.sound_settings),
        navController = navController,
        endClick = {
            Utils.rootShell("killall com.android.systemui")
                   },
    ){
        firstClasses(
            title = R.string.basics
        ) {
            XSuperDropdown(
                title = stringResource(R.string.is_super_blur_volume_title),
                key = "is_super_blur_volume",
                option = R.array.is_super_blur_entire
            )
        }
        classes(title = R.string.progress_bar){
            XMiuixSuperSliderSwitch(
                switchTitle = stringResource(R.string.is_change_qs_progress_radius_title),
                switchKey = "is_change_volume_progress_radius",
                switchSummary = stringResource(id = R.string.progress_radius_summary),
                title = stringResource(R.string.qs_progress_radius_title) ,
                key ="volume_progress_radius",
                minValue = 0f,
                maxValue = 20f,
                progress = 2f,
                unit = "dp",
                decimalPlaces = 1
            )

        }
    }

}