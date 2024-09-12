@file:OptIn(ExperimentalFoundationApi::class)

package com.chaos.hyperstar.ui.pagers

import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.IconButton
import androidx.compose.material3.Shapes
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendModeColorFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.wear.compose.material.Icon
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.module.controlcenter.card.getHeight
import com.chaos.hyperstar.utils.PreferencesUtil
import com.chaos.hyperstar.utils.Utils
import getWindowSize
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.BackHandler
import top.yukonga.miuix.kmp.MiuixNavigationBar
import top.yukonga.miuix.kmp.MiuixScrollBehavior
import top.yukonga.miuix.kmp.MiuixSuperArrow
import top.yukonga.miuix.kmp.MiuixTopAppBar
import top.yukonga.miuix.kmp.NavigationItem
import top.yukonga.miuix.kmp.basic.MiuixButton
import top.yukonga.miuix.kmp.basic.MiuixCard
import top.yukonga.miuix.kmp.basic.MiuixHorizontalPager
import top.yukonga.miuix.kmp.basic.MiuixScaffold
import top.yukonga.miuix.kmp.basic.MiuixSurface
import top.yukonga.miuix.kmp.basic.MiuixText
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowRight
import top.yukonga.miuix.kmp.rememberMiuixTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissPopup
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.showPopup

@OptIn(FlowPreview::class)
@Composable
fun UITest(
    activity: ComponentActivity,
    colorMode: MutableState<Int>,
) {
    val topAppBarScrollBehavior0 = MiuixScrollBehavior(rememberMiuixTopAppBarState())
    val topAppBarScrollBehavior1 = MiuixScrollBehavior(rememberMiuixTopAppBarState())
    val topAppBarScrollBehavior2 = MiuixScrollBehavior(rememberMiuixTopAppBarState())

    val topAppBarScrollBehaviorList = listOf(
        topAppBarScrollBehavior0, topAppBarScrollBehavior1, topAppBarScrollBehavior2
    )

    val pagerState = rememberPagerState(pageCount = { 3 })
    var targetPage by remember { mutableStateOf(pagerState.currentPage) }
    val coroutineScope = rememberCoroutineScope()

    val currentScrollBehavior = when (pagerState.currentPage) {
        0 -> topAppBarScrollBehaviorList[0]
        1 -> topAppBarScrollBehaviorList[1]
        else -> topAppBarScrollBehaviorList[2]
    }

    val items = listOf(

        NavigationItem(stringResource(R.string.main_page_title), ImageVector.vectorResource(id = R.drawable.home)),
        NavigationItem(stringResource(R.string.settings_page_title), ImageVector.vectorResource(id = R.drawable.setting)),
        NavigationItem(stringResource(R.string.about_page_title), ImageVector.vectorResource(id = R.drawable.about)),
    )

    var pagerTitle by remember { mutableStateOf(items[targetPage].label) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.debounce(150).collectLatest {
            targetPage = pagerState.currentPage
            pagerTitle = items[pagerState.currentPage].label
        }
    }

    val showFPSMonitor = remember { mutableStateOf(PreferencesUtil.getBoolean("show_FPS_Monitor",false)) }
    val enablePageUserScroll = remember { mutableStateOf(PreferencesUtil.getBoolean("page_user_scroll",false)) }
    val enableTopBarBlur = remember { mutableStateOf(PreferencesUtil.getBoolean("top_Bar_blur",false)) }
    val enableBottomBarBlur = remember { mutableStateOf(PreferencesUtil.getBoolean("bottom_Bar_blur",false)) }
    val enableOverScroll = remember { mutableStateOf(PreferencesUtil.getBoolean("over_scroll",false)) }

    val show = remember { mutableStateOf(false) }

    val view = LocalView.current
    MiuixSurface {
        MiuixScaffold(
            modifier = Modifier.fillMaxSize(),
            enableTopBarBlur = enableTopBarBlur.value,
            enableBottomBarBlur = enableBottomBarBlur.value,
            topBar = {
                MiuixTopAppBar(
                    color = if (enableTopBarBlur.value) Color.Transparent else colorScheme.background,
                    title = pagerTitle,
                    largeTitle = if (targetPage == 2) "" else pagerTitle,
                    scrollBehavior = currentScrollBehavior,
                    actions = {

                        IconButton(
                            modifier = Modifier.padding(end = 12.dp),
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                show.value = true
                                Log.d("ggc", "IconButton: onClick")
                            }) {


                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                //ImageVector.vectorResource(R.drawable.bar_reboot_icon),
                                contentDescription = "restart",
                                tint = colorScheme.onPrimary)

                        }
                    }
                )
            },
            bottomBar = {
                MiuixNavigationBar(
                    color = if (enableBottomBarBlur.value) Color.Transparent else colorScheme.background,
                    items = items,
                    selected = targetPage,
                    onClick = { index ->
                        targetPage = index
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )

            },
        ) { padding ->
            AppHorizontalPager(
                activity = activity,
                pagerState = pagerState,
                colorMode = colorMode,
                topAppBarScrollBehaviorList = topAppBarScrollBehaviorList,
                padding = padding,
                showFPSMonitor = showFPSMonitor,
                enablePageUserScroll = enablePageUserScroll,
                enableTopBarBlur = enableTopBarBlur,
                enableBottomBarBlur = enableBottomBarBlur,
                enableOverScroll = enableOverScroll,
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

    //LocalHapticFeedback.current

    AnimatedVisibility(
        show.value,
        enter = fadeIn(),
        exit = fadeOut()
    ){
//        BackHandler(
//            dismiss = { dismissPopup() },
//            onDismissRequest = { show.value = false }
//        )

        val alpha: Float by animateFloatAsState(if (show.value) 0.3f else 0f)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(alpha = alpha)
                .background(color = Color.Black)
                .clickable {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    show.value = false
                }
        ){

        }

    }
    ItemPopu(show)

}




@Composable
fun  ItemPopu(show: MutableState<Boolean>) {
    val view = LocalView.current
    AnimatedVisibility (
        show.value,
        enter = fadeIn() + slideInVertically() { -it/6 },

        exit = fadeOut() + slideOutVertically() { -it/6 }
    ){

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            MiuixCard(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    //.fillMaxHeight(0.2f)
                    .clickable(enabled = false) {},
                cornerRadius = 24.dp,
                insideMargin = DpSize(0.dp,30.dp)
            ) {
//                top = 30.dp,
                MiuixText(
                    text = "快速重启",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    style = MiuixTheme.textStyles.paragraph,
                    fontWeight = FontWeight.W700,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )

                Button(
                    shape = RoundedCornerShape(0.dp),
                    contentPadding = PaddingValues(24.dp,20.dp),
                    colors = ButtonColors(Color.Transparent,Color.Transparent,Color.Transparent,Color.Transparent),
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        Utils.rootShell("killall com.android.systemui")
                    }
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        MiuixText(
                            text = "系统界面",
                            modifier = Modifier.weight(1f),
                            style = MiuixTheme.textStyles.paragraph,
                            //textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Image(
                            modifier = Modifier
                                .size(15.dp)
                                .padding(start = 6.dp),
                            imageVector = MiuixIcons.ArrowRight,
                            contentDescription = null,
                            colorFilter = BlendModeColorFilter(colorScheme.subDropdown, BlendMode.SrcIn),
                        )


                    }
                    //
                }

//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(Color.Transparent)
//                        .clickable {
//                            Utils.rootShell("killall com.android.systemui")
//
//                        },
//                    horizontalArrangement = Arrangement.Center,
//                    verticalAlignment = Alignment.CenterVertically
//                    ){
//                    MiuixText(
//                        text = "系统界面",
//                        modifier = Modifier.padding(vertical = 18.dp),
//                        style = MiuixTheme.textStyles.paragraph,
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 18.sp
//                    )
//
//                }
//                MiuixSuperArrow(
//                    title = "系统界面",
//                    modifier = Modifier.fillMaxWidth(),
//                    //insideMargin = DpSize(28.dp,18.dp),
//                    onClick = {
//                        Utils.rootShell("killall com.android.systemui")
//                    }
//                )
                //Spacer(modifier = Modifier.padding(vertical = 10.dp))
            }
        }
