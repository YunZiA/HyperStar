package com.yunzia.hyperstar.ui.component.pager

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.yunzia.hyperstar.ui.component.topbar.ModuleNavTopAppBar
import com.yunzia.hyperstar.ui.component.XScaffold
import com.yunzia.hyperstar.ui.component.modifier.blur
import com.yunzia.hyperstar.ui.component.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.component.modifier.showBlur
import dev.chrisbanes.haze.HazeState
import top.yukonga.miuix.kmp.basic.FabPosition
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import com.yunzia.hyperstar.ui.navigation.Navigator

@Composable
fun ModuleNavPagers(
    activityTitle: String,
    navController: Navigator,
    startClick: () -> Unit =  {
        navController.goBack()
    },
    endClick: () -> Unit,
    endIcon:  @Composable () -> Unit = {},
    content: LazyListScope.() -> Unit
) {

    ModuleNavPager(
        activityTitle = activityTitle,
        navController = navController,
        startClick = startClick,
        endClick = endClick,
        endIcon = endIcon,
    ){ topAppBarScrollBehavior,padding->
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .nestedOverScrollVertical(topAppBarScrollBehavior.nestedScrollConnection)
            ,
            contentPadding = padding
        ) {
            content()
        }
    }
}


@Composable
fun ModuleNavPager(
    activityTitle: String,
    navController: Navigator,
    floatingActionButton: @Composable () -> Unit = {},
    floatingPagerButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    startClick: () -> Unit = {
        navController.goBack()
    },
    endClick: () -> Unit,
    endIcon: @Composable () -> Unit = {},
    contents: @Composable ((ScrollBehavior, PaddingValues) -> Unit)? = null,
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
        if (contents != null) {
            Box(
                Modifier.blur(hazeState)
            ) {
                contents(topAppBarScrollBehavior,padding)
            }
        }

    }

}