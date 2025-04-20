package com.yunzia.hyperstar.ui.base

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.base.modifier.blur
import com.yunzia.hyperstar.ui.base.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.base.modifier.showBlur
import dev.chrisbanes.haze.HazeState
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
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
            modifier = Modifier.height(getWindowSize().height.dp)
                .nestedOverScrollVertical(topAppBarScrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(top = padding.calculateTopPadding()+14.dp, bottom = padding.calculateBottomPadding()+28.dp),
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

        }
    ) { padding ->
        if (contents != null) {
            Box(Modifier.blur(hazeState)) {
                contents(topAppBarScrollBehavior,padding)

            }
        }

    }

}






