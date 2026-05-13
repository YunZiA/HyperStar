package com.yunzia.hyperstar.ui.screen.module.notDeveloper

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.preference.PreferenceScreen
import com.yunzia.hyperstar.ui.navigation.LocalNavigator

@Composable
fun NotDeveloperScopeScreen() {
    val navController = LocalNavigator.current
    PreferenceScreen(
        title = stringResource(R.string.fun_scope),
        navController = navController,
    ) { _, _ ->
    }
}
