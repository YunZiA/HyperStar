package com.yunzia.hyperstar.ui.screen.module.systemui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.TabRow
import com.yunzia.hyperstar.ui.component.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.component.pager.ModuleNavPager
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.ControlCenterPager
import com.yunzia.hyperstar.ui.screen.module.systemui.other.SystemUIOtherPager
import com.yunzia.hyperstar.ui.screen.module.systemui.volume.VolumePager
import com.yunzia.hyperstar.utils.Helper
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun SystemUIScreen(
    navController: NavHostController,
    currentStartDestination: MutableState<String>
){
    ModuleNavPager(
        activityTitle = stringResource(R.string.systemui),
        navController = navController,
        parentRoute = currentStartDestination,
        endClick = {
            Helper.rootShell("killall com.android.systemui")
        },
    ) { scrollBehavior, paddingValue ->

        val coroutineScope = rememberCoroutineScope()

        val tabs = listOf(
            stringResource(R.string.control_center),
            stringResource(R.string.sound_settings),
            stringResource(R.string.more),
        )
        val pagerState = rememberPagerState(initialPage = 0 ,pageCount = { 3 })

        Column(
            modifier = Modifier
                .padding(top = paddingValue.calculateTopPadding())
        ) {

            TabRow(
                tabs = tabs,
                selectedTabIndex = pagerState.targetPage,
                modifier = Modifier.padding(horizontal = 18.dp).padding(top = 14.dp)
            ) {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(it)
                }
            }
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(top = 6.dp),
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