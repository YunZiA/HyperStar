package com.yunzia.hyperstar

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.provider.Settings
import android.util.Log
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yunzia.hyperstar.ui.base.XScaffold
import com.yunzia.hyperstar.ui.base.nav.PagersModel
import com.yunzia.hyperstar.ui.base.nav.pagersJson
import com.yunzia.hyperstar.ui.base.showFPSMonitor
import com.yunzia.hyperstar.ui.module.home.HomePage
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.list.QSListColorPager
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.list.QsListViewPager
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.media.MediaSettingsPager
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.media.app.MediaAppSettingsPager
import com.yunzia.hyperstar.ui.module.systemui.other.SystemUIOtherPager
import com.yunzia.hyperstar.ui.module.systemui.volume.VolumePager
import com.yunzia.hyperstar.ui.pagers.TranslatorPager
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.ControlCenterColorPager
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.ControlCenterListPager
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.ControlCenterPager
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.card.QSCardColorPager
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.card.QSCardListPager
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.devicecenter.DeviceCenterColorPager
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.slider.ToggleSliderColorsPager
import com.yunzia.hyperstar.ui.module.systemui.other.powermenu.PowerMenuStylePager
import com.yunzia.hyperstar.ui.module.systemui.other.powermenu.SelectFunPager
import com.yunzia.hyperstar.ui.pagers.DonationPage
import com.yunzia.hyperstar.ui.pagers.FPSMonitor
import com.yunzia.hyperstar.ui.pagers.GoRootPager
import com.yunzia.hyperstar.ui.pagers.LanguagePager
import com.yunzia.hyperstar.ui.pagers.ReferencesPager
import com.yunzia.hyperstar.ui.pagers.MainPager
import com.yunzia.hyperstar.ui.pagers.MainPagerByThree
import com.yunzia.hyperstar.ui.pagers.NeedMessagePager
import com.yunzia.hyperstar.ui.pagers.SettingsShowPage
import com.yunzia.hyperstar.ui.pagers.dialog.FirstDialog
import com.yunzia.hyperstar.utils.isFold
import com.yunzia.hyperstar.utils.isPad
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.getWindowSize

