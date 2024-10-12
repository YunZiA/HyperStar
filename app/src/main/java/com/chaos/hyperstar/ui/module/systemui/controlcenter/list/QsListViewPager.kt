package com.chaos.hyperstar.ui.module.systemui.controlcenter.list

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ModulePagers
import com.chaos.hyperstar.ui.base.XMiuixSlider
import com.chaos.hyperstar.ui.base.classes
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.utils.Utils


@Composable
fun QsListViewPager(activity: ComponentActivity, ) {
    ModulePagers(
        activityTitle = stringResource(R.string.tile_layout),
        activity = activity,
        endClick = {
            Utils.rootShell("killall com.android.systemui")
        }
    ){

        firstClasses(
            title = R.string.title_style
        ){
            XMiuixSlider(
                title = stringResource(R.string.title_size),
                key = "list_label_size",
                unit = "dp",
                maxValue = 25f,
                minValue = 0f,
                progress = 13f,
                decimalPlaces = 2
            )

            XMiuixSlider(
                title = stringResource(R.string.title_width),
                key = "list_label_width",
                unit = "%",
                maxValue = 100f,
                minValue = 0f,
                progress = 100f
            )

        }
        classes(
            title = R.string.vertical_spacing
        ){
            XMiuixSlider(
                title = stringResource(R.string.disable_icon_labels),
                key = "list_spacing_y",
                unit = "%",
                maxValue = 150f,
                minValue = 0f,
                progress = 100f
            )

            XMiuixSlider(
                title = stringResource(R.string.enable_icon_labels),
                key = "list_label_spacing_y",
                unit = "%",
                maxValue = 150f,
                minValue = 0f,
                progress = 100f
            )

        }


        classes(
            title = R.string.margin_top
        ){
            XMiuixSlider(
                title = stringResource(R.string.icon),
                key = "list_icon_top",
                unit = "%",
                maxValue = 50F,
                minValue = -50f,
                progress = 0f
            )

            XMiuixSlider(
                title = stringResource(R.string.title),
                key = "list_label_top",
                unit = "dp",
                maxValue = 100f,
                minValue = -100f,
                progress = 0f,
                decimalPlaces = 1
            )

        }


    }

}
