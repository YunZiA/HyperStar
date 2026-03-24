package com.yunzia.hyperstar.ui.screen.module.systemui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.TabRow
import com.yunzia.hyperstar.ui.component.pager.ModuleNavPager
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.ControlCenterPager
import com.yunzia.hyperstar.ui.screen.module.systemui.other.SystemUIOtherPager
import com.yunzia.hyperstar.ui.screen.module.systemui.volume.VolumePager
import com.yunzia.hyperstar.utils.Helper
import kotlinx.coroutines.launch

@Composable
fun SystemUIScreen(){
    val navController = LocalNavigator.current
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0 ,pageCount = { 3 })
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
                            paddingValue
                        )
                    }
                    1 -> {
                        VolumePager(
                            navController,
                            scrollBehavior,
                            paddingValue
                        )
                    }
                    2 -> {
                        SystemUIOtherPager(
                            navController,
                            scrollBehavior,
                            paddingValue
                        )
                    }
                }

            }
        }

    }
}