//        Popup(
//            alignment = Alignment.TopCenter,
//            //offset = IntOffset(-70, 220),
//            onDismissRequest = { show.value = false } ,
//        ) {
//
//        }
    }

}

@Composable
fun AppHorizontalPager(
    activity: ComponentActivity,
    colorMode: MutableState<Int>,
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    topAppBarScrollBehaviorList: List<MiuixScrollBehavior>,
    padding: PaddingValues,
    showFPSMonitor: MutableState<Boolean>,
    enablePageUserScroll: MutableState<Boolean>,
    enableTopBarBlur: MutableState<Boolean>,
    enableBottomBarBlur: MutableState<Boolean>,
    enableOverScroll: MutableState<Boolean>,
) {
    MiuixHorizontalPager(
        pagerState = pagerState,
        userScrollEnabled = enablePageUserScroll.value,
        pageContent = { page ->

            when (page) {

                0 ->{

                    MainPage(
                        activity = activity,
                        topAppBarScrollBehavior = topAppBarScrollBehaviorList[0],
                        padding = padding,
                        enableOverScroll = enableOverScroll.value,
                    )


                }


                1 -> {
                    SettingsPage(
                        activity = activity,
                        topAppBarScrollBehavior = topAppBarScrollBehaviorList[1],
                        padding = padding,
                        colorMode = colorMode,
                        showFPSMonitor = showFPSMonitor,
                        enablePageUserScroll = enablePageUserScroll,
                        enableTopBarBlur = enableTopBarBlur,
                        enableBottomBarBlur = enableBottomBarBlur,
                        enableOverScroll = enableOverScroll,
                    )
                }

                else -> {
                    ThirdPage(
                        activity = activity,
                        topAppBarScrollBehavior = topAppBarScrollBehaviorList[2],
                        padding = padding,
                        enableOverScroll = enableOverScroll.value,
                    )
                }
            }
        }
    )
}