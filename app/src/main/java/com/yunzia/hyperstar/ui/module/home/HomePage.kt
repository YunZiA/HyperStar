package com.yunzia.hyperstar.ui.module.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.ModuleNavPagers
import com.yunzia.hyperstar.ui.base.XSuperSwitch
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.utils.Utils

@Composable
fun HomePage(
    navController: NavController,
    currentStartDestination: SnapshotStateList<String>
) {
    ModuleNavPagers(
        activityTitle = stringResource(R.string.hyper_home),
        currentStartDestination = currentStartDestination,
        navController = navController,
        endClick = {
            Utils.rootShell("killall com.miui.home")
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