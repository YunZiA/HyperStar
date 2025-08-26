package com.yunzia.hyperstar.ui.screen.module.mms

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.SuperGroupPosition
import com.yunzia.hyperstar.ui.component.XSuperSwitch
import com.yunzia.hyperstar.ui.component.itemGroup
import com.yunzia.hyperstar.ui.component.pager.ModuleNavPagers
import com.yunzia.hyperstar.utils.Helper

@Composable
fun MMSScreen(
    navController: NavController,
    currentStartDestination: MutableState<String>
) {
    val activity = LocalActivity.current as MainActivity
    ModuleNavPagers(
        activityTitle = activity.appInfo["com.android.mms"]!!.appName,
        parentRoute = currentStartDestination,
        navController = navController,
        endClick = {
            Helper.rootShell("killall com.android.mms")
        },
    ){
        itemGroup(
            title = R.string.basics,
            position = SuperGroupPosition.FIRST
        ) {
            XSuperSwitch(
                title = stringResource(R.string.auto_copy_verification_code_to_clipboard),
                key = "auto_copy_verification_code"
            )

        }
    }

}