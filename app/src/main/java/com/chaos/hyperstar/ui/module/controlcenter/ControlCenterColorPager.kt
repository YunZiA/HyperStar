package com.chaos.hyperstar.ui.module.controlcenter

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ModulePagers
import com.chaos.hyperstar.ui.base.ColorPickerTool
import com.chaos.hyperstar.ui.base.ContentFolder
import com.chaos.hyperstar.ui.base.MiuixActivitySuperArrow
import com.chaos.hyperstar.ui.base.classes
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.ui.module.controlcenter.card.QSCardColorActivity
import com.chaos.hyperstar.ui.module.controlcenter.list.QSListColorActivity
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
            MiuixActivitySuperArrow(
                title = stringResource(R.string.color_edit),
                context = activity,
                activity = QSCardColorActivity::class.java
            )
        }

        classes(
            title = R.string.media
        ){

        }

        classes(
            title = R.string.volume_or_brightness
        ){

        }
        classes(
            title = R.string.device_control
        ){

        }
        classes(
            title = R.string.device_center
        ){

        }
        classes(
            title = R.string.tile
        ){
            MiuixActivitySuperArrow(
                title = stringResource(R.string.color_edit),
                context = activity,
                activity = QSListColorActivity::class.java
            )
        }
        classes(
            title = R.string.edit
        ){

        }


    }
}