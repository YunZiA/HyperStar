package com.yunzia.hyperstar.ui.screen.module.systemui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.TabRow
import com.yunzia.hyperstar.ui.component.pager.ModuleNavPager
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.ControlCenterPager
import com.yunzia.hyperstar.ui.screen.module.systemui.other.SystemUIOtherPager
import com.yunzia.hyperstar.ui.screen.module.systemui.volume.VolumePager
import com.yunzia.hyperstar.utils.Helper
import generated.SearchIndex
import kotlinx.coroutines.launch

import SearchRoute
import androidx.compose.runtime.remember
import com.yunzia.hyperstar.ui.navigation.MainRoutes

@SearchRoute(route = MainRoutes.SystemUI::class)
@Composable
fun SystemUIScreen(){
    val navController = LocalNavigator.current
    val coroutineScope = rememberCoroutineScope()
    val activity = LocalActivity.current as MainActivity
    val scrollToKey = activity.appViewModel.scrollToKey.value

    val initialPage = remember(scrollToKey) {
        if (scrollToKey != null) {
            SearchIndex.entries.find { it.key == scrollToKey }?.tabIndex ?: 0
        } else 0
    }
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { 3 })

    LaunchedEffect(scrollToKey) {
        if (scrollToKey != null) {
            val tabIndex = SearchIndex.entries.find { it.key == scrollToKey }?.tabIndex ?: 0
            if (tabIndex in 0..2) {
                pagerState.animateScrollToPage(tabIndex)
            }
        }
    }

    val tabs = listOf(
        stringResource(R.string.control_center),
        stringResource(R.string.sound_settings),
        stringResource(R.string.more),
    )
    ModuleNavPager(
        activityTitle = stringResource(R.string.systemui),
        navController = navController,
        endClick = {
            Helper.rootShell("killall com.android.systemui")
        },
    ) { scrollBehavior, paddingValue ->

        Column(
            modifier = Modifier.padding(top = paddingValue.calculateTopPadding() + 12.dp)
        ) {

            TabRow(
                tabs = tabs,
                selectedTabIndex = pagerState.targetPage,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp)
            ) {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(it)
                }
            }
            HorizontalPager(
                state = pagerState,
                key = {
                    tabs[it]
                },
                beyondViewportPageCount = 1,
                userScrollEnabled = false
            ) {
                when(it){
                    0 -> {
                        ControlCenterPager(
                            navController,
                            scrollBehavior,
                            paddingValue,
                            scrollToKey = scrollToKey,
                            onScrollComplete = { activity.appViewModel.scrollToKey.value = null }
                        )
                    }
                    1 -> {
                        VolumePager(
                            navController,
                            scrollBehavior,
                            paddingValue,
                            scrollToKey = scrollToKey,
                            onScrollComplete = { activity.appViewModel.scrollToKey.value = null }
                        )
                    }
                    2 -> {
                        SystemUIOtherPager(
                            navController,
                            scrollBehavior,
                            paddingValue,
                            scrollToKey = scrollToKey,
                            onScrollComplete = { activity.appViewModel.scrollToKey.value = null }
                        )
                    }
                }

            }
        }

    }
}