package com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.ItemAnim
import com.yunzia.hyperstar.ui.component.preference.PreferenceScreen
import com.yunzia.hyperstar.ui.component.preference.core.ListPreference
import com.yunzia.hyperstar.ui.component.preference.preferenceGroup
import com.yunzia.hyperstar.ui.component.preference.sp.SpColorPickerPreference
import com.yunzia.hyperstar.ui.navigation.ColorEditRoutes
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.prefs.SPUtils
import com.yunzia.hyperstar.utils.Helper
import SearchRoute
import androidx.activity.compose.LocalActivity
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.ui.component.preference.sp.SpDropdownPreference

@SearchRoute(route = ColorEditRoutes.ListColor::class)
@Composable
fun QSListColorScreen() {
    val navController = LocalNavigator.current
    val activity = LocalActivity.current as MainActivity
    val waitTime = 105L
    var tileColor by remember { mutableIntStateOf(SPUtils.getInt("qs_list_tile_color_for_state", 0)) }
    val tileOptions = stringArrayResource(R.array.qs_list_tile_color_for_state_entire).toList()

    PreferenceScreen(
        title = stringResource(R.string.tile_color),
        navController = navController,
        endClick = {
            Helper.rootShell("killall com.android.systemui")
        },
        scrollToKey = activity.appViewModel.scrollToKey.value,
        onScrollComplete = { activity.appViewModel.scrollToKey.value = null },
    ) { _, _ ->
        preferenceGroup(R.string.general) {
            SpDropdownPreference(
                key = "qs_list_tile_color_for_state",
                title = stringResource(R.string.title_color_in_state),
                entries = tileOptions
            )
            ItemAnim(
                animState = (tileColor == 0),
                waitTime = waitTime
            ) {
                SpColorPickerPreference(
                    title = stringResource(R.string.title),
                    key = "list_title_color"
                )
            }
        }

        preferenceGroup(R.string.close_state_color) {
            SpColorPickerPreference(
                title = stringResource(R.string.icon),
                key = "list_icon_off_color"
            )
            ItemAnim(
                animState = (tileColor == 2),
                waitTime = waitTime
            ) {
                SpColorPickerPreference(
                    title = stringResource(R.string.title),
                    key = "list_title_off_color"
                )
            }
        }

        preferenceGroup(R.string.enable_state_color) {
            SpColorPickerPreference(
                title = stringResource(R.string.enable_background),
                key = "list_enabled_color"
            )
            SpColorPickerPreference(
                title = stringResource(R.string.warning_background),
                key = "list_warning_color"
            )
            SpColorPickerPreference(
                title = stringResource(R.string.icon),
                key = "list_icon_on_color"
            )
            ItemAnim(
                animState = (tileColor == 2),
                waitTime = waitTime
            ) {
                SpColorPickerPreference(
                    title = stringResource(R.string.title),
                    key = "list_title_on_color"
                )
            }
        }

        preferenceGroup(R.string.restricted_state_color) {
            SpColorPickerPreference(
                title = stringResource(R.string.background),
                key = "list_restricted_color"
            )
            SpColorPickerPreference(
                title = stringResource(R.string.icon),
                key = "list_icon_restricted_color"
            )
            ItemAnim(
                animState = (tileColor == 2),
                waitTime = waitTime
            ) {
                SpColorPickerPreference(
                    title = stringResource(R.string.title),
                    key = "list_title_restricted_color"
                )
            }
        }

        preferenceGroup(R.string.unavailable_state_color) {
            SpColorPickerPreference(
                title = stringResource(R.string.background),
                key = "list_unavailable_color"
            )
            SpColorPickerPreference(
                title = stringResource(R.string.icon),
                key = "list_icon_unavailable_color"
            )
            ItemAnim(
                animState = (tileColor == 2),
                waitTime = waitTime
            ) {
                SpColorPickerPreference(
                    title = stringResource(R.string.title),
                    key = "list_title_unavailable_color"
                )
            }
        }
    }
}
