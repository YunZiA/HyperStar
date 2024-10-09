package com.chaos.hyperstar.ui.module.betahome

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ActivityPagers
import com.chaos.hyperstar.ui.base.XSuperSwitch
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.utils.Utils


@Composable
fun BetaHomePager(activity: ComponentActivity) {
    ActivityPagers(
        activityTitle = stringResource(R.string.beta_home),
        activity = activity,
        endClick = {
            Utils.rootShell("killall com.miui.home")
        },
    ){
        firstClasses(title = "特性") {
            XSuperSwitch(
                title = stringResource(R.string.is_use_beta_home_cc_title),
                summary = stringResource(R.string.is_use_beta_home_cc_summary),
                key = "is_use_beta_home_cc"
            )

        }
    }

}