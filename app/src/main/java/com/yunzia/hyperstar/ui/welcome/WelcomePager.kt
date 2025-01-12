package com.yunzia.hyperstar.ui.welcome

import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import com.yunzia.hyperstar.R
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.HorizontalPager
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WelcomePager(
    show:MutableState<Boolean>
) {
    val view = LocalView.current
    val pagerState = rememberPagerState(initialPage = 0 ,pageCount = { 7 })

    val coroutineScope = rememberCoroutineScope()
    Log.d("ggc", "WelcomePager: ${pagerState.currentPage}")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.statusBarsPadding())
        Row(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                pagerState.targetPage != 0,
                enter = fadeIn()  + slideIn(
                    animationSpec = tween(
                        durationMillis = 150,
                        delayMillis = 0,
                        easing = LinearEasing
                    )
                ) {
                    IntOffset(it.width, 0)
                  },
                exit = fadeOut() + slideOut(
                    animationSpec = tween(
                    durationMillis = 150,
                    delayMillis = 0,
                    easing = LinearEasing
                    )
                ) {
                    IntOffset(it.width, 0)
                  },

            ) {
                IconButton(
                    modifier = Modifier,
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        coroutineScope.launch {
                            val last = pagerState.settledPage - 1
                            Log.d("ggc", "WelcomePager: ${last}")
                            pagerState.animateScrollToPage( last)
                        }

                    }
                ) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.bar_back__exit),
                        contentDescription = "back",
                        tint = colorScheme.onBackground)
                }

            }
        }
        HorizontalPager(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
            pagerState = pagerState,
            userScrollEnabled = false,
            pageContent = { page ->

                when (page) {

                    0 ->{
                        WelcomeEnterPager(pagerState)
                    }

                    1 -> {

                        RootPage(pagerState)

                    }
                    2->{

                        LanguagePage(pagerState)

                    }
                    3->{
                        ProviderPage(pagerState)

                    }
                    4->{
                        BaseSettingPage(pagerState)

                    }
                    5->{
                        HookChannelPager(pagerState)
                    }
                    6->{
                        EnterPager(show,pagerState)
                    }
                    7->{

                    }

                    else -> {

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Yellow)
                        ) {

                        }
                    }
                }
            }
        )

    }


}

