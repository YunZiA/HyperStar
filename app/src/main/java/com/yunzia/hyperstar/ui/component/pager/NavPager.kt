package com.yunzia.hyperstar.ui.component.pager

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.yunzia.hyperstar.ui.component.topbar.NavTopAppBar
import com.yunzia.hyperstar.ui.component.XScaffold
import com.yunzia.hyperstar.ui.component.modifier.blur
import com.yunzia.hyperstar.ui.component.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.component.modifier.showBlur
import com.yunzia.hyperstar.ui.component.nav.backParentPager
import dev.chrisbanes.haze.HazeState
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.getWindowSize


@Composable
fun NavPager(
    activityTitle: String,
    navController: NavController,
    parentRoute: MutableState<String>,
    actions: @Composable() (RowScope.() -> Unit) = {},
    content: (LazyListScope.() -> Unit)? = null
) {

    val hazeState = remember { HazeState() }
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
                parentRoute = parentRoute,
                actions = actions
            )

        }
    ) { padding ->
        if (content != null) {
            BackHandler(true) {
                navController.backParentPager(parentRoute.value)
            }
            Box(Modifier.blur(hazeState)) {
                LazyColumn(
                    modifier = Modifier.height(getWindowSize().height.dp)
                        .nestedOverScrollVertical(topAppBarScrollBehavior.nestedScrollConnection),
                    contentPadding = PaddingValues(top = padding.calculateTopPadding()+14.dp, bottom = padding.calculateBottomPadding()+28.dp),
                ) {
                    content()
                }

            }
        }

    }

}