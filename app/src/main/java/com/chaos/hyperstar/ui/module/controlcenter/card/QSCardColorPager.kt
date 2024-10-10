package com.chaos.hyperstar.ui.module.controlcenter.card

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ModulePagers
import com.chaos.hyperstar.ui.base.ColorPickerTool
import com.chaos.hyperstar.ui.base.classes
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.utils.Utils

@Composable
fun QSCardColorPager(
    activity: ComponentActivity,
) {
    ModulePagers(
        activityTitle = stringResource(R.string.card_tile_color),
        activity = activity,
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
