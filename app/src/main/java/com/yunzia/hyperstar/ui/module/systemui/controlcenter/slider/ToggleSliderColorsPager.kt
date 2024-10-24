package com.yunzia.hyperstar.ui.module.systemui.controlcenter.slider

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.ColorPickerTool
import com.yunzia.hyperstar.ui.base.ContentFolder
import com.yunzia.hyperstar.ui.base.ModuleNavPagers
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.utils.Utils

@Composable
fun ToggleSliderColorsPager(
    navController: NavController
) {
    ModuleNavPagers(
        activityTitle = stringResource(R.string.toggle_slider_color),
        navController = navController,
        endClick = {
            Utils.rootShell("killall com.android.systemui")
        },
    ) {
        firstClasses(
            title = R.string.advanced_textures
        ) {

            ContentFolder(stringResource(R.string.progress_bar)){

                ColorPickerTool(
                    title = stringResource(R.string.color_mix_main),
                    key = "toggle_slider_progress_color_main"
                )
                ColorPickerTool(
                    title = stringResource(R.string.color_mix_secondary),
                    key = "toggle_slider_progress_color_secondary"
                )
            }
            ContentFolder(stringResource(R.string.toggle_slider_value)){

                ColorPickerTool(
                    title = stringResource(R.string.color_mix_main),
                    key = "toggle_slider_value_color_main"
                )
                ColorPickerTool(
                    title = stringResource(R.string.color_mix_secondary),
                    key = "toggle_slider_value_color_secondary"
                )
            }
            ContentFolder(stringResource(R.string.icon)){

                ColorPickerTool(
                    title = stringResource(R.string.color_mix_main),
                    key = "toggle_slider_icon_color_main"
                )
                ColorPickerTool(
                    title = stringResource(R.string.color_mix_secondary),
                    key = "toggle_slider_icon_color_secondary"
                )
            }



        }
        classes(
            title = R.string.disabled_advanced_textures
        ) {
            ColorPickerTool(
                title = stringResource(R.string.progress_bar),
                key = "toggle_slider_progress_color"
            )
            ColorPickerTool(
                title = stringResource(R.string.toggle_slider_value),
                key = "toggle_slider_value_color"
            )
            ColorPickerTool(
                title = stringResource(R.string.icon),
                key = "toggle_slider_icon_color"
            )
        }

    }

}