@SuppressLint("RestrictedApi", "StateFlowValueCalledInComposition")
@Composable
fun App(){

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val context = LocalContext.current
    val parentRoute = remember { mutableStateOf(PagerList.MAIN) }
    val pagerState = rememberPagerState(initialPage = 0 ,pageCount = { 3 })

    val navController = rememberNavController()

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            parentRoute.value = destination.route!!.substringBeforeLast("/")
        }
    }

    XScaffold {
        BoxWithConstraints {
            FirstDialog(navController)
            Log.d("ggc", "App:  $maxWidth")

            val layoutType = remember { mutableIntStateOf(1) }

            layoutType.intValue = if (isFold()){
                if (maxWidth > 480.dp && Settings.Global.getInt(context.contentResolver, "device_posture", 0) != 1){
                    2
                }else{
                    1
                }
            }else if (isPad()){
                if (isLandscape){
                    3
                }else{
                    1
                }
            }else{
                if (isLandscape && maxWidth > 480.dp){
                    2
                }else{
                    1
                }
            }

            when(layoutType.intValue){
                1->{
                    OneLayout(
                        pagerState,
                        navController,
                        parentRoute
                    )

                }
                2->{
                    TwoLayout(
                        pagerState,
                        navController,
                        parentRoute
                    )

                }
                3->{
                    ExpandLayout(
                        pagerState,
                        navController,
                        parentRoute
                    )
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

    }

}

fun NavGraphBuilder.pagerContent(
    navController: NavHostController,
    parentRoute: MutableState<String>

){
    composable(SystemUIList.CONTROL_CENTER) { ControlCenterPager(navController,parentRoute) }

    composable(ControlCenterList.COLOR_EDIT) { ControlCenterColorPager(navController,parentRoute) }

    composable(ControlCenterList.LAYOUT_ARRANGEMENT) { ControlCenterListPager(navController,parentRoute) }

    composable(ControlCenterList.MEDIA) { MediaSettingsPager(navController,parentRoute) }

    composable(ControlCenterList.CARD_LIST) { QSCardListPager(navController,parentRoute) }

    composable(ControlCenterList.TILE_LAYOUT) { QsListViewPager(navController,parentRoute) }

    composable(ControlCenterList.MEDIA_APP) { MediaAppSettingsPager(navController,parentRoute) }

    composable(CenterColorList.CARD_TILE) { QSCardColorPager(navController,parentRoute) }

    composable(CenterColorList.TOGGLE_SLIDER) { ToggleSliderColorsPager(navController,parentRoute) }

    composable(CenterColorList.DEVICE_CENTER) { DeviceCenterColorPager(navController,parentRoute) }

    composable(CenterColorList.LIST_COLOR) { QSListColorPager(navController,parentRoute) }

    composable(PagerList.GO_ROOT){ GoRootPager(navController,parentRoute) }

    composable(PagerList.LANGUAGE){ LanguagePager(navController,parentRoute) }

    composable(PagerList.TRANSLATOR) { TranslatorPager(navController,parentRoute) }

    composable(PagerList.DONATION) { DonationPage(navController,parentRoute)  }

    composable(PagerList.SHOW){ SettingsShowPage(navController,parentRoute) }

    composable(PagerList.MESSAGE) { NeedMessagePager(navController,parentRoute)  }

    composable(PagerList.REFERENCES) { ReferencesPager(navController,parentRoute)  }

    composable(PagerList.HOME) { HomePage(navController,parentRoute) }

    composable(SystemUIList.VOLUME_DIALOG) { VolumePager(navController,parentRoute) }

    composable(SystemUIList.MORE) { SystemUIOtherPager(navController,parentRoute) }

    composable(SystemUIList.POWERMENU){ PowerMenuStylePager(navController,parentRoute) }


    composable(
        FunList.SELECT_LIST+"?{pagersJson}",
        listOf(
            navArgument("pagersJson") {
                type = pagersJson<PagersModel>()
            }
        )
    ){ SelectFunPager(navController,it,parentRoute) }

}

@Composable
fun OneLayout(
    initialPage: PagerState,
    navController: NavHostController,
    parentRoute: MutableState<String>
){
    val windowWidth = getWindowSize().width


    //val easing = CubicBezierEasing(0.12f, 0.38f, 0.2f, 1f)
    val easing = FastOutSlowInEasing
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = PagerList.MAIN,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { windowWidth },
                animationSpec = tween(durationMillis = 500, easing = easing)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -windowWidth / 5 },
                animationSpec = tween(durationMillis = 500, easing = easing)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -windowWidth / 5 },
                animationSpec = tween(durationMillis = 500, easing = easing)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { windowWidth },
                animationSpec = tween(durationMillis = 500, easing = easing)
            )
        },
        builder = {

            composable(PagerList.MAIN) { MainPager(navController,initialPage) }
            pagerContent(
                navController,
                parentRoute
            )
        }
    )

}

@Composable
fun TwoLayout(
    pagerState: PagerState,
    navController: NavHostController,
    parentRoute: MutableState<String>
) {
    val windowWidth = getWindowSize().width
    val easing = CubicBezierEasing(0.12f, 0.88f, 0.2f, 1f)
    val dividerLineColor = colorScheme.dividerLine

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        Box(
            modifier = Modifier.weight(0.88f)
        ) {
            MainPager(navController, pagerState)
        }
        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 12.dp)
                .width(0.75.dp),
            color = dividerLineColor
        )
        NavHost(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(0.dp)),
            navController = navController,
            startDestination = PagerList.MAIN,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { windowWidth },
                    animationSpec = tween(durationMillis = 500, easing = easing)
                ) + fadeIn(
                    animationSpec = tween(durationMillis = 200)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { windowWidth / 5 },
                    animationSpec = tween(durationMillis = 500, easing = easing)
                ) + fadeOut(
                    animationSpec = tween(durationMillis = 500)
                )
            },
            builder = {
                composable(PagerList.MAIN) { EmptyPage() }
                pagerContent(
                    navController,
                    parentRoute
                )
            }
        )
    }
}


