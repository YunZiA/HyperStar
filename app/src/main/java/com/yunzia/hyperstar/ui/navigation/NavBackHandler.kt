package com.yunzia.hyperstar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.NavigationEventState
import androidx.navigationevent.compose.rememberNavigationEventState

@Composable 
fun NavBackHandler(
    state: NavigationEventState<out NavigationEventInfo> = rememberNavigationEventState(NavigationEventInfo.None),
    isBackEnabled: Boolean = true,
    onBackCompleted: () -> Unit
) {
    NavigationBackHandler(
        state = state,
        isBackEnabled = isBackEnabled,
        onBackCompleted = onBackCompleted
    )
}