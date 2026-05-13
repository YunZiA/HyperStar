package com.yunzia.hyperstar.ui.screen.module.systemui.other.powermenu

//import com.chaos.hyperstar.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.SuperGroup
import com.yunzia.hyperstar.ui.component.SuperGroupPosition
import com.yunzia.hyperstar.ui.component.topbar.TopButton
import com.yunzia.hyperstar.ui.component.helper.getSystemCornerRadius
import com.yunzia.hyperstar.ui.component.preference.PreferenceScreen
import com.yunzia.hyperstar.utils.Helper
import com.yunzia.hyperstar.prefs.SPUtils
import com.yunzia.hyperstar.utils.rememberWindowSize
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.shapes.SmoothRoundedCornerShape
import com.yunzia.hyperstar.ui.component.preference.impl.NavPreferenceImpl
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.ui.navigation.PowerMenuRoutes
import com.yunzia.hyperstar.ui.navigation.SystemUIRoutes
import SearchRoute
import androidx.activity.compose.LocalActivity
import com.yunzia.hyperstar.MainActivity
import kotlin.math.absoluteValue

@SearchRoute(route = SystemUIRoutes.PowerMenu::class)
@Composable
fun PowerMenuStyleScreen() {

    val navController = LocalNavigator.current
    val activity = LocalActivity.current as MainActivity
    val style = remember { mutableIntStateOf( SPUtils.getInt("is_power_menu_style",0) ) }
    val pagerState = rememberPagerState(initialPage = style.intValue,pageCount = { 3 })
    val funTypes = stringArrayResource(R.array.power_fun_types).toList()
    val funTitles = stringArrayResource(R.array.power_fun_titles).toList()

    val windowSize = rememberWindowSize()
    val screenHeight = animateDpAsState(
        windowSize.value.height * 0.52f,
        animationSpec = TweenSpec(100,0,FastOutSlowInEasing)
    )
    val screenWidth = animateDpAsState(
        windowSize.value.width * 0.52f,
        animationSpec = TweenSpec(100,0,FastOutSlowInEasing)
    )
    val titleSize = remember { mutableStateOf(16.sp) }

    PreferenceScreen(
        title = stringResource(R.string.power_menu_extra),
        navController = navController,
        endIcon = {

            AnimatedVisibility(
                visible = (style.intValue != pagerState.currentPage),
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {

                TopButton(
                    imageVector = ImageVector.vectorResource(R.drawable.save2),
                    contentDescription = "save",
                    tint = colorScheme.primary
                ){
                    style.intValue = pagerState.currentPage
                    SPUtils.putInt("is_power_menu_style",style.intValue)
                }

            }

        },
        endClick = {
            Helper.rootShell("killall com.android.systemui")
        },
        scrollToKey = activity.appViewModel.scrollToKey.value,
        onScrollComplete = { activity.appViewModel.scrollToKey.value = null },
    ) { _, _ ->

        list.item {
            Spacer(Modifier.height(20.dp))

            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(350.dp)
                    .height(screenHeight.value),
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 100.dp),
                flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                    // 自定义减速度
                    decayAnimationSpec = rememberSplineBasedDecay(),
                    snapAnimationSpec = spring(
                        dampingRatio = 1f,
                        stiffness = 570f
                    )
                ),
                pageSpacing = (-10).dp,
                userScrollEnabled = true
            ) { page ->
                when (page) {

                    0 -> {
                        AnimPager(
                            0,
                            pagerState,
                            screenWidth
                        ) {
                            PowerMenuStyleDefault(titleSize)
                        }
                    }

                    1 -> {
                        AnimPager(
                            1,
                            pagerState,
                            screenWidth
                        ) {
                            PowerMenuStyleA(titleSize)
                        }
                    }

                    2 -> {
                        AnimPager(
                            2,
                            pagerState,
                            screenWidth
                        ){
                            PowerMenuStyleB(titleSize)
                        }
                    }
                }

            }
        }

        list.item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 28.dp,
                        bottom = 38.dp
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pagerState.pageCount) { index ->

                    val progress = remember { mutableStateOf(0f) }


                    LaunchedEffect(pagerState.currentPageOffsetFraction, pagerState.currentPage) {
                        // 首先获取线性进度
                        progress.value = when {
                            // 当前页面
                            index == pagerState.currentPage -> {
                                1f - pagerState.currentPageOffsetFraction.absoluteValue
                            }
                            // 下一页或上一页（正在滑入的页面）
                            (index == pagerState.currentPage - 1 && pagerState.currentPageOffsetFraction < 0) ||
                                    (index == pagerState.currentPage + 1 && pagerState.currentPageOffsetFraction > 0) -> {
                                pagerState.currentPageOffsetFraction.absoluteValue
                            }
                            // 其他页面
                            else -> 0f
                        }
                    }
//                    +8.dp*progress.value
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .width(8.dp+8.dp*progress.value)
                            .height(8.dp)
                            .clip(RoundedCornerShape(8.dp-2.dp*progress.value))
                            .background(
                                color =
                                    Color(
                                        ColorUtils.blendARGB(
                                            0xffE4E5E7.toInt(),  // 起始颜色
                                            0xff3988FF.toInt(),  // 目标颜色
                                            progress.value
                                        )
                                    )
                            )
                    )
                }
            }

        }

        list.item {
            when(pagerState.currentPage){
                1->{
//                    Classes{
//                        XSuperDropdown(
//                            key = "is_power_menu_style",
//                            option = R.array.power_menu_style,
//                            title = "A",
//                            selectedIndex = style
//                        )
//                        XSuperDropdown(
//                            key = "is_power_menu_style",
//                            option = R.array.power_menu_style,
//                            title = "B",
//                            selectedIndex = style
//                        )
//
//                    }

                }
                2->{

                    SuperGroup(
                        position = SuperGroupPosition.LAST
                    ){
                        NavPreferenceImpl(
                            title = stringResource(R.string.button)+0,
                            endText = getFunTitle("power_menu_style_b_0", funTypes, funTitles),
                            onClick = { navController.navigate(PowerMenuRoutes.FunSelect(0, "power_menu_style_b_0")) }
                        )
                        NavPreferenceImpl(
                            title = stringResource(R.string.button)+ 1,
                            endText = getFunTitle("power_menu_style_b_1", funTypes, funTitles),
                            onClick = { navController.navigate(PowerMenuRoutes.FunSelect(1, "power_menu_style_b_1")) }
                        )
                        NavPreferenceImpl(
                            title = stringResource(R.string.button) + 2,
                            endText = getFunTitle("power_menu_style_b_2", funTypes, funTitles),
                            onClick = { navController.navigate(PowerMenuRoutes.FunSelect(2, "power_menu_style_b_2")) }
                        )
                        NavPreferenceImpl(
                            title = stringResource(R.string.button) + 3,
                            endText = getFunTitle("power_menu_style_b_3", funTypes, funTitles),
                            onClick = { navController.navigate(PowerMenuRoutes.FunSelect(3, "power_menu_style_b_3")) }
                        )
                        NavPreferenceImpl(
                            title = stringResource(R.string.button) + 4,
                            endText = getFunTitle("power_menu_style_b_4", funTypes, funTitles),
                            onClick = { navController.navigate(PowerMenuRoutes.FunSelect(4, "power_menu_style_b_4")) }
                        )
                        NavPreferenceImpl(
                            title = stringResource(R.string.button) + 5,
                            endText = getFunTitle("power_menu_style_b_5", funTypes, funTitles),
                            onClick = { navController.navigate(PowerMenuRoutes.FunSelect(5, "power_menu_style_b_5")) }
                        )
                        NavPreferenceImpl(
                            title = stringResource(R.string.button) + 6,
                            endText = getFunTitle("power_menu_style_b_6", funTypes, funTitles),
                            onClick = { navController.navigate(PowerMenuRoutes.FunSelect(6, "power_menu_style_b_6")) }
                        )
                        NavPreferenceImpl(
                            title = stringResource(R.string.button) + 7,
                            endText = getFunTitle("power_menu_style_b_7", funTypes, funTitles),
                            onClick = { navController.navigate(PowerMenuRoutes.FunSelect(7, "power_menu_style_b_7")) }
                        )
                    }

                }
            }


        }

    }


}

