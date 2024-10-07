package com.chaos.hyperstar.ui.module.betahome

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import com.chaos.hyperstar.ui.base.ActivityPagers
import com.chaos.hyperstar.ui.base.XSuperSwitch
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.utils.Utils


@Composable
fun BetaHomePager(activity: ComponentActivity) {
    ActivityPagers(
        activityTitle = "Beta桌面",
        activity = activity,
        endClick = {
            Utils.rootShell("killall com.miui.home")
        },
    ){
        firstClasses(title = "特性") {
            XSuperSwitch(
                title = "解锁部分beta特性",
                summary = "圆角矩形大图标小部件 && OS2设置风格",
                key = "is_use_beta_home_cc")

        }
    }

}