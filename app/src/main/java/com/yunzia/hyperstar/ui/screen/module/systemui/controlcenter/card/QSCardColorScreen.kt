package com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.card

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.preference.PreferenceScreen
import com.yunzia.hyperstar.ui.component.preference.preferenceGroup
import com.yunzia.hyperstar.ui.component.preference.sp.SpColorPickerPreference
import com.yunzia.hyperstar.ui.navigation.ColorEditRoutes
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.utils.Helper
import SearchRoute
import androidx.activity.compose.LocalActivity
import com.yunzia.hyperstar.MainActivity

@SearchRoute(route = ColorEditRoutes.CardTileColor::class)
@Composable
fun QSCardColorScreen() {
    val navController = LocalNavigator.current
    val activity = LocalActivity.current as MainActivity
    PreferenceScreen(
        title = stringResource(R.string.card_tile_color),
        navController = navController,
        endClick = {
            Helper.rootShell("killall com.android.systemui")
        },
        scrollToKey = activity.appViewModel.scrollToKey.value,
        onScrollComplete = { activity.appViewModel.scrollToKey.value = null },
    ) { _, _ ->
        preferenceGroup(R.string.close_state_color) {
            SpColorPickerPreference(
                title = stringResource(R.string.icon),
                key = "card_icon_off_color"
            )
            SpColorPickerPreference(
                title = stringResource(R.string.title_primary),
                key = "card_primary_disabled_color"
            )
            SpColorPickerPreference(
                title = stringResource(R.string.title_secondary),
                key = "card_secondary_disabled_color"
            )
        }
        preferenceGroup(R.string.enable_state_color) {
            SpColorPickerPreference(
                title = stringResource(R.string.background),
                key = "card_enabled_color"
            )
            SpColorPickerPreference(
                title = stringResource(R.string.icon),
                key = "card_icon_on_color"
            )
            SpColorPickerPreference(
                title = stringResource(R.string.title_primary),
                key = "card_primary_enabled_color"
            )
            SpColorPickerPreference(
                title = stringResource(R.string.title_secondary),
                key = "card_secondary_enabled_color"
            )
        }
        preferenceGroup(R.string.restricted_state_color) {
            SpColorPickerPreference(
                title = stringResource(R.string.background),
                key = "card_restricted_color"
            )
            SpColorPickerPreference(
                title = stringResource(R.string.icon),
                key = "card_icon_restricted_color"
            )
            SpColorPickerPreference(
                title = stringResource(R.string.title_primary),
                key = "card_primary_restricted_color"
            )
            SpColorPickerPreference(
                title = stringResource(R.string.title_secondary),
                key = "card_secondary_restricted_color"
            )
        }
        preferenceGroup(R.string.unavailable_state_color) {
            SpColorPickerPreference(
                title = stringResource(R.string.background),
                key = "card_unavailable_color"
            )
            SpColorPickerPreference(
                title = stringResource(R.string.icon),
                key = "card_icon_unavailable_color"
            )
            SpColorPickerPreference(
                title = stringResource(R.string.title_primary),
                key = "card_primary_unavailable_color"
            )
            SpColorPickerPreference(
                title = stringResource(R.string.title_secondary),
                key = "card_secondary_unavailable_color"
            )
        }
    }
}
