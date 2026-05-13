package com.yunzia.hyperstar.ui.screen.module.mms

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.preference.PreferenceScreen
import com.yunzia.hyperstar.ui.component.preference.preferenceGroup
import com.yunzia.hyperstar.ui.component.preference.sp.SpSwitchPreference
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.ui.navigation.MainRoutes
import com.yunzia.hyperstar.ui.navigation.displayName
import com.yunzia.hyperstar.utils.Helper
import SearchRoute

@SearchRoute(route = MainRoutes.MMS::class)
@Composable
fun MMSScreen() {
    val activity = LocalActivity.current as MainActivity
    val navController = LocalNavigator.current
    PreferenceScreen(
        title = activity.appViewModel.visibleEntryMap["com.android.mms"]?.appName
            ?: MainRoutes.MMS.displayName(),
        navController = navController,
        endClick = {
            Helper.rootShell("killall com.android.mms")
        },
        scrollToKey = activity.appViewModel.scrollToKey.value,
        onScrollComplete = { activity.appViewModel.scrollToKey.value = null },
    ) { _, _ ->
        preferenceGroup(R.string.basics) {
            SpSwitchPreference(
                title = stringResource(R.string.auto_copy_verification_code_to_clipboard),
                key = "auto_copy_verification_code"
            )
        }
    }
}
