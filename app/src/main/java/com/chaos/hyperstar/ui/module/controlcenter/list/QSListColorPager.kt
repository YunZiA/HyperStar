package com.chaos.hyperstar.ui.module.controlcenter.list

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ModulePagers
import com.chaos.hyperstar.ui.base.ColorPickerTool
import com.chaos.hyperstar.ui.base.ItemAnim
import com.chaos.hyperstar.ui.base.XSuperDropdown
import com.chaos.hyperstar.ui.base.classes
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.utils.SPUtils
import com.chaos.hyperstar.utils.Utils

@Composable
fun QSListColorPager(
    activity: ComponentActivity,
) {
    val tileColor = remember {
        mutableIntStateOf(SPUtils.getInt("qs_list_tile_color_for_state",0))
    }
    val waitTime = 105L
    ModulePagers(
        activityTitle = stringResource(R.string.tile_color),
        activity = activity,
        endClick = {
            Utils.rootShell("killall com.android.systemui")
        },
    ) {

        firstClasses(R.string.general) {

            XSuperDropdown(
                title = stringResource(R.string.title_color_in_state),
                option = R.array.qs_list_tile_color_for_state_entire,
                key = "qs_list_tile_color_for_state",
                selectedIndex = tileColor
            )
            ItemAnim(
                animState = (tileColor.intValue == 0),
                waitTime = waitTime
            ) {
                ColorPickerTool(
                    title = stringResource(R.string.title),
                    key = "list_title_color"
                )
            }

        }

        classes(
            title = R.string.close_state_color
        ) {
            ColorPickerTool(
                title = stringResource(R.string.icon),
                key = "list_icon_off_color"
            )
            ItemAnim(
                animState = (tileColor.intValue == 2),
                waitTime = waitTime
            ) {
                ColorPickerTool(
                    title = stringResource(R.string.title),
                    key = "list_title_off_color"
                )
            }

        }

        classes(
            title = R.string.enable_state_color
        ){
            ColorPickerTool(
                title = stringResource(R.string.enable_background),
                key = "list_enabled_color"
            )
            ColorPickerTool(
                title = stringResource(R.string.warning_background),
                key = "list_warning_color"
            )
            ColorPickerTool(
                title = stringResource(R.string.icon),
                key = "list_icon_on_color"
            )
            ItemAnim(
                animState = (tileColor.intValue == 2),
                waitTime = waitTime
            ) {
                ColorPickerTool(
                    title = stringResource(R.string.title),
                    key = "list_title_on_color"
                )
            }
        }

        classes(
            title = R.string.restricted_state_color
        ){
            ColorPickerTool(
                title = stringResource(R.string.background),
                key = "list_restricted_color"
            )
            ColorPickerTool(
                title = stringResource(R.string.icon),
                key = "list_icon_restricted_color"
            )
            ItemAnim(
                animState = (tileColor.intValue == 2),
                waitTime = waitTime
            ) {
                ColorPickerTool(
                    title = stringResource(R.string.title),
                    key = "list_title_restricted_color"
                )
            }
        }

        classes(
            title = R.string.unavailable_state_color
        ){
            ColorPickerTool(
                title = stringResource(R.string.background),
                key = "list_unavailable_color"
            )
            ColorPickerTool(
                title = stringResource(R.string.icon),
                key = "list_icon_unavailable_color"
            )
            ItemAnim(
                animState = (tileColor.intValue == 2),
                waitTime = waitTime
            ) {
                ColorPickerTool(
                    title = stringResource(R.string.title),
                    key = "list_title_unavailable_color"
                )
            }
        }


    }
}