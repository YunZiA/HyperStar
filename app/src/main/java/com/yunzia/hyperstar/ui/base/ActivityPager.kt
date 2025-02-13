package com.yunzia.hyperstar.ui.base

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.yunzia.hyperstar.ui.base.modifier.blur
import com.yunzia.hyperstar.ui.base.modifier.showBlur
import com.yunzia.hyperstar.ui.base.nav.backParentPager
import dev.chrisbanes.haze.HazeState
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixFabPosition
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun ModulePagers(
    activityTitle: String,
    activity: ComponentActivity,
    endClick: () -> Unit,
    endIcon :  @Composable () -> Unit = {},
    content: LazyListScope.() -> Unit
) {

    ModulePager(
        activityTitle = activityTitle,
        activity = activity,
        endClick = endClick,
        endIcon = endIcon,
    ){ topAppBarScrollBehavior,padding->
        LazyColumn(
            modifier = Modifier.height(getWindowSize().height.dp),
            contentPadding = PaddingValues(top = padding.calculateTopPadding()+14.dp, bottom = padding.calculateBottomPadding()+28.dp),
            topAppBarScrollBehavior = topAppBarScrollBehavior
        ) {
            content()
        }
    }
}

@Composable
fun ModulePager(
    activityTitle: String,
    activity: ComponentActivity,
    endClick: () -> Unit,
    endIcon :  @Composable () -> Unit = {},
    contents: @Composable ((ScrollBehavior, PaddingValues) -> Unit)? = null
) {

    val hazeState = remember { HazeState() }
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())

    XScaffold(
        modifier = Modifier.fillMaxSize(),
        popupHost = { },
        topBar = {
            ModuleTopAppBar(
                modifier = Modifier.showBlur(hazeState),
                color = Color.Transparent,
                title = activityTitle,
                scrollBehavior = topAppBarScrollBehavior,
                activity = activity,
                endIcon = endIcon,
                endClick = {
                    endClick()
                }
            )

        },

        ) { padding ->
        if (contents != null) {
            Box(Modifier.blur(hazeState)) {
                contents(topAppBarScrollBehavior,padding)

            }
        }

    }

}
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
            modifier = Modifier.height(getWindowSize().height.dp),
            contentPadding = PaddingValues(top = padding.calculateTopPadding()+14.dp, bottom = padding.calculateBottomPadding()+28.dp),
            topAppBarScrollBehavior = topAppBarScrollBehavior
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
    floatingActionButtonPosition: MiuixFabPosition = MiuixFabPosition.End,
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
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
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
            Box(Modifier.blur(hazeState)) {
                contents(topAppBarScrollBehavior,padding)

            }
        }

    }

}



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
                    modifier = Modifier.height(getWindowSize().height.dp),
                    contentPadding = PaddingValues(top = padding.calculateTopPadding()+14.dp, bottom = padding.calculateBottomPadding()+28.dp),
                    topAppBarScrollBehavior = topAppBarScrollBehavior
                ) {
                    content()
                }

            }
        }

    }

}


