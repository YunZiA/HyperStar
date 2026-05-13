package com.yunzia.hyperstar.ui.screen.module.systemui.other

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.SuperGroupPosition
import com.yunzia.hyperstar.ui.component.preference.PreferenceList
import com.yunzia.hyperstar.ui.component.preference.core.SearchableNavPreference
import com.yunzia.hyperstar.ui.component.preference.preferenceGroup
import com.yunzia.hyperstar.ui.component.preference.sp.SpColorPickerPreference
import com.yunzia.hyperstar.ui.component.preference.sp.SpSwitchPreference
import SearchRoute
import com.yunzia.hyperstar.ui.navigation.MainRoutes
import com.yunzia.hyperstar.ui.navigation.Navigator
import com.yunzia.hyperstar.ui.navigation.SystemUIRoutes
import com.yunzia.hyperstar.utils.getSettingChannel
import com.yunzia.hyperstar.utils.isOS2Settings
import top.yukonga.miuix.kmp.basic.ScrollBehavior

@SearchRoute(route = MainRoutes.SystemUI::class, tabIndex = 2)
@Composable
fun SystemUIOtherPager(
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
            title = R.string.status_bar,
            position = SuperGroupPosition.FIRST,
        ) {
            SpSwitchPreference(
                title = stringResource(R.string.transparent_statusBar_background),
                summary = stringResource(R.string.transparent_statusBar_background_summary),
                key = "is_transparent_statusBar_background"
            )
        }
        preferenceGroup(
            title = R.string.navigation_bar,
        ) {
            SpSwitchPreference(
                title = stringResource(R.string.transparent_navigationBar_background),
                summary = stringResource(R.string.transparent_statusBar_background_summary),
                key = "is_transparent_navigationBar_background"
            )
        }
        if (getSettingChannel() >= 2) {
            preferenceGroup(
                title = R.string.classic_noy_type,
            ) {
                SearchableNavPreference(
                    key = "icon_stacking_whitelist_nav",
                    title = stringResource(R.string.icon_stacking_whitelist),
                    onClick = { navController.navigate(SystemUIRoutes.NotificationOfIm) }
                )
            }
        }
        preferenceGroup(
            title = R.string.power_menu,
        ) {
            SpSwitchPreference(
                title = stringResource(R.string.is_power_menu_nav_show_title),
                key = "is_power_menu_nav_show"
            )
            SearchableNavPreference(
                key = "power_menu_extra_nav",
                title = stringResource(R.string.power_menu_extra),
                onClick = { navController.navigate(SystemUIRoutes.PowerMenu) }
            )
        }
        preferenceGroup(
            title = R.string.other_settings,
            position = SuperGroupPosition.LAST,
        ) {
            SpColorPickerPreference(
                title = stringResource(R.string.low_device_qc_background_color),
                key = "low_device_qc_background_color"
            )
            if (isOS2Settings()) {
                SpColorPickerPreference(
                    title = stringResource(R.string.notification_expansion_overlay_color_on_low_end_devices),
                    key = "low_device_not_second_background_color"
                )
            }
        }
    }
}
