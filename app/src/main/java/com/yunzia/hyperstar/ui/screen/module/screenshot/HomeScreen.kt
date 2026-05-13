package com.yunzia.hyperstar.ui.screen.module.screenshot

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

@SearchRoute(route = MainRoutes.Screenshot::class)
@Composable
fun ScreenshotScreen() {
    val navController = LocalNavigator.current
    val activity = LocalActivity.current as MainActivity
    PreferenceScreen(
        title = activity.appViewModel.visibleEntryMap["com.miui.screenshot"]?.appName
            ?: MainRoutes.Screenshot.displayName(),
        navController = navController,
        endClick = {
            Helper.rootShell("killall com.miui.screenshot")
        },
        scrollToKey = activity.appViewModel.scrollToKey.value,
        onScrollComplete = { activity.appViewModel.scrollToKey.value = null },
    ) { _, _ ->
        preferenceGroup(R.string.basics) {
            SpSwitchPreference(
                title = stringResource(R.string.enable_clipboard_write_on_screenshot),
                key = "enable_clipboard_write_on_screenshot"
            )
        }
    }
}
