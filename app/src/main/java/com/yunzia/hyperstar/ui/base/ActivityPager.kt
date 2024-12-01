package com.yunzia.hyperstar.ui.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.ui.base.modifier.blur
import com.yunzia.hyperstar.ui.base.modifier.showBlur
import com.yunzia.hyperstar.ui.pagers.FPSMonitor
import com.yunzia.hyperstar.utils.PreferencesUtil
import dev.chrisbanes.haze.HazeState
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
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

    val showFPSMonitor = remember { mutableStateOf(PreferencesUtil.getBoolean("show_FPS_Monitor",false)) }

    XScaffold(
        modifier = Modifier.fillMaxSize(),
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


    if (showFPSMonitor.value) {
        FPSMonitor(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 28.dp)
        )
    }
}
@Composable
fun ModuleNavPagers(
    activityTitle: String,
    navController: NavController,
    startClick: () -> Unit =  { navController.popBackStack() },
    endClick: () -> Unit,
    endIcon :  @Composable () -> Unit = {},
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
    startClick: () -> Unit = { navController.popBackStack() },
    endClick: () -> Unit,
    endIcon :  @Composable () -> Unit = {},
    contents: @Composable ((ScrollBehavior, PaddingValues) -> Unit)? = null
) {

    val hazeState = remember { HazeState() }
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())

    val showFPSMonitor = remember { mutableStateOf(PreferencesUtil.getBoolean("show_FPS_Monitor",false)) }

    XScaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
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

        },

        ) { padding ->
        if (contents != null) {
            Box(Modifier.blur(hazeState)) {
                contents(topAppBarScrollBehavior,padding)

            }
        }

    }



    if (showFPSMonitor.value) {
        FPSMonitor(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 28.dp)
        )
    }


}

fun NavHostController.goBackRouteWithParams(
    route: String,
    autoPop: Boolean = true,
    callback: (Bundle.() -> Unit)? = null,
) {
    getBackStackEntry(route).arguments?.let {
        callback?.invoke(it)
    }
    if (autoPop) {
        popBackStack()
    }
}

fun NavHostController.goBackWithParams(
    autoPop: Boolean = true,
    callback: (Bundle.() -> Unit)? = null,
) {
    previousBackStackEntry?.arguments?.let {
        callback?.invoke(it)
    }
    if (autoPop) {
        popBackStack()
    }
}

@Composable
fun NavPager(
    activityTitle: String,
    navController: NavController,
    actions: @Composable() (RowScope.() -> Unit) = {},
    content: (LazyListScope.(MutableState<Boolean>) -> Unit)? = null
) {

    val hazeState = remember { HazeState() }
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())

    val showFPSMonitor = remember { mutableStateOf(PreferencesUtil.getBoolean("show_FPS_Monitor",false)) }


    XScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            NavTopAppBar(
                modifier = Modifier.showBlur(hazeState),
                color = Color.Transparent,
                title = activityTitle,
                scrollBehavior = topAppBarScrollBehavior,
                navController = navController,
                actions = actions
            )

        },

        ) { padding ->
        if (content != null) {
            Box(Modifier.blur(hazeState)) {
                LazyColumn(
                    modifier = Modifier.height(getWindowSize().height.dp),
                    contentPadding = PaddingValues(top = padding.calculateTopPadding()+14.dp, bottom = padding.calculateBottomPadding()+28.dp),
                    topAppBarScrollBehavior = topAppBarScrollBehavior
                ) {
                    content(showFPSMonitor)
                }

            }
        }

    }


    if (showFPSMonitor.value) {
        FPSMonitor(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 28.dp)
        )
    }
}


