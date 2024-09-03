package com.chaos.hyperstar.ui.pagers

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.wear.compose.material.Icon
import com.chaos.hyperstar.R
import com.chaos.hyperstar.utils.PreferencesUtil
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.MiuixNavigationBar
import top.yukonga.miuix.kmp.MiuixScrollBehavior
import top.yukonga.miuix.kmp.MiuixTopAppBar
import top.yukonga.miuix.kmp.NavigationItem
import top.yukonga.miuix.kmp.basic.MiuixCard
import top.yukonga.miuix.kmp.basic.MiuixHorizontalPager
import top.yukonga.miuix.kmp.basic.MiuixScaffold
import top.yukonga.miuix.kmp.basic.MiuixSurface
import top.yukonga.miuix.kmp.basic.MiuixText
import top.yukonga.miuix.kmp.rememberMiuixTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

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
        NavigationItem(stringResource(R.string.main_page_title), Icons.Default.Home),
        NavigationItem(stringResource(R.string.settings_page_title), Icons.Default.Settings),
        NavigationItem(stringResource(R.string.about_page_title), Icons.Default.Info),
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.debounce(150).collectLatest {
            targetPage = pagerState.currentPage
        }
    }

    var TopAppBar_title by remember { mutableStateOf(items[0].label) }

    val showFPSMonitor = remember { mutableStateOf(PreferencesUtil.getBoolean("show_FPS_Monitor",false)) }
    val enablePageUserScroll = remember { mutableStateOf(PreferencesUtil.getBoolean("page_user_scroll",false)) }
    val enableTopBarBlur = remember { mutableStateOf(PreferencesUtil.getBoolean("top_Bar_blur",false)) }
    val enableBottomBarBlur = remember { mutableStateOf(PreferencesUtil.getBoolean("bottom_Bar_blur",false)) }
    val enableOverScroll = remember { mutableStateOf(PreferencesUtil.getBoolean("over_scroll",false)) }

    val show = remember { mutableStateOf(false) }

    if (show.value){
        itemPopu()
    }
    MiuixSurface {
        MiuixScaffold(
            modifier = Modifier.fillMaxSize(),
            enableTopBarBlur = enableTopBarBlur.value,
            enableBottomBarBlur = enableBottomBarBlur.value,
            topBar = {
                MiuixTopAppBar(
                    color = if (enableTopBarBlur.value) Color.Transparent else colorScheme.background,
                    title = TopAppBar_title,
                    scrollBehavior = currentScrollBehavior,
                    actions = {
                        IconButton(
                            modifier = Modifier.padding(end = 12.dp),
                            onClick = {
                                show.value = !show.value
                                Log.d("ggc", "IconButton: onClick")
                                //val res:String = Utils.rootShell("killall com.android.systemui")
                                //Log.d("ggc",res)
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
                        TopAppBar_title = items.get(index).label
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
}

@Composable
fun  itemPopu(

){
    var popupControl by remember { mutableStateOf(true) }
    if (popupControl){
        Popup(
            alignment = Alignment.TopEnd,
            offset = IntOffset(-100, 200),
            onDismissRequest = { popupControl = false } ,
        ) {
            MiuixCard(
                modifier = Modifier
                    .height(150.dp)
                    .width(100.dp)
//                .padding(horizontal = 28.dp)
//                .padding(bottom = 28.dp)
            ) {
                MiuixText(
                    text = "Card",
                    style = MiuixTheme.textStyles.paragraph,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(10.dp))
                MiuixText(
                    text = "123456789",
                    style = MiuixTheme.textStyles.paragraph
                )
                MiuixText(
                    text = "一二三四五六七八九",
                    style = MiuixTheme.textStyles.paragraph
                )
                MiuixText(
                    text = "!@#$%^&*()_+-=",
                    style = MiuixTheme.textStyles.paragraph
                )
            }
        }
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
                0 -> MainPage(
                    activity = activity,
                    topAppBarScrollBehavior = topAppBarScrollBehaviorList[0],
                    padding = padding,
                    enableOverScroll = enableOverScroll.value,
                )

                1 -> SettingsPage(
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

                else -> ThirdPage(
                    activity = activity,
                    topAppBarScrollBehavior = topAppBarScrollBehaviorList[2],
                    padding = padding,
                    enableOverScroll = enableOverScroll.value,
                )
            }
        }
    )
}