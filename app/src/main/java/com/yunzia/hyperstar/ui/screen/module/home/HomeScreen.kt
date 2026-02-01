package com.yunzia.hyperstar.ui.screen.module.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.SuperGroupPosition
import com.yunzia.hyperstar.ui.component.XSuperSwitch
import com.yunzia.hyperstar.ui.component.itemGroup
import com.yunzia.hyperstar.ui.component.pager.ModuleNavPagers
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.utils.Helper

@Composable
fun HomeScreen() {
    val navController = LocalNavigator.current
    ModuleNavPagers(
        activityTitle = stringResource(R.string.hyper_home),
        navController = navController,
        endClick = {
            Helper.rootShell("killall com.miui.home")
        },
    ){
        itemGroup(
            title = R.string.basics,
            position = SuperGroupPosition.FIRST
        ) {
            XSuperSwitch(
                title = stringResource(R.string.remove_no_support_blur_device),
                key = "is_unlock_home_blur"
            )

        }
    }

}