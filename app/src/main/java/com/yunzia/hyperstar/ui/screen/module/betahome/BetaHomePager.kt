package com.yunzia.hyperstar.ui.screen.module.betahome

import IgnoreSearchIndex
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.preference.PreferenceScreen
import com.yunzia.hyperstar.ui.component.preference.preferenceGroup
import com.yunzia.hyperstar.ui.component.preference.sp.SpSwitchPreference
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.utils.Helper

@IgnoreSearchIndex
@Composable
fun BetaHomePager(
    parentRoute: MutableState<String>,
) {
    val navController = LocalNavigator.current
    PreferenceScreen(
        title = stringResource(R.string.beta_home),
        navController = navController,
        endClick = {
            Helper.rootShell("killall com.miui.home")
        },
    ) { _, _ ->
        preferenceGroup(R.string.unlock) {
            SpSwitchPreference(
                title = stringResource(R.string.is_use_beta_home_cc_title),
                summary = stringResource(R.string.is_use_beta_home_cc_summary),
                key = "is_use_beta_home_cc"
            )
        }
    }
}
