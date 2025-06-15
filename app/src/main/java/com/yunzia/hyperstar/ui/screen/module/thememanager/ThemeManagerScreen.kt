package com.yunzia.hyperstar.ui.screen.module.thememanager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.XSuperSwitch
import com.yunzia.hyperstar.ui.component.firstClasses
import com.yunzia.hyperstar.ui.component.pager.ModuleNavPagers
import com.yunzia.hyperstar.utils.Helper

@Composable
fun ThemeManagerScreen(
    navController: NavController,
    currentStartDestination: MutableState<String>
) {
    ModuleNavPagers(
        activityTitle = stringResource(R.string.thememanager),
        parentRoute = currentStartDestination,
        navController = navController,
        endClick = {
            Helper.rootShell("killall com.android.thememanager")
        },
    ){
        firstClasses(
            title = R.string.basics
        ) {
            XSuperSwitch(
                title = stringResource(R.string.unlock_ai_wallpaper),
                key = "is_unlock_ai_wallpaper"
            )

        }
    }

}