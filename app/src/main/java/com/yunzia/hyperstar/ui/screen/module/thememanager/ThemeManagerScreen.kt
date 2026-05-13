package com.yunzia.hyperstar.ui.screen.module.thememanager

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.preference.PreferenceScreen
import com.yunzia.hyperstar.ui.component.preference.preferenceGroup
import com.yunzia.hyperstar.ui.component.preference.sp.SpSwitchPreference
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.ui.navigation.MainRoutes
import com.yunzia.hyperstar.utils.Helper
import SearchRoute
import androidx.activity.compose.LocalActivity
import com.yunzia.hyperstar.MainActivity

@SearchRoute(route = MainRoutes.ThemeManager::class)
@Composable
fun ThemeManagerScreen() {
    val navController = LocalNavigator.current
    val activity = LocalActivity.current as MainActivity
    PreferenceScreen(
        title = stringResource(R.string.thememanager),
        navController = navController,
        endClick = {
            Helper.rootShell("killall com.android.thememanager")
        },
        scrollToKey = activity.appViewModel.scrollToKey.value,
        onScrollComplete = { activity.appViewModel.scrollToKey.value = null },
    ) { _, _ ->
        preferenceGroup(R.string.basics) {
            SpSwitchPreference(
                title = stringResource(R.string.unlock_ai_wallpaper),
                key = "is_unlock_ai_wallpaper"
            )
        }
    }
}
