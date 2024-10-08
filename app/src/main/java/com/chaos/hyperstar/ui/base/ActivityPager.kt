package com.chaos.hyperstar.ui.base

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.chaos.hyperstar.ui.base.XPopupUtil.Companion.MiuixPopupHost
import com.chaos.hyperstar.ui.pagers.FPSMonitor
import com.chaos.hyperstar.utils.PreferencesUtil
import dev.chrisbanes.haze.HazeState
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun ActivityPagers(
    activityTitle: String,
    activity: ComponentActivity,
    endClick: () -> Unit,
    endIcon :  @Composable () -> Unit = {},
    content: LazyListScope.() -> Unit
) {

    ActivityPager(
        activityTitle = activityTitle,
        activity = activity,
        endClick = endClick,
        endIcon = endIcon,
    ){ topAppBarScrollBehavior,padding,enableOverScroll->
        LazyColumn(
            modifier = Modifier.height(getWindowSize().height.dp),
            enableOverScroll = enableOverScroll,
            contentPadding = PaddingValues(top = padding.calculateTopPadding()+14.dp, bottom = padding.calculateBottomPadding()+28.dp),
            topAppBarScrollBehavior = topAppBarScrollBehavior
        ) {
            content()
        }
    }
}

@Composable
fun ActivityPager(
    activityTitle: String,
    activity: ComponentActivity,
    endClick: () -> Unit,
    endIcon :  @Composable () -> Unit = {},
    contents: @Composable ((ScrollBehavior, PaddingValues, Boolean) -> Unit)? = null
) {

    val hazeState = remember { HazeState() }
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())

    val showFPSMonitor = remember { mutableStateOf(PreferencesUtil.getBoolean("show_FPS_Monitor",false)) }
    val enableOverScroll = remember { mutableStateOf(PreferencesUtil.getBoolean("over_scroll",false)) }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SubMiuixTopAppBar(
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
                contents(topAppBarScrollBehavior,padding,enableOverScroll.value)

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


