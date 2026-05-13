package com.yunzia.hyperstar.ui.screen.pagers

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.PDropdown
import com.yunzia.hyperstar.ui.component.preference.widget.PreferenceListPage
import com.yunzia.hyperstar.ui.component.preference.widget.SwitchPreference
import com.yunzia.hyperstar.ui.component.preference.widget.itemGroup
import com.yunzia.hyperstar.prefs.PreferencesUtil
import com.yunzia.hyperstar.ui.navigation.LocalNavigator

@Composable
fun SettingsShowScreen() {
    val context = LocalContext.current
    val navController = LocalNavigator.current
    val activity = LocalActivity.current as MainActivity
    val showFPSMonitor = activity.showFPSMonitor
    val rebootStyle = activity.rebootStyle

    PreferenceListPage(
        title = stringResource(R.string.model_pager_setting),
        navController = navController,
    ) {
        itemGroup(R.string.global) {
            var fps by remember { mutableStateOf(showFPSMonitor.value) }
            SwitchPreference(
                title = stringResource(R.string.show_FPS_Monitor_title),
                checked = fps,
                onCheckedChange = {
                    fps = it
                    showFPSMonitor.value = it
                    PreferencesUtil.putBoolean("show_FPS_Monitor", it)
                }
            )
        }
        itemGroup(R.string.main_page_title) {
            PDropdown(
                title = stringResource(R.string.title_reboot_menus_style),
                option = R.array.reboot_menus_style,
                selectedIndex = rebootStyle.intValue,
                onSelectedIndexChange = {
                    rebootStyle.intValue = it
                    PreferencesUtil.putInt("reboot_menus_style", rebootStyle.intValue)
                }
            )
            var pageScroll by remember { mutableStateOf(activity.enablePageUserScroll.value) }
            SwitchPreference(
                title = stringResource(R.string.page_user_scroll_title),
                checked = pageScroll,
                onCheckedChange = {
                    pageScroll = it
                    activity.enablePageUserScroll.value = it
                    PreferencesUtil.putBoolean("page_user_scroll", it)
                }
            )
        }
        itemGroup(R.string.setting_item) {
            var bounceAnim by remember { mutableStateOf(PreferencesUtil.getBoolean("bounce_anim_enable", true)) }
            SwitchPreference(
                title = stringResource(R.string.click_bounce),
                checked = bounceAnim,
                onCheckedChange = {
                    bounceAnim = it
                    PreferencesUtil.putBoolean("bounce_anim_enable", it)
                }
            )
        }
    }
}
