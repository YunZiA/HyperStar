package com.yunzia.hyperstar.ui.screen.module.mms

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.SuperGroupPosition
import com.yunzia.hyperstar.ui.component.XSuperSwitch
import com.yunzia.hyperstar.ui.component.itemGroup
import com.yunzia.hyperstar.ui.component.pager.ModuleNavPagers
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.utils.Helper
import top.yukonga.miuix.kmp.basic.Card
import androidx.compose.runtime.collectAsState

@Composable
fun MMSScreen() {
    val activity = LocalActivity.current as MainActivity
    val navController = LocalNavigator.current
    ModuleNavPagers(
        activityTitle = activity.appViewModel.appInScope.collectAsState().value["com.android.mms"]!!.appName,
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