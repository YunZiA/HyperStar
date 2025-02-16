package com.yunzia.hyperstar.ui.module.systemui.other.powermenu

//import com.chaos.hyperstar.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.FunList
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.Classes
import com.yunzia.hyperstar.ui.base.ModuleNavPagers
import com.yunzia.hyperstar.ui.base.SuperArgNavHostArrow
import com.yunzia.hyperstar.ui.base.TopButton
import com.yunzia.hyperstar.ui.base.helper.getSystemCornerRadius
import com.yunzia.hyperstar.utils.Helper
import com.yunzia.hyperstar.utils.SPUtils
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
fun PowerMenuStylePager(
    navController: NavHostController,
    currentStartDestination: MutableState<String>
) {

    val style = remember { mutableIntStateOf( SPUtils.getInt("is_power_menu_style",0) ) }
    val pagerState = rememberPagerState(initialPage = style.intValue,pageCount = { 3 })
    val funTypes = stringArrayResource(R.array.power_fun_types).toList()
    val funTitles = stringArrayResource(R.array.power_fun_titles).toList()

    ModuleNavPagers(
        activityTitle = stringResource(R.string.power_menu_extra),
        navController = navController,
        parentRoute = currentStartDestination,
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
                    SPUtils.setInt("is_power_menu_style",style.intValue)
                }

            }

        },
        endClick = {
            Helper.rootShell("killall com.android.systemui")
        },
    ) {

        item {

            val configuration = LocalConfiguration.current
            val screenHeight by remember { mutableFloatStateOf(configuration.screenHeightDp*0.52f) }
            val titleSize = remember { mutableStateOf(16.sp) }

            Spacer(Modifier.height(20.dp))

            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight.dp),
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 100.dp),
                pageSpacing = 0.dp,
                userScrollEnabled = true
            ) { page ->
                when (page) {

                    0 -> {
                        AnimPager(
                            0,
                            pagerState.currentPage,
                        ) {
                            PowerMenuStyleDefault(titleSize)
                        }
                    }

                    1 -> {
                        AnimPager(
                            1,
                            pagerState.currentPage
                        ) {
                            PowerMenuStyleA(titleSize)
                        }
                    }

                    2 -> {
                        AnimPager(
                            2,
                            pagerState.currentPage
                        ){
                            PowerMenuStyleB(titleSize)
                        }
                    }
                }

            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 26.dp,
                        bottom = 36.dp
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pagerState.pageCount) { index ->
                    val mgScale by animateDpAsState(
                        targetValue = if (index == pagerState.currentPage) 14.dp else 7.dp,
                        animationSpec = tween(300),
                        label = ""
                    )

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .width(mgScale)
                            .height(7.dp)
                            .clip(if (index == pagerState.currentPage) RoundedCornerShape(2.dp) else CircleShape)
                            .background(
                                color = if (index == pagerState.currentPage) Color(
                                    0xff3988FF
                                ) else Color(
                                    0xffE4E5E7
                                ),
                                shape = if (index == pagerState.currentPage) RoundedCornerShape(
                                    6.dp
                                ) else CircleShape
                            )
                    )
                }
            }

        }

        item {
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
                    Classes{
                        SuperArgNavHostArrow(
                            title = stringResource(R.string.button)+0,
                            navController = navController,
                            route = FunList.SELECT_LIST,
                            key = "power_menu_style_b_0",
                            rightDo = { getFunTitle(funTypes,funTitles,it) }
                        )
                        SuperArgNavHostArrow(
                            title = stringResource(R.string.button)+1,
                            navController = navController,
                            route = FunList.SELECT_LIST,
                            key = "power_menu_style_b_1",
                            rightDo = { getFunTitle(funTypes,funTitles,it) }
                        )
                        SuperArgNavHostArrow(
                            title = stringResource(R.string.button)+2,
                            navController = navController,
                            route = FunList.SELECT_LIST,
                            key = "power_menu_style_b_2",
                            rightDo = { getFunTitle(funTypes,funTitles,it) }
                        )
                        SuperArgNavHostArrow(
                            title = stringResource(R.string.button)+3,
                            navController = navController,
                            route = FunList.SELECT_LIST,
                            key = "power_menu_style_b_3",
                            rightDo = { getFunTitle(funTypes,funTitles,it) }
                        )
                        SuperArgNavHostArrow(
                            title = stringResource(R.string.button)+4,
                            navController = navController,
                            route = FunList.SELECT_LIST,
                            key = "power_menu_style_b_4",
                            rightDo = { getFunTitle(funTypes,funTitles,it) }
                        )
                        SuperArgNavHostArrow(
                            title = stringResource(R.string.button)+5,
                            navController = navController,
                            route = FunList.SELECT_LIST,
                            key = "power_menu_style_b_5",
                            rightDo = { getFunTitle(funTypes,funTitles,it) }
                        )
                        SuperArgNavHostArrow(
                            title = stringResource(R.string.button)+6,
                            navController = navController,
                            route = FunList.SELECT_LIST,
                            key = "power_menu_style_b_6",
                            rightDo = { getFunTitle(funTypes,funTitles,it) }
                        )
                        SuperArgNavHostArrow(
                            title = stringResource(R.string.button)+7,
                            navController = navController,
                            route = FunList.SELECT_LIST,
                            key = "power_menu_style_b_7",
                            rightDo = { getFunTitle(funTypes,funTitles,it) }
                        )


                    }

                }
            }


        }

    }


}


@Composable
private fun getFunTitle(
    types: List<String>,
    titles: List<String>,
    type: String
):String{
    return titles[types.indexOf(type)]
}

@Composable
fun AnimPager(
    page:Int,
    currentPage:Int,
    content: @Composable() BoxWithConstraintsScope.() -> Unit
){

    val roundedCorner by rememberUpdatedState(getSystemCornerRadius())
    val configuration = LocalConfiguration.current
    val screenWidth by remember { mutableFloatStateOf(configuration.screenWidthDp*0.52f)}

    val imgScale by animateFloatAsState(
        targetValue = if (currentPage == page) 1f else 0.8f,
        animationSpec =  TweenSpec(400,0,LinearOutSlowInEasing),
        label = ""
    )
    val select by animateDpAsState(
        targetValue = if (currentPage == page) 3.dp else 0.dp,
        animationSpec = TweenSpec(400,0,LinearOutSlowInEasing),
        label = ""
    )

    val selectAlpha by animateFloatAsState(
        targetValue = if (currentPage == page) 1f else 0f,
        animationSpec = TweenSpec(400,0, LinearOutSlowInEasing),
        label = ""
    )
    Box(
        modifier = Modifier
            .scale(imgScale)
            .fillMaxHeight()
            .border(
                select,
                Color(0xff3988FF ).copy(alpha = selectAlpha),
                SmoothRoundedCornerShape(roundedCorner, 0.8F)
            )
            .width(screenWidth.dp),
        contentAlignment = Alignment.Center,
    ) {
        BoxWithConstraints(
            Modifier
                .padding(6.dp)
                .clip(SmoothRoundedCornerShape(roundedCorner/5*4, 0.8F))
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