@Composable
fun ExpandLayout(
    pagerState: PagerState,
    navController: NavHostController,
    parentRoute: MutableState<String>
) {
    val windowWidth = getWindowSize().width
    val easing = CubicBezierEasing(0.12f, 0.88f, 0.2f, 1f)
    val dividerLineColor = colorScheme.dividerLine

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        Box(
            modifier = Modifier.weight(0.88f)
        ) {
            MainPagerByThree(navController,pagerState)
        }
        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 12.dp)
                .width(0.75.dp),
            color = dividerLineColor
        )
        NavHost(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(0.dp)),
            navController = navController,
            startDestination = PagerList.MAIN,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { windowWidth },
                    animationSpec = tween(durationMillis = 500, easing = easing)
                ) + fadeIn(
                    animationSpec = tween(durationMillis = 200)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { windowWidth / 5 },
                    animationSpec = tween(durationMillis = 500, easing = easing)
                ) + fadeOut(
                    animationSpec = tween(durationMillis = 500)
                )
            },
            builder = {
                composable(PagerList.MAIN) { EmptyPage() }
                pagerContent(
                    navController,
                    parentRoute
                )
            }
        )
    }
}

object PagerList {

    //主页
    const val MAIN = "root"

    const val HOME = "${MAIN}/home"

    const val LANGUAGE = "${MAIN}/language"

    const val GO_ROOT = "${MAIN}/go_root"
    //翻译
    const val TRANSLATOR = "${MAIN}/translator"
    //
    const val REFERENCES = "${MAIN}/references"
    //投喂
    const val DONATION = "${MAIN}/donation"
    //显示设置
    const val SHOW = "${MAIN}/show"

    const val MESSAGE = "${MAIN}/message"
}

object SystemUIList {
    //控制中心
    const val CONTROL_CENTER = "${PagerList.MAIN}/control_center"

    //音量条
    const val VOLUME_DIALOG = "${PagerList.MAIN}/volumeDialog"
    //系统界面更多
    const val MORE = "${PagerList.MAIN}/systemUI_more"

    const val POWERMENU = "${PagerList.MAIN}/powermenu"
}


object FunList {

    //控制中心
    const val SELECT_LIST = "${SystemUIList.POWERMENU}/selectList"
}

object ControlCenterList{
    //控制中心
    const val CONTROL_CENTER = "${SystemUIList.CONTROL_CENTER}/controlCenter"
    //颜色编辑
    const val COLOR_EDIT = "${SystemUIList.CONTROL_CENTER}/colorEdit"
    //颜色编辑
    const val LAYOUT_ARRANGEMENT = "${SystemUIList.CONTROL_CENTER}/layoutArrangement"
    //妙播
    const val MEDIA = "${SystemUIList.CONTROL_CENTER}/media"
    //妙播应用选择
    const val MEDIA_APP = "${MEDIA}/mediaApp"
    //卡片磁贴列表
    const val CARD_LIST = "${SystemUIList.CONTROL_CENTER}/cardList"
    //普通磁贴布局
    const val TILE_LAYOUT = "${SystemUIList.CONTROL_CENTER}/tileLayout"

}


object CenterColorList {

    //卡片磁贴
    const val CARD_TILE = "${ControlCenterList.COLOR_EDIT}/cardTileColor"
    //滑条
    const val TOGGLE_SLIDER = "${ControlCenterList.COLOR_EDIT}/toggleSliderColor"
    //滑条
    const val DEVICE_CENTER = "${ControlCenterList.COLOR_EDIT}/deviceCenterColor"
    //普通磁贴
    const val LIST_COLOR = "${ControlCenterList.COLOR_EDIT}/listColor"
}

@Composable
fun EmptyPage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val context = LocalContext.current
        BackHandler(true) {
            finishAffinity(context as MainActivity)
        }
        Icon(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            tint = MiuixTheme.colorScheme.secondary,
            modifier = Modifier.size(256.dp)
        )
    }
}