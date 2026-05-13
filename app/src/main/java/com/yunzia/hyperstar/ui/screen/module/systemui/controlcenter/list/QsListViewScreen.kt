package com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.preference.PreferenceScreen
import com.yunzia.hyperstar.ui.component.preference.core.ListPreference
import com.yunzia.hyperstar.ui.component.preference.preferenceGroup
import com.yunzia.hyperstar.ui.component.preference.sp.SpSliderPreference
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.ui.navigation.SystemUIRoutes
import com.yunzia.hyperstar.prefs.SPUtils
import com.yunzia.hyperstar.utils.Helper
import SearchRoute
import androidx.activity.compose.LocalActivity
import com.yunzia.hyperstar.MainActivity

@SearchRoute(route = SystemUIRoutes.TileLayout::class)
@Composable
fun QsListViewScreen() {
    val navController = LocalNavigator.current
    val activity = LocalActivity.current as MainActivity
    PreferenceScreen(
        title = stringResource(R.string.tile_layout),
        navController = navController,
        endClick = {
            Helper.rootShell("killall com.android.systemui")
        },
        scrollToKey = activity.appViewModel.scrollToKey.value,
        onScrollComplete = { activity.appViewModel.scrollToKey.value = null },
    ) { _, _ ->
        preferenceGroup {
            val wordlessOptions = stringArrayResource(R.array.is_wordless_mode_entire).toList()
            ListPreference(
                title = stringResource(R.string.wordless_mode),
                entries = wordlessOptions,
                entryValues = wordlessOptions.indices.map { it.toString() },
                value = "0",
                onValueChange = { SPUtils.putInt("is_wordless_mode_2", it.toIntOrNull() ?: 0) },
            )
        }

        preferenceGroup(R.string.title_style) {
            SpSliderPreference(
                title = stringResource(R.string.title_size),
                key = "list_label_size",
                valueRange = 0f..25f,
                defaultValue = 13f,
                decimalPlaces = 2,
                valueFormatter = { "${it}dp" }
            )
            SpSliderPreference(
                title = stringResource(R.string.title_width),
                key = "list_label_width",
                valueRange = 0f..100f,
                defaultValue = 100f,
                valueFormatter = { "${it.toInt()}%" }
            )
        }
        preferenceGroup(R.string.vertical_spacing) {
            SpSliderPreference(
                title = stringResource(R.string.disable_icon_labels),
                key = "list_spacing_y",
                valueRange = 0f..150f,
                defaultValue = 100f,
                valueFormatter = { "${it.toInt()}%" }
            )
            SpSliderPreference(
                title = stringResource(R.string.enable_icon_labels),
                key = "list_label_spacing_y",
                valueRange = 0f..150f,
                defaultValue = 100f,
                valueFormatter = { "${it.toInt()}%" }
            )
        }
        preferenceGroup(R.string.margin_top) {
            SpSliderPreference(
                title = stringResource(R.string.icon),
                key = "list_icon_top",
                valueRange = -50f..50f,
                defaultValue = 0f,
                valueFormatter = { "${it.toInt()}%" }
            )
            SpSliderPreference(
                title = stringResource(R.string.title),
                key = "list_label_top",
                valueRange = -100f..100f,
                defaultValue = 0f,
                decimalPlaces = 1,
                valueFormatter = { "${it}dp" }
            )
        }
    }
}
