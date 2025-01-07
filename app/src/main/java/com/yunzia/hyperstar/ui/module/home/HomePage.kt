package com.yunzia.hyperstar.ui.module.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.ModuleNavPagers
import com.yunzia.hyperstar.ui.base.XSuperSwitch
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.utils.Helper

@Composable
fun HomePage(
    navController: NavController,
    currentStartDestination: MutableState<String>
) {
    ModuleNavPagers(
        activityTitle = stringResource(R.string.hyper_home),
        parentRoute = currentStartDestination,
        navController = navController,
        endClick = {
            Helper.rootShell("killall com.miui.home")
        },
    ){
        firstClasses(
            title = R.string.basics
        ) {
            XSuperSwitch(
                title = stringResource(R.string.remove_no_support_blur_device),
                key = "is_unlock_home_blur"
            )

        }
    }

}