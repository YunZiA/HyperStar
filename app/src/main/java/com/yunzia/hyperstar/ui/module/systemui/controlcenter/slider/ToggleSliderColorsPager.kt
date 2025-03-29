package com.yunzia.hyperstar.ui.module.systemui.controlcenter.slider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.ColorPickerTool
import com.yunzia.hyperstar.ui.base.ContentFolder
import com.yunzia.hyperstar.ui.base.pager.ModuleNavPagers
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.utils.Helper

@Composable
fun ToggleSliderColorsPager(
    navController: NavController,
    currentStartDestination: MutableState<String>
) {
    ModuleNavPagers(
        activityTitle = stringResource(R.string.toggle_slider_color),
        parentRoute = currentStartDestination,
        navController = navController,
        endClick = {
            Helper.rootShell("killall com.android.systemui")
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