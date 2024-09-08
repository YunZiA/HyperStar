package com.chaos.hyperstar.ui.module.controlcenter.ui

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
import com.chaos.hyperstar.ui.base.XMiuixContentDropdown
import com.chaos.hyperstar.ui.base.XMiuixSuperDropdown
import com.chaos.hyperstar.ui.base.XMiuixSuperSwitch
import com.chaos.hyperstar.ui.module.controlcenter.list.QsListViewSettings
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
fun ControlCenterPager(
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
                    title = stringResource(R.string.controlcenter),
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
    topAppBarScrollBehavior : MiuixScrollBehavior,
) {
    MiuixLazyColumn(
        modifier = Modifier.height(getWindowSize().height.dp),
        enableOverScroll = enableOverScroll,
        contentPadding = PaddingValues(top = padding.calculateTopPadding()+14.dp, bottom = padding.calculateBottomPadding()+28.dp),
        topAppBarScrollBehavior = topAppBarScrollBehavior
    ) {

        item {
            MiuixActivitySuperArrow(
                title = "妙播设置",
                context = activity,
                activity = MediaSettingsActivity::class.java
            )
            XMiuixSuperDropdown(
                title = "控件·高级材质",
                key = "is_super_blur_Widget",
                option = R.array.is_super_blur_entire,
                activity = activity
            )
            XMiuixClasser(
                title = "普通磁贴",
                top = 12.dp,
                content = {
                    XMiuixSuperSwitch(
                        title = "背景圆角矩形",
                        key = "is_qs_tile_radius"
                    )
                    XMiuixSuperSwitch(
                        title = "标题颜色跟随图标",
                        key = "qs_list_tile_color_for_icon"
                    )
                    XMiuixSuperSwitch(
                        title = "标题超出跑马灯特效显示",
                        key = "list_tile_label_marquee"
                    )
                    XMiuixContentDropdown(
                        title = "标题显示样式",
                        key = "is_list_label_mode",
                        option = R.array.is_list_label_mode_entire,
                        showOption = 2,
                        activity = activity,
                        content = {
                            MiuixActivitySuperArrow(
                                title = "磁铁布局",
                                context = activity,
                                activity = QsListViewSettings::class.java
                            )

                        }
                    )
                }
            )




        }

    }

}