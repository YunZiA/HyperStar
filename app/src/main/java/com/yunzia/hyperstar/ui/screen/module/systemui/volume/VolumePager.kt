package com.yunzia.hyperstar.ui.screen.module.systemui.volume

import IgnoreSearchIndex
import SearchRoute
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.preference.PreferenceContentFolder
import com.yunzia.hyperstar.ui.component.SuperGroupPosition
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.PreferenceList
import com.yunzia.hyperstar.ui.component.preference.preferenceGroup
import com.yunzia.hyperstar.ui.component.preference.sp.SpSliderPreference
import com.yunzia.hyperstar.ui.component.preference.sp.SpSwitchPreference
import com.yunzia.hyperstar.ui.component.preference.sp.SpSwitchSliderPreference
import com.yunzia.hyperstar.ui.component.preference.sp.SpDropdownPreference
import com.yunzia.hyperstar.ui.navigation.MainRoutes
import com.yunzia.hyperstar.ui.navigation.Navigator
import com.yunzia.hyperstar.utils.isAtLeastOS2Settings
import com.yunzia.hyperstar.utils.isOS1Settings
import top.yukonga.miuix.kmp.basic.ScrollBehavior

@SearchRoute(route = MainRoutes.SystemUI::class, tabIndex = 1)
@Composable
fun VolumePager(
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
                key = "is_super_blur_volume",
                title = stringResource(R.string.is_super_blur_volume_title),
                entries = blurOptions
            )
            if (isOS1Settings()) {
                SpSwitchPreference(
                    title = stringResource(R.string.title_use_pad_volume),
                    summary = stringResource(R.string.summary_use_pad_volume),
                    key = "is_use_pad_volume"
                )
            }
            if (isAtLeastOS2Settings()) {
                SpSwitchPreference(
                    title = stringResource(R.string.title_volume_top_value_show),
                    key = "volume_top_value_show"
                )
            }
            SpSwitchSliderPreference(
                switchKey = "is_change_volume_progress_radius",
                switchTitle = stringResource(R.string.is_change_qs_progress_radius_title),
                switchSummary = stringResource(R.string.progress_radius_summary),
                key = "volume_progress_radius",
                title = stringResource(R.string.qs_progress_radius_title),
                unit = "dp",
                minValue = 0f,
                maxValue = 20f,
                defaultValue = 2f,
                decimalPlaces = 1,
            )
        }
        preferenceGroup(
            title = R.string.sidebar_mode,
        ) {
            if (isAtLeastOS2Settings()) {
                SpSwitchPreference(
                    title = stringResource(R.string.title_press_expand_volume),
                    summary = stringResource(R.string.summary_press_expand_volume),
                    key = "is_press_expand_volume"
                )
            }
            SpSwitchPreference(
                title = stringResource(R.string.title_standardview_hide),
                key = "is_hide_StandardView"
            )
            OrientationDimBarFolder(
                stringResource(R.string.title_volume_height_collapsed), "volume_height_collapsed", 300f
            )
            OrientationDimBarFolder(
                stringResource(R.string.title_volume_offset_top_collapsed), "volume_offset_top_collapsed", 250f
            )
            OrientationDimBarFolder(
                stringResource(R.string.title_volume_shadow_height_collapsed), "volume_shadow_height_collapsed", 300f
            )
            OrientationDimBarFolder(
                stringResource(R.string.status_shadow_height_when_button_hidden), "volume_shadow_height_collapsed_no_footer", 300f
            )
            OrientationDimBarFolder(
                stringResource(R.string.title_volume_shadow_margin_top_collapsed), "volume_shadow_margin_top_collapsed", 450f
            )
        }
    }
}

@Composable
@IgnoreSearchIndex
fun PreferenceGroupScope.OrientationDimBarFolder(
    title: String,
    key: String,
    maxValue: Float
) {
    PreferenceContentFolder(title, key = key) {
        SpSliderPreference(
            key = key + "_p",
            title = stringResource(R.string.PORTRAIT),
            defaultValue = -1f,
            valueRange = 0f..maxValue,
            unit = "dp",
            decimalPlaces = 1
        )
        SpSliderPreference(
            key = key + "_l",
            title = stringResource(R.string.LANDSCAPE),
            defaultValue = -1f,
            valueRange = 0f..maxValue,
            unit = "dp",
            decimalPlaces = 1
        )
    }
}
