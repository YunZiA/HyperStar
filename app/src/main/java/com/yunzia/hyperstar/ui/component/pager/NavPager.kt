package com.yunzia.hyperstar.ui.component.pager

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.yunzia.hyperstar.ui.component.topbar.NavTopAppBar
import com.yunzia.hyperstar.ui.component.XScaffold
import com.yunzia.hyperstar.ui.component.modifier.blur
import com.yunzia.hyperstar.ui.component.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.component.modifier.showBlur
import com.yunzia.hyperstar.ui.navigation.Navigator
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState



@Composable
fun NavPager(
    activityTitle: String,
    navController: Navigator,
    actions: @Composable() (RowScope.() -> Unit) = {},
    content: (LazyListScope.() -> Unit)? = null
) {

    val hazeState = rememberHazeState()
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())


    XScaffold(
        modifier = Modifier.fillMaxSize(),
        popupHost = { },
        topBar = {
            NavTopAppBar(
                modifier = Modifier.showBlur(hazeState),
                color = Color.Transparent,
                title = activityTitle,
                scrollBehavior = topAppBarScrollBehavior,
                navController = navController,
                actions = actions
            )

        }
    ) { padding ->
        if (content != null) {
            Box(Modifier.blur(hazeState)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                        .nestedOverScrollVertical(topAppBarScrollBehavior.nestedScrollConnection),
                    contentPadding = PaddingValues(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding()),
                ) {
                    content()
                }

            }
        }

    }

}