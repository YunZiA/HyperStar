package com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.preference.core.SearchableNavPreference
import com.yunzia.hyperstar.ui.component.preference.PreferenceContentFolder
import com.yunzia.hyperstar.ui.component.preference.PreferenceScreen
import com.yunzia.hyperstar.ui.component.preference.preferenceGroup
import com.yunzia.hyperstar.ui.component.preference.sp.SpColorPickerPreference
import com.yunzia.hyperstar.ui.component.preference.sp.SpContentDropdownPreference
import com.yunzia.hyperstar.ui.navigation.ColorEditRoutes
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.ui.navigation.SystemUIRoutes
import com.yunzia.hyperstar.utils.Helper
import SearchRoute
import androidx.activity.compose.LocalActivity
import com.yunzia.hyperstar.MainActivity

@SearchRoute(route = SystemUIRoutes.ColorEdit::class)
@Composable
fun ControlCenterColorScreen() {
    val navController = LocalNavigator.current
    val activity = LocalActivity.current as MainActivity
    PreferenceScreen(
        title = stringResource(R.string.control_center_color_edit),
        navController = navController,
        endClick = {
            Helper.rootShell("killall com.android.systemui")
        },
        scrollToKey = activity.appViewModel.scrollToKey.value,
        onScrollComplete = { activity.appViewModel.scrollToKey.value = null },
    ) { _, _ ->
        preferenceGroup(R.string.control_center_background_color) {
            SpColorPickerPreference(
                title = stringResource(R.string.disabled_advanced_textures),
                key = "background_color"
            )
            PreferenceContentFolder(stringResource(R.string.advanced_textures)) {
                SpColorPickerPreference(
                    title = stringResource(R.string.color_mix_main),
                    key = "background_blend_color_main"
                )
                SpColorPickerPreference(
                    title = stringResource(R.string.color_mix_secondary),
                    key = "background_blend_color_secondary"
                )
            }
        }
        preferenceGroup(R.string.card_tile) {
            SearchableNavPreference(
                key = "card_tile_color_edit_nav",
                title = stringResource(R.string.color_edit),
                onClick = { navController.navigate(ColorEditRoutes.CardTileColor) }
            )
        }
        preferenceGroup(R.string.media) {
            PreferenceContentFolder(stringResource(R.string.color_edit)) {
                SpColorPickerPreference(
                    title = stringResource(R.string.expand_button),
                    key = "media_device_icon_color"
                )
                SpColorPickerPreference(
                    title = stringResource(R.string.song_title),
                    key = "media_title_color"
                )
                SpColorPickerPreference(
                    title = stringResource(R.string.artist),
                    key = "media_artist_color"
                )
                SpColorPickerPreference(
                    title = stringResource(R.string.empty_state),
                    key = "media_empty_state_color"
                )
                SpColorPickerPreference(
                    title = stringResource(R.string.media_button),
                    key = "media_icon_color_enabled"
                )
                SpColorPickerPreference(
                    title = stringResource(R.string.media_button_disabled),
                    key = "media_icon_color_disabled"
                )
            }
        }
        preferenceGroup(R.string.volume_or_brightness) {
            SearchableNavPreference(
                key = "toggle_slider_color_edit_nav",
                title = stringResource(R.string.color_edit),
                onClick = { navController.navigate(ColorEditRoutes.ToggleSliderColor) }
            )
        }
        preferenceGroup(R.string.device_control) {
            SpColorPickerPreference(
                title = stringResource(R.string.icon),
                key = "device_control_icon_color"
            )
            SpColorPickerPreference(
                title = stringResource(R.string.title),
                key = "device_control_title_color"
            )
        }
        preferenceGroup(R.string.device_center) {
            SearchableNavPreference(
                key = "device_center_color_edit_nav",
                title = stringResource(R.string.color_edit),
                onClick = { navController.navigate(ColorEditRoutes.DeviceCenterColor) }
            )
        }
        preferenceGroup(R.string.tile) {
            SearchableNavPreference(
                key = "list_color_edit_nav",
                title = stringResource(R.string.color_edit),
                onClick = { navController.navigate(ColorEditRoutes.ListColor) }
            )
        }
        preferenceGroup(R.string.edit) {
            SpContentDropdownPreference(
                key = "edit_background_mode",
                title = stringResource(R.string.edit_background_mode),
                option = R.array.edit_background_mode_entire,
                showOption = 0,
                content = {
                    SpColorPickerPreference(
                        title = stringResource(R.string.disabled_advanced_textures),
                        key = "edit_background_color"
                    )
                    PreferenceContentFolder(stringResource(R.string.advanced_textures)) {
                        SpColorPickerPreference(
                            title = stringResource(R.string.color_mix_main),
                            key = "edit_background_blend_color_main"
                        )
                        SpColorPickerPreference(
                            title = stringResource(R.string.color_mix_secondary),
                            key = "edit_background_blend_color_secondary"
                        )
                    }
                }
            )
            SpColorPickerPreference(
                title = stringResource(R.string.title),
                key = "edit_title_color"
            )
        }
    }
}
