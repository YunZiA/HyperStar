package com.yunzia.hyperstar.ui.module.systemui.volume

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.ContentFolder
import com.yunzia.hyperstar.ui.base.ModuleNavPagers
import com.yunzia.hyperstar.ui.base.XMiuixSuperSliderSwitch
import com.yunzia.hyperstar.ui.base.XSuperDropdown
import com.yunzia.hyperstar.ui.base.XSuperSliders
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
        classes(title = R.string.sidebar_mode){

            XSuperSwitch(
                title = stringResource(R.string.title_press_expand_volume),
                summary = stringResource(R.string.summary_press_expand_volume),
                key = "is_press_expand_volume"
            )
            XSuperSwitch(
                title = stringResource(R.string.title_standardview_hide),
                key = "is_hide_StandardView"
            )
            OrientationDimBarFolder(
                stringResource(R.string.title_volume_height_collapsed),"volume_height_collapsed",300f
            )
            OrientationDimBarFolder(
                stringResource(R.string.title_volume_offset_top_collapsed),"volume_offset_top_collapsed",250f
            )
            OrientationDimBarFolder(
                stringResource(R.string.title_volume_shadow_height_collapsed),"volume_shadow_height_collapsed",300f
            )
            OrientationDimBarFolder(
                stringResource(R.string.title_volume_shadow_margin_top_collapsed),"volume_shadow_margin_top_collapsed",450f
            )

        }
    }

}


@Composable
fun OrientationDimBarFolder(
    title:String,
    key:String,
    maxValue: Float
){
    ContentFolder(title){

        XSuperSliders(
            host = title,
            title = stringResource(R.string.PORTRAIT),
            key = key+"_p",
            defValue = -1f,
            maxValue = maxValue,
            minValue = 0f,
            unit = "dp",
            decimalPlaces = 1
        )


        XSuperSliders(
            host = title,
            title = stringResource(R.string.LANDSCAPE),
            key = key+"_l",
            defValue = -1f,
            maxValue = maxValue,
            minValue = 0f,
            unit = "dp",
            decimalPlaces = 1
        )

    }
}
