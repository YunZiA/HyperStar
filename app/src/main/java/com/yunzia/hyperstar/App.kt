package com.yunzia.hyperstar

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.yunzia.hyperstar.prefs.PreferencesUtil
import com.yunzia.hyperstar.ui.component.window.shouldShowSplitPane
import com.yunzia.hyperstar.ui.navigation.ColorEditRoutes
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.ui.navigation.MainRoutes
import com.yunzia.hyperstar.ui.navigation.MediaRoutes
import com.yunzia.hyperstar.ui.navigation.PowerMenuRoutes
import com.yunzia.hyperstar.ui.navigation.SystemUIRoutes
import com.yunzia.hyperstar.ui.navigation.rememberNavigator
import com.yunzia.hyperstar.ui.screen.module.notDeveloper.NotDeveloperScreen
import com.yunzia.hyperstar.ui.screen.module.barrage.BarrageScreen
import com.yunzia.hyperstar.ui.screen.module.home.HomeScreen
import com.yunzia.hyperstar.ui.screen.module.mms.MMSScreen
import com.yunzia.hyperstar.ui.screen.module.screenshot.ScreenshotScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.SystemUIScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.ControlCenterColorScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.ControlCenterListScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.card.QSCardColorScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.card.QSCardListScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.devicecenter.DeviceCenterColorScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.list.QSListColorScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.list.QsListViewScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.media.MediaSettingsScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.media.app.MediaAppSettingsPager
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.slider.ToggleSliderColorsScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.other.notification.NotificationAppDetail
import com.yunzia.hyperstar.ui.screen.module.systemui.other.notification.NotificationOfImScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.other.powermenu.PowerMenuStyleScreen
import com.yunzia.hyperstar.ui.screen.module.systemui.other.powermenu.SelectFunScreen
import com.yunzia.hyperstar.ui.screen.module.thememanager.ThemeManagerScreen
import com.yunzia.hyperstar.ui.screen.pagers.CurrentVersionLogScreen
import com.yunzia.hyperstar.ui.screen.pagers.DonationPage
import com.yunzia.hyperstar.ui.screen.pagers.FPSMonitor
import com.yunzia.hyperstar.ui.screen.pagers.GoRootPager
import com.yunzia.hyperstar.ui.screen.pagers.LanguagePager
import com.yunzia.hyperstar.ui.screen.pagers.LogHistoryScreen
import com.yunzia.hyperstar.ui.screen.pagers.main.MainPager
import com.yunzia.hyperstar.ui.screen.pagers.main.MainPagerInLand
import com.yunzia.hyperstar.ui.screen.pagers.NeedMessageScreen
import com.yunzia.hyperstar.ui.screen.pagers.main.RebootDialog
import com.yunzia.hyperstar.ui.screen.pagers.ReferencesScreen
import com.yunzia.hyperstar.ui.screen.pagers.SettingsShowScreen
import com.yunzia.hyperstar.ui.screen.pagers.TranslatorScreen
import com.yunzia.hyperstar.ui.screen.pagers.UpdaterScreen
import com.yunzia.hyperstar.ui.screen.welcome.ActivePage
import com.yunzia.hyperstar.ui.screen.welcome.WelcomeScreen
import com.yunzia.hyperstar.utils.isFold
import com.yunzia.hyperstar.utils.isPad
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtils.Companion.MiuixPopupHost


