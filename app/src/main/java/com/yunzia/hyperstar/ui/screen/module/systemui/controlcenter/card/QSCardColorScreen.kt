package com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.card

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.ColorPickerTool
import com.yunzia.hyperstar.ui.component.itemGroup
import com.yunzia.hyperstar.ui.component.pager.ModuleNavPagers
import com.yunzia.hyperstar.utils.Helper

@Composable
fun QSCardColorScreen(
    navController: NavHostController,
    currentStartDestination: MutableState<String>
) {
    ModuleNavPagers(
        activityTitle = stringResource(R.string.card_tile_color),
        parentRoute = currentStartDestination,
        navController = navController,
        endClick = {
            Helper.rootShell("killall com.android.systemui")
        },
    ) {
        itemGroup(
            title = R.string.close_state_color
        ) {

            ColorPickerTool(
                title = stringResource(R.string.icon),
                key = "card_icon_off_color"
            )
            ColorPickerTool(
                title = stringResource(R.string.title_primary),
                key = "card_primary_disabled_color"
            )
            ColorPickerTool(
                title = stringResource(R.string.title_secondary),
                key = "card_secondary_disabled_color"
            )

        }
        this.itemGroup(
            title = R.string.enable_state_color
        ) {
            ColorPickerTool(
                title = stringResource(R.string.background),
                key = "card_enabled_color"
            )
            ColorPickerTool(
                title = stringResource(R.string.icon),
                key = "card_icon_on_color"
            )
            ColorPickerTool(
                title = stringResource(R.string.title_primary),
                key = "card_primary_enabled_color"
            )
            ColorPickerTool(
                title = stringResource(R.string.title_secondary),
                key = "card_secondary_enabled_color"
            )


        }
        this.itemGroup(
            title = R.string.restricted_state_color,
        ) {
            ColorPickerTool(
                title = stringResource(R.string.background),
                key = "card_restricted_color"
            )
            ColorPickerTool(
                title = stringResource(R.string.icon),
                key = "card_icon_restricted_color"
            )
            ColorPickerTool(
                title = stringResource(R.string.title_primary),
                key = "card_primary_restricted_color"
            )
            ColorPickerTool(
                title = stringResource(R.string.title_secondary),
                key = "card_secondary_restricted_color"
            )


        }
        this.itemGroup(
            title = R.string.unavailable_state_color
        ) {
            ColorPickerTool(
                title = stringResource(R.string.background),
                key = "card_unavailable_color"
            )
            ColorPickerTool(
                title = stringResource(R.string.icon),
                key = "card_icon_unavailable_color"
            )
            ColorPickerTool(
                title = stringResource(R.string.title_primary),
                key = "card_primary_unavailable_color"
            )
            ColorPickerTool(
                title = stringResource(R.string.title_secondary),
                key = "card_secondary_unavailable_color"
            )


        }

    }
}
