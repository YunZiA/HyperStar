package com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.ColorPickerTool
import com.yunzia.hyperstar.ui.component.ItemAnim
import com.yunzia.hyperstar.ui.component.SuperGroupPosition
import com.yunzia.hyperstar.ui.component.XSuperDropdown
import com.yunzia.hyperstar.ui.component.itemGroup
import com.yunzia.hyperstar.ui.component.pager.ModuleNavPagers
import com.yunzia.hyperstar.utils.Helper
import com.yunzia.hyperstar.utils.SPUtils

@Composable
fun QSListColorScreen(
    navController: NavController,
    currentStartDestination: MutableState<String>
) {
    val tileColor = remember {
        mutableIntStateOf(SPUtils.getInt("qs_list_tile_color_for_state",0))
    }
    val waitTime = 105L
    ModuleNavPagers(
        activityTitle = stringResource(R.string.tile_color),
        navController = navController,
        parentRoute = currentStartDestination,
        endClick = {
            Helper.rootShell("killall com.android.systemui")
        },
    ) {

        itemGroup(
            R.string.general,
            position = SuperGroupPosition.FIRST
        ) {

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

        itemGroup(
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

        itemGroup(
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

        itemGroup(
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

        itemGroup(
            title = R.string.unavailable_state_color,
            position = SuperGroupPosition.LAST
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