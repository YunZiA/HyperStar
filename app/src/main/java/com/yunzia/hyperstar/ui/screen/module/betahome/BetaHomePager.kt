package com.yunzia.hyperstar.ui.screen.module.betahome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.pager.ModuleNavPagers
import com.yunzia.hyperstar.ui.component.XSuperSwitch
import com.yunzia.hyperstar.ui.component.firstClasses
import com.yunzia.hyperstar.utils.Helper


@Composable
fun BetaHomePager(
    navController: NavController,
    parentRoute: MutableState<String>,
) {
    ModuleNavPagers(
        activityTitle = stringResource(R.string.beta_home),
        navController = navController,
        parentRoute = parentRoute,
        endClick = {
            Helper.rootShell("killall com.miui.home")
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