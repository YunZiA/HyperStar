package com.yunzia.hyperstar.ui.module.systemui.controlcenter

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.yunzia.hyperstar.CenterColorList
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.ColorPickerTool
import com.yunzia.hyperstar.ui.base.ContentFolder
import com.yunzia.hyperstar.ui.base.ModuleNavPagers
import com.yunzia.hyperstar.ui.base.SuperNavHostArrow
import com.yunzia.hyperstar.ui.base.XMiuixContentDropdown
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.utils.Utils

@Composable
fun ControlCenterColorPager(
    navController: NavController
) {
    ModuleNavPagers(
        activityTitle = stringResource(R.string.control_center_color_edit),
        navController = navController,
        endClick = {
            Utils.rootShell("killall com.android.systemui")
        },
    ) {

        firstClasses(
            title = R.string.control_center_background_color
        ){

            ColorPickerTool(
                title = stringResource(R.string.disabled_advanced_textures),
                key = "background_color"
            )
            ContentFolder(stringResource(R.string.advanced_textures)){

                ColorPickerTool(
                    title = stringResource(R.string.color_mix_main),
                    key = "background_blend_color_main"
                )
                ColorPickerTool(
                    title = stringResource(R.string.color_mix_secondary),
                    key = "background_blend_color_secondary"
                )
            }


        }

        classes(
            title = R.string.card_tile
        ){

            SuperNavHostArrow(
                title = stringResource(R.string.color_edit),
                navController = navController,
                route = CenterColorList.CARD_TILE
            )
        }

        classes(
            title = R.string.media
        ){
            ContentFolder(stringResource(R.string.color_edit)){
                ColorPickerTool(
                    title = stringResource(R.string.expand_button),
                    key = "media_device_icon_color"
                )
                ColorPickerTool(
                    title = stringResource(R.string.song_title),
                    key = "media_title_color"
                )
                ColorPickerTool(
                    title = stringResource(R.string.artist),
                    key = "media_artist_color"
                )
                ColorPickerTool(
                    title = stringResource(R.string.empty_state),
                    key = "media_empty_state_color"
                )
                ColorPickerTool(
                    title = stringResource(R.string.media_button),
                    key = "media_icon_color_enabled"
                )
                ColorPickerTool(
                    title = stringResource(R.string.media_button_disabled),
                    key = "media_icon_color_disabled"
                )

            }
        }

        classes(
            title = R.string.volume_or_brightness
        ){
            SuperNavHostArrow(
                title = stringResource(R.string.color_edit),
                navController = navController,
                route = CenterColorList.TOGGLE_SLIDER
            )

        }
        classes(
            title = R.string.device_control
        ){
            ColorPickerTool(
                title = stringResource(R.string.icon),
                key = "device_control_icon_color"
            )
            ColorPickerTool(
                title = stringResource(R.string.title),
                key = "device_control_title_color"
            )

        }
        classes(
            title = R.string.device_center
        ){
            SuperNavHostArrow(
                title = stringResource(R.string.color_edit),
                navController = navController,
                route = CenterColorList.DEVICE_CENTER
            )

        }
        classes(
            title = R.string.tile
        ){
            SuperNavHostArrow(
                title = stringResource(R.string.color_edit),
                navController = navController,
                route = CenterColorList.LIST_COLOR
            )
        }
        classes(
            title = R.string.edit
        ){

            XMiuixContentDropdown(
                title = stringResource(R.string.edit_background_mode),
                key = "edit_background_mode",
                option = R.array.edit_background_mode_entire,
                showOption = 0,
            ){
                ColorPickerTool(
                    title = stringResource(R.string.disabled_advanced_textures),
                    key = "edit_background_color"
                )
                ContentFolder(stringResource(R.string.advanced_textures)){

                    ColorPickerTool(
                        title = stringResource(R.string.color_mix_main),
                        key = "edit_background_blend_color_main"
                    )
                    ColorPickerTool(
                        title = stringResource(R.string.color_mix_secondary),
                        key = "edit_background_blend_color_secondary"
                    )
                }

            }

            ColorPickerTool(
                title = stringResource(R.string.title),
                key = "edit_title_color"
            )

        }


    }
}