@Composable
private fun getFunTitle(
    key: String,
    types: List<String>,
    titles: List<String>
):String{
    return titles[types.indexOf(SPUtils.getString(key,"null"))]
}

@Composable
fun AnimPager(
    page:Int,
    pagerState:PagerState,
    width: State<Dp>,
    content: @Composable() BoxWithConstraintsScope.() -> Unit
){

    val progress = remember { mutableStateOf(0f) }


    LaunchedEffect(pagerState.currentPageOffsetFraction, pagerState.currentPage) {
        // 首先获取线性进度
        progress.value = when {
            // 当前页面
            page == pagerState.currentPage -> {
                1f - pagerState.currentPageOffsetFraction.absoluteValue
            }
            // 下一页或上一页（正在滑入的页面）
            (page == pagerState.currentPage - 1 && pagerState.currentPageOffsetFraction < 0) ||
                    (page == pagerState.currentPage + 1 && pagerState.currentPageOffsetFraction > 0) -> {
                pagerState.currentPageOffsetFraction.absoluteValue
            }
            // 其他页面
            else -> 0f
        }
    }

    val roundedCorner by rememberUpdatedState(getSystemCornerRadius())

    val selectAlpha by animateFloatAsState(
        targetValue = if (pagerState.currentPage == page) 1f else 0f,
        animationSpec = TweenSpec(250,0, LinearEasing),
        label = ""
    )
    Box(
        modifier = Modifier
            .scale(0.7f + progress.value * 0.3f)
            .fillMaxHeight()
            .border(
                3.dp,
                Color(0xff3988FF).copy(alpha = selectAlpha),
                SmoothRoundedCornerShape(roundedCorner)
            )
            .width(width.value),
        contentAlignment = Alignment.Center,
    ) {
        BoxWithConstraints(
            Modifier
                .padding(6.dp)
                .clip(SmoothRoundedCornerShape(roundedCorner / 5 * 4))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF5470CB), Color(0xFF62B1D0)),
                        endY = 700f,
                        tileMode = TileMode.Clamp
                    )
                )
        ){
            content()

        }

    }
}

