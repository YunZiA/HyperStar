package com.yunzia.hyperstar.ui.module.betahome

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.ModuleNavPagers
import com.yunzia.hyperstar.ui.base.XSuperSwitch
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.utils.Utils


@Composable
fun BetaHomePager(
    navController: NavController
) {
    ModuleNavPagers(
        activityTitle = stringResource(R.string.beta_home),
        navController = navController,
        endClick = {
            Utils.rootShell("killall com.miui.home")
        },
    ){
        firstClasses(
            title = R.string.unlock
        ) {
            XSuperSwitch(
                title = stringResource(R.string.is_use_beta_home_cc_title),
                summary = stringResource(R.string.is_use_beta_home_cc_summary),
                key = "is_use_beta_home_cc"
            )

        }
    }

}