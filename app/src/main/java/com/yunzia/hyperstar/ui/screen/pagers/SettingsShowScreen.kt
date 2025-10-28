package com.yunzia.hyperstar.ui.screen.pagers

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.PMiuixSuperDropdown
import com.yunzia.hyperstar.ui.component.PMiuixSuperSwitch
import com.yunzia.hyperstar.ui.component.itemGroup
import com.yunzia.hyperstar.ui.component.pager.NavPager
import com.yunzia.hyperstar.utils.PreferencesUtil

@Composable
fun SettingsShowScreen(
    navController: NavHostController,
    currentStartDestination: MutableState<String>
) {

    val context = LocalContext.current
    val activity = LocalActivity.current as MainActivity
    val showFPSMonitor = activity.showFPSMonitor
    val rebootStyle = activity.rebootStyle

    NavPager(
        activityTitle = stringResource(R.string.model_pager_setting),
        navController = navController,
        parentRoute = currentStartDestination,
    ) {

        itemGroup(
            title = R.string.global
        ) {
            PMiuixSuperSwitch(
                title = stringResource(R.string.show_FPS_Monitor_title),
                checked = showFPSMonitor.value,
                onCheckedChange = {
                    showFPSMonitor.value = it
                    PreferencesUtil.putBoolean("show_FPS_Monitor",showFPSMonitor.value)
                }
            )

        }
        this.itemGroup(
            title = R.string.main_page_title
        ){
            PMiuixSuperDropdown(
                title = stringResource(R.string.title_reboot_menus_style),
                option = R.array.reboot_menus_style,
                selectedIndex = rebootStyle.intValue,
                onSelectedIndexChange = {
                    rebootStyle.intValue = it
                    PreferencesUtil.putInt("reboot_menus_style", rebootStyle.intValue)
                }
            )
            PMiuixSuperSwitch(
                title = stringResource(R.string.page_user_scroll_title),
                checked = activity.enablePageUserScroll.value,
                onCheckedChange = {
                    activity.enablePageUserScroll.value = it
                    PreferencesUtil.putBoolean("page_user_scroll",activity.enablePageUserScroll.value)
                }
            )
    //            PMiuixSuperSwitch(
    //                title = stringResource(R.string.page_user_scroll_title),
    //                key = "page_user_scroll",
    //            )

        }
        this.itemGroup(
            title = R.string.setting_item
        ) {
            PMiuixSuperSwitch(
                title = stringResource(R.string.click_bounce),
                key = "bounce_anim_enable",
                defValue = true
            )

            PMiuixSuperSwitch(
                title = stringResource(R.string.progress_effect_title),
                key = "is_progress_effect"
            )
        }

    }

}