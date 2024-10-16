package com.chaos.hyperstar.ui.module.betahome

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ModuleNavPagers
import com.chaos.hyperstar.ui.base.ModulePagers
import com.chaos.hyperstar.ui.base.XSuperSwitch
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.utils.Utils


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