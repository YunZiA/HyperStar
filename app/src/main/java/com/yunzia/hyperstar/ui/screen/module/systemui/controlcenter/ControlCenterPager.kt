package com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.SuperGroupPosition
import com.yunzia.hyperstar.ui.component.XContentDropdown
import com.yunzia.hyperstar.ui.component.XDropdown
import com.yunzia.hyperstar.ui.component.preference.PreferenceList
import com.yunzia.hyperstar.ui.component.preference.core.SearchableNavPreference
import com.yunzia.hyperstar.ui.component.preference.preferenceGroup
import com.yunzia.hyperstar.ui.component.preference.sp.SpSliderPreference
import com.yunzia.hyperstar.ui.component.preference.sp.SpStringPreference
import com.yunzia.hyperstar.ui.component.preference.sp.SpSwitchFolderPreference
import com.yunzia.hyperstar.ui.component.preference.sp.SpSwitchPreference
import com.yunzia.hyperstar.ui.component.preference.sp.SpSwitchSliderPreference
import SearchRoute
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.component.preference.sp.SpContentDropdownPreference
import com.yunzia.hyperstar.ui.component.preference.sp.SpDropdownPreference
import com.yunzia.hyperstar.ui.navigation.MainRoutes
import com.yunzia.hyperstar.ui.navigation.Navigator
import com.yunzia.hyperstar.ui.navigation.SystemUIRoutes
import com.yunzia.hyperstar.utils.getSettingChannel
import com.yunzia.hyperstar.utils.isOS1Settings
import top.yukonga.miuix.kmp.basic.ScrollBehavior

