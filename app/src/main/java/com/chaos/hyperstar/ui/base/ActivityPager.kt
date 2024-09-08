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
import androidx.compose.ui.unit.dp
import com.chaos.hyperstar.ui.pagers.FPSMonitor
import com.chaos.hyperstar.utils.PreferencesUtil
import getWindowSize
import top.yukonga.miuix.kmp.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.MiuixLazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScaffold
import top.yukonga.miuix.kmp.basic.MiuixSurface
import top.yukonga.miuix.kmp.rememberMiuixTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
fun ActivityPagers(
    activityTitle: String,
    activity: ComponentActivity,
    endClick: () -> Unit,
    content: LazyListScope.() -> Unit
) {
    ActivityPager(
        activityTitle = activityTitle,
        activity = activity,
        endClick =endClick,
    ){ topAppBarScrollBehavior,padding,enableOverScroll->
        MiuixLazyColumn(
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
    contents: @Composable ((MiuixScrollBehavior, PaddingValues, Boolean) -> Unit)? = null
) {
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberMiuixTopAppBarState())

    val showFPSMonitor = remember { mutableStateOf(PreferencesUtil.getBoolean("show_FPS_Monitor",false)) }
    val enableTopBarBlur = remember { mutableStateOf(PreferencesUtil.getBoolean("top_Bar_blur",false)) }
    val enableBottomBarBlur = remember { mutableStateOf(PreferencesUtil.getBoolean("bottom_Bar_blur",false)) }
    val enableOverScroll = remember { mutableStateOf(PreferencesUtil.getBoolean("over_scroll",false)) }

    MiuixSurface {
        MiuixScaffold(
            modifier = Modifier.fillMaxSize(),
            enableTopBarBlur = enableTopBarBlur.value,
            enableBottomBarBlur = enableBottomBarBlur.value,
            topBar = {
                SubMiuixTopAppBar(
                    color = if (enableTopBarBlur.value) Color.Transparent else colorScheme.background,
                    title = activityTitle,
                    scrollBehavior = topAppBarScrollBehavior,
                    activity = activity,
                    endClick = {
                        endClick()
                    }
                )

            },

            ) { padding ->
            if (contents != null) {
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


