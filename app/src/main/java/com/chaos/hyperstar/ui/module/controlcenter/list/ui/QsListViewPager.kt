package com.chaos.hyperstar.ui.module.controlcenter.list.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.MiuixActivitySuperArrow
import com.chaos.hyperstar.ui.base.SubMiuixTopAppBar
import com.chaos.hyperstar.ui.base.XMiuixClasser
import com.chaos.hyperstar.ui.base.XMiuixSlider
import com.chaos.hyperstar.ui.base.XMiuixSuperDropdown
import com.chaos.hyperstar.ui.base.XMiuixSuperSwitch
import com.chaos.hyperstar.ui.module.controlcenter.media.MediaSettingsActivity
import com.chaos.hyperstar.ui.pagers.FPSMonitor
import com.chaos.hyperstar.utils.PreferencesUtil
import com.chaos.hyperstar.utils.Utils
import getWindowSize
import top.yukonga.miuix.kmp.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.MiuixLazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScaffold
import top.yukonga.miuix.kmp.basic.MiuixSurface
import top.yukonga.miuix.kmp.rememberMiuixTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme


@Composable
fun QsListViewPager(
    activity: ComponentActivity,
) {
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberMiuixTopAppBarState())

    val showFPSMonitor = remember { mutableStateOf(PreferencesUtil.getBoolean("show_FPS_Monitor",false)) }
    val enableTopBarBlur = remember { mutableStateOf(PreferencesUtil.getBoolean("top_Bar_blur",false)) }
    val enableBottomBarBlur = remember { mutableStateOf(PreferencesUtil.getBoolean("bottom_Bar_blur",false)) }
    val enableOverScroll = remember { mutableStateOf(PreferencesUtil.getBoolean("over_scroll",false)) }

    MiuixSurface {
        MiuixScaffold(
            modifier = Modifier.fillMaxSize(),
            enableTopBarBlur = enableTopBarBlur.value,
            enableBottomBarBlur = enableBottomBarBlur.value,
            topBar = {
                SubMiuixTopAppBar(
                    color = if (enableTopBarBlur.value) Color.Transparent else colorScheme.background,
                    title = "磁贴布局",
                    scrollBehavior = topAppBarScrollBehavior,
                    activity = activity,
                    endClick = {
                        Utils.rootShell("killall com.android.systemui")
                    }
                )
            },

        ) { padding ->
            AppHorizontalPager(
                activity = activity,
                topAppBarScrollBehavior = topAppBarScrollBehavior,
                padding = padding,
                enableOverScroll = enableOverScroll.value,
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
fun AppHorizontalPager(
    activity : ComponentActivity,
    padding: PaddingValues,
    enableOverScroll: Boolean,
    topAppBarScrollBehavior : MiuixScrollBehavior
) {
    MiuixLazyColumn(
        modifier = Modifier.height(getWindowSize().height.dp),
        enableOverScroll = enableOverScroll,
        contentPadding = PaddingValues(top = padding.calculateTopPadding()+14.dp, bottom = padding.calculateBottomPadding()+28.dp),
        topAppBarScrollBehavior = topAppBarScrollBehavior
    ) {

        item {
            XMiuixClasser(
                title = "标题样式",
                top = 12.dp,
                content = {

                    XMiuixSlider(
                        title = "标题大小",
                        key = "list_label_size",
                        unit = "dp",
                        maxValue = 25f,
                        minValue = 0f,
                        progress = 13f,
                        decimalPlaces = 2
                    )

                    XMiuixSlider(
                        title = "标题宽度",
                        key = "list_label_width",
                        unit = "%",
                        maxValue = 100f,
                        minValue = 0f,
                        progress = 100f
                    )
                }
            )
            XMiuixClasser(
                title = "竖直间距",
                top = 12.dp,
                content = {

                    XMiuixSlider(
                        title = "无字样式",
                        key = "list_spacing_y",
                        unit = "%",
                        maxValue = 150f,
                        minValue = 0f,
                        progress = 100f
                    )

                    XMiuixSlider(
                        title = "有字样式",
                        key = "list_label_spacing_y",
                        unit = "%",
                        maxValue = 150f,
                        minValue = 0f,
                        progress = 100f
                    )
                }
            )

            XMiuixClasser(
                title = "上边距",
                top = 12.dp,
                content = {

                    XMiuixSlider(
                        title = "图标",
                        key = "list_icon_top",
                        unit = "%",
                        maxValue = 50F,
                        minValue = -50f,
                        progress = 0f
                    )

                    XMiuixSlider(
                        title = "标题",
                        key = "list_label_top",
                        unit = "%",
                        maxValue = 200F,
                        minValue = -100f,
                        progress = 100f
                    )
                }
            )




        }

    }

}