@SearchRoute(route = MainRoutes.SystemUI::class, tabIndex = 0)
@Composable
fun ControlCenterPager(
    navController: Navigator,
    scrollBehavior: ScrollBehavior,
    paddingValue: PaddingValues,
    scrollToKey: String? = null,
    onScrollComplete: (() -> Unit)? = null,
) {
    PreferenceList(
        contentPadding = PaddingValues(bottom = paddingValue.calculateBottomPadding()),
        scrollBehavior = scrollBehavior,
        scrollToKey = scrollToKey,
        onScrollComplete = onScrollComplete
    ) {
        preferenceGroup(
            title = R.string.basics,
            position = SuperGroupPosition.FIRST,
        ) {
            val blurOptions = stringArrayResource(R.array.is_super_blur_entire).toList()
            SpDropdownPreference(
                key = "is_super_blur_Widget",
                title = stringResource(R.string.widget_advanced_textures),
                entries = blurOptions,
            )

            SearchableNavPreference(
                key = "control_center_color_edit_nav",
                title = stringResource(R.string.color_edit),
                onClick = { navController.navigate(SystemUIRoutes.ColorEdit) }
            )
            SearchableNavPreference(
                key = "control_center_layout_arrangement_nav",
                title = stringResource(R.string.control_center_edit),
                onClick = { navController.navigate(SystemUIRoutes.LayoutArrangement) }
            )
            SearchableNavPreference(
                key = "media_settings_nav",
                title = stringResource(R.string.media_settings),
                onClick = { navController.navigate(SystemUIRoutes.Media) }
            )
        }

        preferenceGroup(
            title = R.string.header,
        ) {
            SpSwitchPreference(
                title = stringResource(R.string.close_qs_clock_anim_title),
                key = "close_qs_clock_anim",
                visible = { getSettingChannel() == 1 }
            )

            SpSwitchPreference(
                title = stringResource(R.string.is_use_chaos_header_title),
                key = "is_use_chaos_header"
            )

            SpSwitchFolderPreference(
                key = "close_header_show_message",
                title = stringResource(R.string.close_header_show_message_title),
                contrary = true,
                visible = { getSettingChannel() == 2 },
                content = {
                    SpSliderPreference(
                        title = stringResource(R.string.header_show_message_millis_title),
                        key = "header_show_message_millis",
                        defaultValue = 1f,
                        valueRange = 0.1f..5f,
                        decimalPlaces = 2,
                        valueFormatter = { "${it}s" }
                    )
                }
            )

        }
        preferenceGroup(
            title = R.string.card_tile,
        ) {
            SpSwitchPreference(
                title = stringResource(R.string.card_tile_click_close_title),
                summary = stringResource(R.string.card_tile_click_close_summary),
                key = "card_tile_click_close"
            )
            SpSwitchFolderPreference(
                key = "use_card_tile_list",
                title = stringResource(R.string.enable_card_tile_edit),
                content = {
                    SearchableNavPreference(
                        key = "card_tile_edit_nav",
                        title = stringResource(R.string.card_tile_edit),
                        onClick = { navController.navigate(SystemUIRoutes.CardList) }
                    )
                }
            )
        }
        preferenceGroup(
            title = R.string.volume_or_brightness,
        ) {
            SpSwitchSliderPreference(
                switchKey = "is_change_qs_progress_radius",
                switchTitle = stringResource(R.string.is_change_qs_progress_radius_title),
                switchSummary = stringResource(R.string.progress_radius_summary),
                key = "qs_progress_radius",
                title = stringResource(R.string.qs_progress_radius_title),
                unit = "Dp",
                minValue = 0f,
                maxValue = 20f,
                defaultValue = 2f,
                decimalPlaces = 1,
            )
            SpSwitchPreference(
                title = stringResource(R.string.qs_brightness_top_value_show_title),
                key = "qs_brightness_top_value_show"
            )
            SpSwitchPreference(
                title = stringResource(R.string.qs_volume_top_value_show_title),
                key = "qs_volume_top_value_show",
                visible = { isOS1Settings() }
            )

        }
        preferenceGroup(
            title = R.string.device_center,
        ) {
            SpDropdownPreference(
                key = "is_device_center_mode",
                title = stringResource(R.string.device_center_ist),
                entriesId = R.array.is_device_center_mode_entire
            )
        }
        preferenceGroup(
            title = R.string.tile,
        ) {
            SpSwitchPreference(
                title = stringResource(R.string.list_tile_click_close_title),
                summary = stringResource(R.string.list_tile_click_close_summary),
                key = "list_tile_click_close"
            )
            SpSwitchPreference(
                title = stringResource(R.string.title_fix_list_tile_icon_scale),
                key = "fix_list_tile_icon_scale"
            )
            SpSwitchSliderPreference(
                switchKey = "is_qs_list_tile_radius",
                switchTitle = stringResource(R.string.is_qs_list_tile_radius_title),
                switchSummary = stringResource(R.string.is_qs_list_tile_radius_summary),
                key = "qs_list_tile_radius",
                title = stringResource(R.string.qs_list_tile_radius_title),
                unit = "dp",
                minValue = 0f,
                maxValue = 36f,
                defaultValue = when (getSettingChannel()) {
                    1, 2 -> 20f
                    else -> 24f
                },
                decimalPlaces = 1,
            )
            SpContentDropdownPreference(
                title = stringResource(R.string.is_list_label_mode_title),
                key = "is_list_label_mode",
                option = R.array.is_list_label_mode_entire
            ) {
                when(it) {
                    0 -> SpDropdownPreference(
                        title = stringResource(R.string.wordless_mode),
                        key = "is_wordless_mode_0",
                        entriesId = R.array.is_wordless_mode_entire
                    )
                    2 -> SearchableNavPreference(
                        key = "tile_layout_nav",
                        title = stringResource(R.string.tile_layout),
                        onClick = { navController.navigate(SystemUIRoutes.TileLayout) }
                    )
                }
            }
            SpSwitchPreference(
                title = stringResource(R.string.enable_title_follow_animation),
                key = "title_follow_anim"
            )
            SpSwitchPreference(
                title = stringResource(R.string.list_tile_label_marquee_title),
                key = "list_tile_label_marquee"
            )
        }

        preferenceGroup(
            title = R.string.other,
            position = SuperGroupPosition.LAST,
        ) {
            SpStringPreference(
                title = stringResource(R.string.title_qs_customize_entry_button_text),
                key = "qs_customize_entry_button_text"
            )
            SpSwitchPreference(
                title = stringResource(R.string.close_edit_button_show_title),
                key = "close_edit_button_show"
            )
        }
    }
}
