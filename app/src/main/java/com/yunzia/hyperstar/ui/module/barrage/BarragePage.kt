package com.yunzia.hyperstar.ui.module.barrage

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
fun BarragePage(
    navController: NavController,
    currentStartDestination: MutableState<String>
) {

    ModuleNavPagers(
        activityTitle = stringResource(R.string.barrage),
        parentRoute = currentStartDestination,
        navController = navController,
        endClick = {
            Helper.rootShell("killall com.xiaomi.barrage")
        },
    ){
        firstClasses(
            title = R.string.basics
        ) {
            XSuperSwitch(
                title = stringResource(R.string.disable_click_events),
                key = "is_disable_barrage_click"
            )

        }
    }

}
