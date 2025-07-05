package com.yunzia.hyperstar

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.yunzia.hyperstar.ui.component.BaseActivity
import com.yunzia.hyperstar.ui.component.XScaffold
import com.yunzia.hyperstar.ui.component.nav.PagersModel
import com.yunzia.hyperstar.ui.component.nav.pagersJson
import com.yunzia.hyperstar.ui.screen.module.NotDeveloperScreen
import com.yunzia.hyperstar.ui.screen.module.barrage.BarrageScreen
import com.yunzia.hyperstar.ui.screen.module.home.HomeScreen
import com.yunzia.hyperstar.ui.screen.module.screenshot.screenshotScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.ControlCenterColorScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.ControlCenterListScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.ControlCenterScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.card.QSCardColorScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.card.QSCardListScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.devicecenter.DeviceCenterColorScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.list.QSListColorScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.list.QsListViewScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.media.MediaSettingsScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.media.app.MediaAppSettingsPager
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.slider.ToggleSliderColorsScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.other.SystemUIOtherScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.other.notification.NotificationAppDetail
import com.yunzia.hyperstar.ui.screen.module.systemui.other.notification.NotificationOfImScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.other.powermenu.PowerMenuStyleScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.other.powermenu.SelectFunScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.volume.VolumeScreen
import com.yunzia.hyperstar.ui.screen.module.thememanager.ThemeManagerScreen
import com.yunzia.hyperstar.ui.screen.pagers.CurrentVersionLogScreen
import com.yunzia.hyperstar.ui.screen.pagers.DonationPage
import com.yunzia.hyperstar.ui.screen.pagers.FPSMonitor
import com.yunzia.hyperstar.ui.screen.pagers.GoRootPager
import com.yunzia.hyperstar.ui.screen.pagers.LanguagePager
import com.yunzia.hyperstar.ui.screen.pagers.LogHistoryScreen
import com.yunzia.hyperstar.ui.screen.pagers.MainPager
import com.yunzia.hyperstar.ui.screen.pagers.MainPagerByThree
import com.yunzia.hyperstar.ui.screen.pagers.NeedMessageScreen
import com.yunzia.hyperstar.ui.screen.pagers.ReferencesScreen
import com.yunzia.hyperstar.ui.screen.pagers.SettingsShowScreen
import com.yunzia.hyperstar.ui.screen.pagers.TranslatorScreen
import com.yunzia.hyperstar.ui.screen.pagers.UpdaterScreen
import com.yunzia.hyperstar.ui.screen.welcome.ActivePage
import com.yunzia.hyperstar.ui.screen.welcome.WelcomeScreen
import com.yunzia.hyperstar.utils.Helper.isModuleActive
import com.yunzia.hyperstar.utils.PreferencesUtil
import com.yunzia.hyperstar.utils.isFold
import com.yunzia.hyperstar.utils.isPad
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.VerticalDivider
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.getWindowSize