val LocalMainPagerState = compositionLocalOf<PagerState> {
    error("LocalMainPagerState not provider")
}
val LocalRebootDialogState = compositionLocalOf<MutableState<Boolean>> {
    error("LocalMainPagerState not provider")
}

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("SourceLockedOrientationActivity", "UnusedBoxWithConstraintsScope",
    "LocalContextConfigurationRead"
)
@Composable
fun App(){
    val context = LocalContext.current
    val activity = LocalActivity.current as MainActivity

    Scaffold(
        modifier = Modifier
    ) {
        Log.d("MainPageContent", "MainPageContent XScaffold: init")
        var showInactivePage by remember { mutableStateOf(false) }
        LaunchedEffect(activity.appViewModel.isActive.value) {
            if (!activity.appViewModel.isActive.value) {
                delay(350)
                showInactivePage = !activity.appViewModel.isActive.value
            } else {
                showInactivePage = false
            }
        }
        AnimatedVisibility (
            showInactivePage,
            modifier = Modifier.background(colorScheme.surface),
            enter = slideInVertically { it / 10 } + fadeIn() + scaleIn(initialScale = 0.9f, transformOrigin = TransformOrigin(0.5f, 1f)),
            exit = slideOutVertically { it / 10 } + fadeOut() + scaleOut(transformOrigin = TransformOrigin(0.5f, 1f)),
        ) {
            if (!isFold() && !isPad()) {
                activity.requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            ActivePage()
        }
        if (showInactivePage) return@Scaffold

        val navigator = rememberNavigator(MainRoutes.Key)
        val welcome = remember { mutableStateOf(PreferencesUtil.getBoolean("is_first_use",true)) }
        val easing  = CubicBezierEasing(.42f,0f,0.26f,.85f)
        val coroutineScope = rememberCoroutineScope()

        CompositionLocalProvider(LocalNavigator provides navigator) {

            AnimatedVisibility(
                !welcome.value,
                enter = fadeIn(animationSpec = tween(300, easing = easing)) + scaleIn(animationSpec = tween(300, easing = easing),initialScale = 0.9f),
                exit = fadeOut() + scaleOut()
            ) {

                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                Log.d("App", "App: layout")

                val rebootStyle = activity.rebootStyle
                val rebootDialogState = rememberSaveable { mutableStateOf(false) }
                if (rebootStyle.intValue == 0){ RebootDialog(rebootDialogState) }
                val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })

                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val showTwoPanes = shouldShowSplitPane()
                    if(showTwoPanes.value) {
                        CompositionLocalProvider(
                            LocalMainPagerState provides pagerState,
                            LocalRebootDialogState provides rebootDialogState
                        ) {
                            MainPagerInLand(
                                modifier = Modifier
                                    .weight(0.45f)
                                    .zIndex(1f),
                            )
                        }
                    }

                    NavDisplay(
                        modifier = Modifier.weight(if (showTwoPanes.value) 0.55f else 1f),
                        backStack = navigator.backStack,
                        entryDecorators = listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator()
                        ),
                        onBack = {
                            if (navigator.backStack.size >= 2) {
                                navigator.goBack()
                            }
                        },
                        entryProvider = entryProvider {

                            entry<MainRoutes.Key> { if (showTwoPanes.value) EmptyPage() else
                                CompositionLocalProvider(
                                    LocalMainPagerState provides pagerState,
                                    LocalRebootDialogState provides rebootDialogState
                                ) { MainPager() } }

                            entry<SystemUIRoutes.ColorEdit> { ControlCenterColorScreen() }

                            entry<SystemUIRoutes.LayoutArrangement> { ControlCenterListScreen() }

                            entry<SystemUIRoutes.Media> { MediaSettingsScreen() }

                            entry<SystemUIRoutes.CardList> { QSCardListScreen() }

                            entry<SystemUIRoutes.TileLayout> { QsListViewScreen() }

                            entry<MediaRoutes.MediaApp> { MediaAppSettingsPager() }

                            entry<ColorEditRoutes.CardTileColor> { QSCardColorScreen() }

                            entry<ColorEditRoutes.ToggleSliderColor> { ToggleSliderColorsScreen() }

                            entry<ColorEditRoutes.DeviceCenterColor> { DeviceCenterColorScreen() }

                            entry<ColorEditRoutes.ListColor> { QSListColorScreen() }

                            entry<MainRoutes.GoRoot> { GoRootPager() }

                            entry<MainRoutes.NotDeveloper> { NotDeveloperScreen() }

                            entry<MainRoutes.Language> { LanguagePager() }

                            entry<MainRoutes.Translator> { TranslatorScreen() }

                            entry<MainRoutes.SystemUI> { SystemUIScreen() }

                            entry<MainRoutes.Donation> { DonationPage()  }

                            entry<MainRoutes.Show> { SettingsShowScreen() }

                            entry<MainRoutes.Message> { NeedMessageScreen()  }

                            entry<MainRoutes.References> { ReferencesScreen()  }

                            entry<MainRoutes.Home> { HomeScreen() }

                            entry<MainRoutes.Screenshot> { ScreenshotScreen() }

                            entry<MainRoutes.MMS> { MMSScreen() }

                            entry<MainRoutes.Barrage> { BarrageScreen() }

                            entry<MainRoutes.ThemeManager> { ThemeManagerScreen() }

                            entry<MainRoutes.Updater> { UpdaterScreen() }

                            entry<SystemUIRoutes.PowerMenu> { PowerMenuStyleScreen() }

                            entry<SystemUIRoutes.NotificationOfIm> { NotificationOfImScreen() }

                            entry<SystemUIRoutes.NotificationImAppDetail> { NotificationAppDetail() }

                            entry<MainRoutes.CurrentLog> { CurrentVersionLogScreen() }

                            entry<MainRoutes.LogHistory> { LogHistoryScreen() }

                            entry<PowerMenuRoutes.FunSelect>{ SelectFunScreen(it) }
                        }
                    )

                }
            }
            AnimatedVisibility(
                welcome.value,
                exit = fadeOut(animationSpec = tween(300, easing = easing)) + scaleOut(animationSpec = tween(300, easing = easing),targetScale = 0.9f)
            ) {
                if (!isFold() && !isPad()){
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
                val welcomeState = rememberPagerState(initialPage = 0 ,pageCount = { 7 })

                LaunchedEffect(Unit) {
                    if (!welcome.value){
                        delay(300)
                        coroutineScope.launch {
                            welcomeState.scrollToPage(0)
                        }
                    }

                }
                WelcomeScreen(welcome,welcomeState)
            }
        }
    }

    FPSMonitor(activity.showFPSMonitor.value)

}

@Composable
fun EmptyPage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            tint = colorScheme.secondary,
            modifier = Modifier.size(256.dp)
        )
    }
}