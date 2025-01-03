package com.yunzia.hyperstar

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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
import com.yunzia.hyperstar.ui.base.navtype.PagersModel
import com.yunzia.hyperstar.ui.base.navtype.pagersJson
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
import com.yunzia.hyperstar.ui.pagers.NeedMessagePager
import com.yunzia.hyperstar.ui.pagers.SettingsShowPage
import com.yunzia.hyperstar.ui.pagers.dialog.FirstDialog
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
    val currentStartDestination = remember { mutableStateOf("") }

    val navController = rememberNavController()

    XScaffold {
        BoxWithConstraints {
            FirstDialog(navController)

            if (isLandscape || maxWidth > 768.dp){
                LandscapeLayout(
                    navController,
                    currentStartDestination
                )
            }else{
                PortraitLayout(
                    navController,
                    currentStartDestination
                )

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
    currentStartDestination: MutableState<String>

){

    composable(PagerList.MAIN) { MainPager(navController) }
    composable("EmptyPage") { EmptyPage() }

    composable(PagerList.GO_ROOT){ GoRootPager(navController,currentStartDestination) }

    composable(SystemUIPagerList.CONTROL_CENTER) { ControlCenterPager(navController,currentStartDestination) };

    composable(PagerList.LANGUAGE){ LanguagePager(navController,currentStartDestination) }

    composable(PagerList.TRANSLATOR) { TranslatorPager(navController,currentStartDestination) }

    composable(PagerList.DONATION) { DonationPage(navController,currentStartDestination)  }

    composable(PagerList.SHOW){ SettingsShowPage(navController,currentStartDestination) }
    composable(PagerList.MESSAGE) { NeedMessagePager(navController,currentStartDestination)  }

    composable(PagerList.REFERENCES) { ReferencesPager(navController,currentStartDestination)  }

    composable(PagerList.HOME) { HomePage(navController,currentStartDestination) }

    composable(SystemUIPagerList.COLOR_EDIT) { ControlCenterColorPager(navController,currentStartDestination) }

    composable(SystemUIPagerList.LAYOUT_ARRANGEMENT) { ControlCenterListPager(navController,currentStartDestination) }

    composable(SystemUIPagerList.MEDIA) { MediaSettingsPager(navController,currentStartDestination) }

    composable(SystemUIPagerList.CARD_LIST) { QSCardListPager(navController,currentStartDestination) }

    composable(SystemUIPagerList.TILE_LAYOUT) { QsListViewPager(navController,currentStartDestination) }

    composable(CenterColorList.CARD_TILE) { QSCardColorPager(navController,currentStartDestination) }

    composable(CenterColorList.TOGGLE_SLIDER) { ToggleSliderColorsPager(navController,currentStartDestination) }

    composable(CenterColorList.DEVICE_CENTER) { DeviceCenterColorPager(navController,currentStartDestination) }

    composable(CenterColorList.LIST_COLOR) { QSListColorPager(navController,currentStartDestination) }

    composable(SystemUIPagerList.VOLUME_DIALOG) { VolumePager(navController,currentStartDestination) }

    composable(SystemUIPagerList.MORE) { SystemUIOtherPager(navController,currentStartDestination) }

    composable(SystemUIPagerList.MEDIA_APP) { MediaAppSettingsPager(navController,currentStartDestination) }

    composable(SystemUIPagerList.POWERMENU){ PowerMenuStylePager(navController,currentStartDestination) }


    composable(
        FunList.SELECT_LIST+"/{pagersJson}",
        listOf(
            navArgument("pagersJson") {
                type = pagersJson<PagersModel>()
            }
        )
    ){ SelectFunPager(navController,it,currentStartDestination) }

}

@Composable
fun PortraitLayout(
    navController: NavHostController,
    currentStartDestination: MutableState<String>
){
    val windowWidth = getWindowSize().width

    LaunchedEffect(navController) {
        currentStartDestination.value = PagerList.MAIN
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.route == "EmptyPage") {
                navController.navigate(PagerList.MAIN) {
                    popUpTo(PagerList.MAIN) { inclusive = true }
                }
            }
        }
    }

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
            pagerContent(
                navController,
                currentStartDestination
            )
        }
    )

}

@Composable
fun LandscapeLayout(
    navController: NavHostController,
    currentStartDestination: MutableState<String>
) {
    val windowWidth = getWindowSize().width
    val easing = CubicBezierEasing(0.12f, 0.88f, 0.2f, 1f)
    val dividerLineColor = colorScheme.dividerLine
    LaunchedEffect(navController) {
        currentStartDestination.value = "EmptyPage"
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.route == PagerList.MAIN) {
                navController.navigate("EmptyPage") {
                    popUpTo("EmptyPage") { inclusive = true }
                }
            }
        }
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        Box(
            modifier = Modifier.weight(0.88f)
        ) {
            MainPager(navController)
        }
        Canvas(
            Modifier
                .fillMaxHeight()
                .padding(horizontal = 12.dp)
                .width(0.75.dp)
        ) {
            drawLine(
                color = dividerLineColor,
                strokeWidth = 0.75.dp.toPx(),
                start = Offset(0.75.dp.toPx() / 2, 0f),
                end = Offset(0.75.dp.toPx() / 2, size.height),
            )
        }
        NavHost(
            modifier = Modifier.weight(1f).clip(RoundedCornerShape(0.dp)),
            navController = navController,
            startDestination = "EmptyPage",
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
                pagerContent(
                    navController,
                    currentStartDestination
                )
            }
        )
    }
}



object PagerList {

    //主页
    const val MAIN = "main"
    //系统界面更多
    const val HOME = "home"

    const val LANGUAGE = "language"

    const val GO_ROOT = "go_root"
    //翻译
    const val TRANSLATOR = "translator"
    //
    const val REFERENCES = "references"
    //投喂
    const val DONATION = "donation"
    //显示设置
    const val SHOW = "show"

    const val MESSAGE = "message"
}

object SystemUIPagerList {

    //控制中心
    const val CONTROL_CENTER = "controlCenter"
    //颜色编辑
    const val COLOR_EDIT = "colorEdit"
    //颜色编辑
    const val LAYOUT_ARRANGEMENT = "layoutArrangement"
    //妙播
    const val MEDIA = "media"
    //妙播应用选择
    const val MEDIA_APP = "mediaApp"
    //卡片磁贴列表
    const val CARD_LIST = "cardList"
    //普通磁贴布局
    const val TILE_LAYOUT = "tileLayout"
    //音量条
    const val VOLUME_DIALOG = "volumeDialog"
    //系统界面更多
    const val MORE = "more"

    const val POWERMENU = "powermenu"
}


object FunList {

    //控制中心
    const val SELECT_LIST = "selectList"
}


object CenterColorList {

    //卡片磁贴
    const val CARD_TILE = "cardTileColor"
    //滑条
    const val TOGGLE_SLIDER = "toggleSliderColor"
    //滑条
    const val DEVICE_CENTER = "deviceCenterColor"
    //普通磁贴
    const val LIST_COLOR = "listColor"
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