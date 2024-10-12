package com.chaos.hyperstar.ui.module.systemui.other

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ModulePagers
import com.chaos.hyperstar.ui.base.XSuperSwitch
import com.chaos.hyperstar.ui.base.classes
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.utils.Utils

@Composable
fun SystemUIOtherPager(activity: ComponentActivity,
) {
    ModulePagers(
        activityTitle = stringResource(R.string.more),
        activity = activity,
        endClick = {
            Utils.rootShell("killall com.android.systemui")
        },
    ) {
        firstClasses(
            title = R.string.status_bar,
        ) {
            XSuperSwitch(
                title = stringResource(R.string.transparent_statusBar_background),
                summary = stringResource(R.string.transparent_statusBar_background_summary),
                key = "is_transparent_statusBar_background"
            )
        }
        classes(
            title = R.string.navigation_bar,
        ) {
            XSuperSwitch(
                title = stringResource(R.string.transparent_navigationBar_background),
                summary = stringResource(R.string.transparent_statusBar_background_summary),
                key = "is_transparent_navigationBar_background"
            )
        }
    }
}