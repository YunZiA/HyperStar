package com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.media

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.ContentDropdown
import com.yunzia.hyperstar.ui.component.preference.PreferenceScreen
import com.yunzia.hyperstar.ui.component.preference.core.ListPreference
import com.yunzia.hyperstar.ui.component.preference.core.SearchableNavPreference
import com.yunzia.hyperstar.ui.component.preference.preferenceGroup
import com.yunzia.hyperstar.ui.component.preference.sp.SpStringPreference
import com.yunzia.hyperstar.ui.component.preference.sp.SpSwitchFolderPreference
import com.yunzia.hyperstar.ui.component.preference.sp.SpSwitchPreference
import com.yunzia.hyperstar.ui.component.preference.sp.SpSwitchSliderPreference
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.ui.navigation.MediaRoutes
import com.yunzia.hyperstar.ui.navigation.SystemUIRoutes
import com.yunzia.hyperstar.prefs.SPUtils
import com.yunzia.hyperstar.utils.Helper
import SearchRoute
import androidx.activity.compose.LocalActivity
import com.yunzia.hyperstar.MainActivity

@SearchRoute(route = SystemUIRoutes.Media::class)
@Composable
fun MediaSettingsScreen() {
    val navController = LocalNavigator.current
    val activity = LocalActivity.current as MainActivity
    PreferenceScreen(
        title = stringResource(R.string.media_settings),
        navController = navController,
        endClick = {
            Helper.rootShell("killall com.android.systemui")
        },
        scrollToKey = activity.appViewModel.scrollToKey.value,
        onScrollComplete = { activity.appViewModel.scrollToKey.value = null },
    ) { _, _ ->
        preferenceGroup(R.string.base_settings) {
            SearchableNavPreference(
                key = "media_default_app_settings_nav",
                title = stringResource(R.string.media_default_app_settings),
                onClick = { navController.navigate(MediaRoutes.MediaApp) }
            )
            SpStringPreference(
                title = stringResource(R.string.title_miplay_detail_header_no_song),
                key = "miplay_detail_header_no_song"
            )
        }
        preferenceGroup(R.string.mipalyer_normal) {
            SpSwitchFolderPreference(
                key = "is_hide_cover",
                title = stringResource(R.string.is_hide_cover_title),
                content = {
                    SpSwitchPreference(
                        title = stringResource(R.string.is_title_center_title),
                        key = "is_title_center"
                    )
                }
            )
            SpSwitchPreference(
                title = stringResource(R.string.is_title_marquee_title),
                key = "is_title_marquee"
            )
            SpSwitchPreference(
                title = stringResource(R.string.is_artist_marquee_title),
                key = "is_artist_marquee"
            )
            SpSwitchPreference(
                title = stringResource(R.string.is_emptyState_marquee_title),
                key = "is_emptyState_marquee"
            )
            ContentDropdown(
                title = stringResource(R.string.media_background_style_title),
                key = "media_background_style",
                option = R.array.media_background_style_entire
            ) { state ->
                AnimatedVisibility(
                    (state.value == 2),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        SpSwitchSliderPreference(
                            switchKey = "is_cover_scale_background",
                            switchTitle = stringResource(R.string.is_cover_scale_background_title),
                            key = "cover_scale_background_value",
                            title = stringResource(R.string.cover_scale_background_value_title),
                            unit = "f",
                            minValue = 1.1f,
                            maxValue = 2f,
                            defaultValue = 1.5f,
                            decimalPlaces = 2,
                        )
                        SpSwitchSliderPreference(
                            switchKey = "is_cover_blur_background",
                            switchTitle = stringResource(R.string.is_cover_blur_background_title),
                            key = "cover_blur_background_value",
                            title = stringResource(R.string.cover_blur_background_value_title),
                            unit = "f",
                            minValue = 0f,
                            maxValue = 60f,
                            defaultValue = 50f,
                            decimalPlaces = 2,
                        )
                        SpSwitchSliderPreference(
                            switchKey = "is_cover_dim_background",
                            switchTitle = stringResource(R.string.is_cover_dim_background_title),
                            key = "cover_dim_background_value",
                            title = stringResource(R.string.cover_dim_background_value_title),
                            unit = "f",
                            minValue = 0f,
                            maxValue = 100f,
                            defaultValue = 0f,
                        )
                        SpSwitchPreference(
                            title = stringResource(R.string.cover_anciently_title),
                            key = "cover_anciently"
                        )
                    }
                }
            }
        }

        preferenceGroup(R.string.miplayer_expand) {
            SpSwitchPreference(
                title = stringResource(R.string.title_qs_detail_app_icon_radius),
                key = "qs_detail_app_icon_radius"
            )
            SpSwitchPreference(
                title = stringResource(R.string.title_qs_detail_progress_bg_radius),
                key = "qs_detail_progress_bg_radius"
            )
            SpSwitchPreference(
                title = stringResource(R.string.title_detail_volumebar_show_value),
                key = "is_detail_volumebar_show_value"
            )
            val speakerOptions = stringArrayResource(R.array.is_local_speaker_entire).toList()
            ListPreference(
                title = stringResource(R.string.is_local_speaker_title),
                entries = speakerOptions,
                entryValues = speakerOptions.indices.map { it.toString() },
                value = "0",
                onValueChange = { SPUtils.putInt("is_local_speaker", it.toIntOrNull() ?: 0) },
            )
        }
    }
}
