package com.yunzia.hyperstar.ui.base.pager

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import com.yunzia.hyperstar.ui.base.ModuleNavTopAppBar
import com.yunzia.hyperstar.ui.base.XScaffold
import com.yunzia.hyperstar.ui.base.modifier.blur
import com.yunzia.hyperstar.ui.base.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.base.modifier.showBlur
import com.yunzia.hyperstar.ui.base.nav.backParentPager
import dev.chrisbanes.haze.HazeState
import top.yukonga.miuix.kmp.basic.FabPosition
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun ModuleNavPagers(
    activityTitle: String,
    navController: NavController,
    parentRoute: MutableState<String>,
    startClick: () -> Unit =  {
        navController.backParentPager(parentRoute.value)
    },
    endClick: () -> Unit,
    endIcon:  @Composable () -> Unit = {},
    content: LazyListScope.() -> Unit
) {

    ModuleNavPager(
        activityTitle = activityTitle,
        navController = navController,
        parentRoute = parentRoute,
        startClick = startClick,
        endClick = endClick,
        endIcon = endIcon,
    ){ topAppBarScrollBehavior,padding->
        LazyColumn(
            modifier = Modifier.height(getWindowSize().height.dp)
                .nestedOverScrollVertical(topAppBarScrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(top = padding.calculateTopPadding()+14.dp, bottom = padding.calculateBottomPadding()+28.dp)
        ) {
            content()
        }
    }
}


@Composable
fun ModuleNavPager(
    activityTitle: String,
    navController: NavController,
    parentRoute: MutableState<String>,
    floatingActionButton: @Composable () -> Unit = {},
    floatingPagerButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    startClick: () -> Unit = {
        navController.backParentPager(parentRoute.value)
    },
    endClick: () -> Unit,
    endIcon:  @Composable () -> Unit = {},
    contents: @Composable ((ScrollBehavior, PaddingValues) -> Unit)? = null
) {

    val hazeState = remember { HazeState() }
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())

    XScaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        floatingPagerButton = floatingPagerButton,
        popupHost = { },
        topBar = {
            ModuleNavTopAppBar(
                modifier = Modifier.showBlur(hazeState),
                color = Color.Transparent,
                title = activityTitle,
                scrollBehavior = topAppBarScrollBehavior,
                startClick = startClick,
                endIcon = endIcon,
                endClick = {
                    endClick()
                }
            )
        }

    ) { padding ->
        BackHandler(true) {
            navController.backParentPager(parentRoute.value)
        }
        if (contents != null) {
            Box(
                Modifier.blur(hazeState)
            ) {
                contents(topAppBarScrollBehavior,padding)
            }
        }

    }

}