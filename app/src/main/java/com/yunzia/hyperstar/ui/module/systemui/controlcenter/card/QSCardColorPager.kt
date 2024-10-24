package com.yunzia.hyperstar.ui.module.systemui.controlcenter.card

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.ColorPickerTool
import com.yunzia.hyperstar.ui.base.ModuleNavPagers
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.utils.Utils

@Composable
fun QSCardColorPager(
    navController: NavController
) {
    ModuleNavPagers(
        activityTitle = stringResource(R.string.card_tile_color),
        navController = navController,
        endClick = {
            Utils.rootShell("killall com.android.systemui")
        },
    ) {
        firstClasses(
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
        classes(
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
        classes(
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
        classes(
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
