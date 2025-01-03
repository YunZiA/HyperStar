package com.yunzia.hyperstar.ui.base

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.ui.base.modifier.blur
import com.yunzia.hyperstar.ui.base.modifier.showBlur
import dev.chrisbanes.haze.HazeState
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.getWindowSize
import kotlin.math.log

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
    currentStartDestination: SnapshotStateList<String>,
    startClick: () -> Unit =  {
        navController.backParentPager()
    },
    endClick: () -> Unit,
    endIcon:  @Composable () -> Unit = {},
    content: LazyListScope.() -> Unit
) {

    ModuleNavPager(
        activityTitle = activityTitle,
        navController = navController,
        currentStartDestination = currentStartDestination,
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
    currentStartDestination: SnapshotStateList<String>,
    startClick: () -> Unit = {
        navController.backParentPager()
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
            navController.backParentPager()
        }
        if (contents != null) {
            Box(Modifier.blur(hazeState)) {
                contents(topAppBarScrollBehavior,padding)

            }
        }

    }

}

fun NavController.backParentPager(){
    val ss = this.currentDestination?.route!!.substringBeforeLast("/")
    Log.d("ggc", "backParentPager: ${this.currentDestination?.route!!}\n$ss")
    this.popBackStack(ss,false)

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
    currentStartDestination: SnapshotStateList<String>,
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
                currentStartDestination = currentStartDestination,
                actions = actions
            )

        }
    ) { padding ->
        if (content != null) {
            BackHandler(true) {
                navController.backParentPager()
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


