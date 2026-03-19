package com.yunzia.hyperstar.ui.screen.module.notDeveloper

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.XScaffold
import com.yunzia.hyperstar.ui.component.modifier.blur
import com.yunzia.hyperstar.ui.component.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.component.modifier.showBlur
import com.yunzia.hyperstar.ui.component.pager.NavPager
import com.yunzia.hyperstar.ui.component.topbar.NavTopAppBar
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import dev.chrisbanes.haze.rememberHazeState
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState

@Composable
fun NotDeveloperScopeScreen(){

    val navController = LocalNavigator.current

    val hazeState = rememberHazeState()
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())

    XScaffold(
        modifier = Modifier.fillMaxSize(),
        popupHost = { },
        topBar = {
            NavTopAppBar(
                modifier = Modifier.showBlur(hazeState),
                color = Color.Transparent,
                title = stringResource(R.string.fun_scope),
                scrollBehavior = topAppBarScrollBehavior,
                navController = navController,
                actions = {}
            )

        }
    ) { padding ->
        Box(Modifier.blur(hazeState)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .nestedOverScrollVertical(topAppBarScrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                ),
            ) {

            }

        }
    }



}