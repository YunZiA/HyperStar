package com.yunzia.hyperstar.ui.pagers

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.Classes
import com.yunzia.hyperstar.ui.base.NavPager
import com.yunzia.hyperstar.ui.base.PMiuixSuperSwitch
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.utils.PreferencesUtil

@Composable
fun SettingsShowPage(
    navController: NavHostController
) {


    NavPager(
        activityTitle = stringResource(R.string.model_pager_setting),
        navController = navController,
    ) { showFPSMonitor->

        firstClasses(
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
        classes(
            title = R.string.main_page_title
        ){
            PMiuixSuperSwitch(
                title = stringResource(R.string.page_user_scroll_title),
                key = "page_user_scroll",
            )

        }
        classes(
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