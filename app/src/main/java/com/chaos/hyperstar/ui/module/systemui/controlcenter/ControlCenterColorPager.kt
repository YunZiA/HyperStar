package com.chaos.hyperstar.ui.module.systemui.controlcenter

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ModulePagers
import com.chaos.hyperstar.ui.base.ColorPickerTool
import com.chaos.hyperstar.ui.base.ContentFolder
import com.chaos.hyperstar.ui.base.SuperActivityArrow
import com.chaos.hyperstar.ui.base.XMiuixContentDropdown
import com.chaos.hyperstar.ui.base.XSuperSwitch
import com.chaos.hyperstar.ui.base.classes
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.ui.module.systemui.controlcenter.card.QSCardColorActivity
import com.chaos.hyperstar.ui.module.systemui.controlcenter.devicecenter.DeviceCenterColorSettings
import com.chaos.hyperstar.ui.module.systemui.controlcenter.list.QSListColorActivity
import com.chaos.hyperstar.ui.module.systemui.controlcenter.list.QsListViewSettings
import com.chaos.hyperstar.ui.module.systemui.controlcenter.slider.ToggleSliderColorsSettings
import com.chaos.hyperstar.utils.Utils

@Composable
fun ControlCenterColorPager(
    activity: ComponentActivity,
) {
    ModulePagers(
        activityTitle = stringResource(R.string.control_center_color_edit),
        activity = activity,
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
            SuperActivityArrow(
                title = stringResource(R.string.color_edit),
                context = activity,
                activity = QSCardColorActivity::class.java
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
            SuperActivityArrow(
                title = stringResource(R.string.color_edit),
                context = activity,
                activity = ToggleSliderColorsSettings::class.java
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
            SuperActivityArrow(
                title = stringResource(R.string.color_edit),
                context = activity,
                activity = DeviceCenterColorSettings::class.java
            )

        }
        classes(
            title = R.string.tile
        ){
            SuperActivityArrow(
                title = stringResource(R.string.color_edit),
                context = activity,
                activity = QSListColorActivity::class.java
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