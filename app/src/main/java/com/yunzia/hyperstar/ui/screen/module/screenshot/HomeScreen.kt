package com.yunzia.hyperstar.ui.screen.module.screenshot

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.XSuperSwitch
import com.yunzia.hyperstar.ui.component.firstClasses
import com.yunzia.hyperstar.ui.component.pager.ModuleNavPagers
import com.yunzia.hyperstar.utils.Helper

@Composable
fun ScreenshotScreen(
    navController: NavController,
    currentStartDestination: MutableState<String>
) {
    val activity = LocalActivity.current as MainActivity
    ModuleNavPagers(
        activityTitle = activity.appInfo["com.miui.screenshot"]!!.appName,
        parentRoute = currentStartDestination,
        navController = navController,
        endClick = {
            Helper.rootShell("killall com.miui.screenshot")
        },
    ){
        firstClasses(
            title = R.string.basics
        ) {
            XSuperSwitch(
                title = stringResource(R.string.enable_clipboard_write_on_screenshot),
                key = "enable_clipboard_write_on_screenshot"
            )

        }
    }

}