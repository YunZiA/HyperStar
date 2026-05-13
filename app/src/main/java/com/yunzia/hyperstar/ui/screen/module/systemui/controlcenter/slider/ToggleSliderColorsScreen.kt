package com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.slider

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.preference.PreferenceContentFolder
import com.yunzia.hyperstar.ui.component.preference.PreferenceScreen
import com.yunzia.hyperstar.ui.component.preference.preferenceGroup
import com.yunzia.hyperstar.ui.component.preference.sp.SpColorPickerPreference
import com.yunzia.hyperstar.ui.navigation.ColorEditRoutes
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.utils.Helper
import SearchRoute
import androidx.activity.compose.LocalActivity
import com.yunzia.hyperstar.MainActivity

@SearchRoute(route = ColorEditRoutes.ToggleSliderColor::class)
@Composable
fun ToggleSliderColorsScreen() {
    val navController = LocalNavigator.current
    val activity = LocalActivity.current as MainActivity
    PreferenceScreen(
        title = stringResource(R.string.toggle_slider_color),
        navController = navController,
        endClick = {
            Helper.rootShell("killall com.android.systemui")
        },
        scrollToKey = activity.appViewModel.scrollToKey.value,
        onScrollComplete = { activity.appViewModel.scrollToKey.value = null },
    ) { _, _ ->
        preferenceGroup(R.string.advanced_textures) {
            PreferenceContentFolder(stringResource(R.string.progress_bar)) {
                SpColorPickerPreference(
                    title = stringResource(R.string.color_mix_main),
                    key = "toggle_slider_progress_color_main"
                )
                SpColorPickerPreference(
                    title = stringResource(R.string.color_mix_secondary),
                    key = "toggle_slider_progress_color_secondary"
                )
            }
            PreferenceContentFolder(stringResource(R.string.toggle_slider_value)) {
                SpColorPickerPreference(
                    title = stringResource(R.string.color_mix_main),
                    key = "toggle_slider_value_color_main"
                )
                SpColorPickerPreference(
                    title = stringResource(R.string.color_mix_secondary),
                    key = "toggle_slider_value_color_secondary"
                )
            }
            PreferenceContentFolder(stringResource(R.string.icon)) {
                SpColorPickerPreference(
                    title = stringResource(R.string.color_mix_main),
                    key = "toggle_slider_icon_color_main"
                )
                SpColorPickerPreference(
                    title = stringResource(R.string.color_mix_secondary),
                    key = "toggle_slider_icon_color_secondary"
                )
            }
        }
        preferenceGroup(R.string.disabled_advanced_textures) {
            SpColorPickerPreference(
                title = stringResource(R.string.progress_bar),
                key = "toggle_slider_progress_color"
            )
            SpColorPickerPreference(
                title = stringResource(R.string.toggle_slider_value),
                key = "toggle_slider_value_color"
            )
            SpColorPickerPreference(
                title = stringResource(R.string.icon),
                key = "toggle_slider_icon_color"
            )
        }
    }
}