@SuppressLint("SourceLockedOrientationActivity", "UnusedBoxWithConstraintsScope",
    "LocalContextConfigurationRead"
)
@Composable
fun App(){

    val context = LocalContext.current
    val activity = LocalActivity.current as BaseActivity
    XScaffold {
        val isUpdate = remember { mutableStateOf(false) }

        if (!isModuleActive()){
            if (!isFold() && !isPad()){
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            ActivePage()
            return@XScaffold
        }
        val welcomeState = rememberPagerState(initialPage = 0 ,pageCount = { 7 })
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val parentRoute = remember { mutableStateOf(PagerList.MAIN) }
        val pagerState = rememberPagerState(initialPage = 0 ,pageCount = { 3 })

        val welcome = remember { mutableStateOf(PreferencesUtil.getBoolean("is_first_use",true)) }
        //val welcome = remember { mutableStateOf(true )}
        val easing  = CubicBezierEasing(.42f,0f,0.26f,.85f)
        val navController = rememberNavController()
        val layoutType = remember { mutableIntStateOf(1) }
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(navController) {
            navController.addOnDestinationChangedListener { _, destination, _ ->
                parentRoute.value = destination.route!!.substringBeforeLast("/")
            }
        }

        LaunchedEffect(welcome.value) {
            if (!welcome.value){
                delay(300)
                coroutineScope.launch {
                    welcomeState.scrollToPage(0)
                }
            }

        }

        LaunchedEffect(
            activity.updateUI
        ) {
            if ( activity.updateUI == 0) return@LaunchedEffect
            isUpdate.value = true
            delay(20)
            isUpdate.value = false
        }
        if (isUpdate.value) return@XScaffold

        AnimatedVisibility(
            !welcome.value,
            enter = fadeIn(animationSpec = tween(300, easing = easing)) + scaleIn(animationSpec = tween(300, easing = easing),initialScale = 0.9f),
            exit = fadeOut()+scaleOut()
        ) {
            BoxWithConstraints {
                //FirstDialog(navController)
                Log.d("ggc", "App:  $maxWidth")

                layoutType.intValue = if (isFold()) {
                    if (maxWidth > 480.dp && Settings.Global.getInt(
                            context.contentResolver,
                            "device_posture",
                            0
                        ) != 1
                    ) {
                        2
                    } else {
                        1
                    }
                } else if (isPad()) {
                    if (isLandscape) {
                        3
                    } else {
                        1
                    }
                } else {
                    if (isLandscape && maxWidth > 480.dp) {
                        2
                    } else {
                        1
                    }
                }

                when (layoutType.intValue) {
                    1 -> {
                        OneLayout(
                            pagerState,
                            navController,
                            parentRoute
                        )

                    }

                    2 -> {
                        TwoLayout(
                            pagerState,
                            navController,
                            parentRoute
                        )

                    }

                    3 -> {
                        ExpandLayout(
                            pagerState,
                            navController,
                            parentRoute
                        )
                    }
                }

                AnimatedVisibility(
                    activity.showFPSMonitor.value
                ) {
                    FPSMonitor(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(horizontal = 28.dp)
                    )

                }
            }

        }
        AnimatedVisibility(
            welcome.value,
            exit = fadeOut(animationSpec = tween(300, easing = easing))+ scaleOut(animationSpec = tween(300, easing = easing),targetScale = 0.9f)
        ) {

            if (!isFold() && !isPad()){

                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }

            WelcomeScreen(welcome,welcomeState)
        }


    }

}

fun NavGraphBuilder.pagerContent(
    navController: NavHostController,
    parentRoute: MutableState<String>

){

    composable(SystemUIList.CONTROL_CENTER,) { ControlCenterScreen(navController,parentRoute) }

    composable(ControlCenterList.COLOR_EDIT) { ControlCenterColorScreen(navController,parentRoute) }

    composable(ControlCenterList.LAYOUT_ARRANGEMENT) { ControlCenterListScreen(navController,parentRoute) }

    composable(ControlCenterList.MEDIA) { MediaSettingsScreen(navController,parentRoute) }

    composable(ControlCenterList.CARD_LIST) { QSCardListScreen(navController,parentRoute) }

    composable(ControlCenterList.TILE_LAYOUT) { QsListViewScreen(navController,parentRoute) }

    composable(ControlCenterList.MEDIA_APP) { MediaAppSettingsPager(navController,parentRoute) }

    composable(CenterColorList.CARD_TILE) { QSCardColorScreen(navController,parentRoute) }

    composable(CenterColorList.TOGGLE_SLIDER) { ToggleSliderColorsScreen(navController,parentRoute) }

    composable(CenterColorList.DEVICE_CENTER) { DeviceCenterColorScreen(navController,parentRoute) }

    composable(CenterColorList.LIST_COLOR) { QSListColorScreen(navController,parentRoute) }

    composable(PagerList.GO_ROOT){ GoRootPager(navController,parentRoute) }
    composable(PagerList.NOTDEVELOP){ NotDeveloperScreen(navController,parentRoute) }

    composable(PagerList.LANGUAGE){ LanguagePager(navController,parentRoute) }

    composable(PagerList.TRANSLATOR) { TranslatorScreen(navController,parentRoute) }

    composable(PagerList.DONATION) { DonationPage(navController,parentRoute)  }

    composable(PagerList.SHOW){ SettingsShowScreen(navController,parentRoute) }

    composable(PagerList.MESSAGE) { NeedMessageScreen(navController,parentRoute)  }

    composable(PagerList.REFERENCES) { ReferencesScreen(navController,parentRoute)  }

    composable(PagerList.HOME) { HomeScreen(navController,parentRoute) }

    composable(PagerList.SCREENSHOT) { screenshotScreen(navController,parentRoute) }

    composable(PagerList.BARRAGE) { BarrageScreen(navController,parentRoute) }

    composable(PagerList.THEMEMANAGER) { ThemeManagerScreen(navController,parentRoute) }

    composable(PagerList.UPDATER) { UpdaterScreen(navController,parentRoute) }

    composable(SystemUIList.VOLUME_DIALOG) { VolumeScreen(navController,parentRoute) }

    composable(SystemUIList.MORE) { SystemUIOtherScreen(navController,parentRoute) }

    composable(SystemUIMoreList.POWERMENU){ PowerMenuStyleScreen(navController,parentRoute) }



    composable(SystemUIMoreList.NOTIFICATIONOFIM){
        NotificationOfImScreen(navController,parentRoute)
    }

    composable(SystemUIMoreList.NOTIFICATION_APP_DETAIL) { NotificationAppDetail(navController,parentRoute) }

    composable(PagerList.CURRENTLOG) {
        CurrentVersionLogScreen(navController,parentRoute)
    }

    composable(PagerList.LOGHISTORY) {
        LogHistoryScreen(navController,parentRoute)

    }

    composable(
        FunList.SELECT_LIST+"?{pagersJson}",
        listOf(
            navArgument("pagersJson") {
                type = pagersJson<PagersModel>()
            }
        )
    ){ SelectFunScreen(navController,it,parentRoute) }

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
    val activity = LocalActivity.current as MainActivity

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
                .padding(end = 12.dp)
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
            modifier = Modifier.weight(1f)
        ) {
            MainPagerByThree(navController,pagerState)
        }
        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 12.dp)
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
            tint = colorScheme.secondary,
            modifier = Modifier.size(256.dp)
        )
    }
}