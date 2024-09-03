package com.chaos.hyperstar.ui.module.controlcenter.media

import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.MiuixActivitySuperArrow
import com.chaos.hyperstar.ui.base.SubMiuixTopAppBar
import com.chaos.hyperstar.ui.base.XMiuixClass
import com.chaos.hyperstar.ui.base.XMiuixSlider
import com.chaos.hyperstar.ui.base.XMiuixSuperDropdown
import com.chaos.hyperstar.ui.base.XMiuixSuperSliderSwitch
import com.chaos.hyperstar.ui.base.XMiuixSuperSwitch
import com.chaos.hyperstar.ui.module.controlcenter.media.app.MediaDefaultAppSettingsActivity
import com.chaos.hyperstar.ui.pagers.FPSMonitor
import com.chaos.hyperstar.ui.pagers.OtherComponent
import com.chaos.hyperstar.ui.pagers.SecondComponent
import com.chaos.hyperstar.ui.pagers.TextComponent
import com.chaos.hyperstar.utils.PreferencesUtil
import com.chaos.hyperstar.utils.SPUtils
import com.chaos.hyperstar.utils.Utils
import getWindowSize
import top.yukonga.miuix.kmp.HorizontalDivider
import top.yukonga.miuix.kmp.MiuixScrollBehavior
import top.yukonga.miuix.kmp.MiuixSuperDropdown
import top.yukonga.miuix.kmp.MiuixSuperSwitch
import top.yukonga.miuix.kmp.MiuixTopAppBar
import top.yukonga.miuix.kmp.basic.MiuixCard
import top.yukonga.miuix.kmp.basic.MiuixLazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScaffold
import top.yukonga.miuix.kmp.basic.MiuixSlider
import top.yukonga.miuix.kmp.basic.MiuixSurface
import top.yukonga.miuix.kmp.basic.MiuixText
import top.yukonga.miuix.kmp.rememberMiuixTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme


@Composable
fun MediaSettingsPager(activity: ComponentActivity) {
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
                    title = "妙播设置",
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
            val isCoverBackground = remember { mutableStateOf(SPUtils.getBoolean("is_cover_background",false)) }
            MiuixCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                insideMargin = DpSize(0.dp,14.dp)
            ) {

                MiuixText(
                    text = "基础设置",
                    modifier = Modifier
                        .padding(horizontal = 28.dp)
                        .padding(top = 14.dp, bottom = 8.dp),
                    fontSize = 15.sp,
                    color = colorResource(R.color.class_name_color),
                    fontWeight = FontWeight.Medium
                )
                MiuixActivitySuperArrow(
                    title = "默认播放应用选择",
                    context = activity,
                    activity = MediaDefaultAppSettingsActivity::class.java
                )
            }

            MiuixCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp),
                insideMargin = DpSize(0.dp,14.dp)
            ) {

                MiuixText(
                    text = "常规播放页",
                    modifier = Modifier
                        .padding(horizontal = 28.dp)
                        .padding(top = 14.dp, bottom = 8.dp),
                    fontSize = 15.sp,
                    color = colorResource(R.color.class_name_color),
                    fontWeight = FontWeight.Medium
                )
                XMiuixSuperSwitch(
                    title = stringResource(R.string.is_cover_background_title),
                    key = "is_cover_background",
                    state = isCoverBackground
                )
                AnimatedVisibility(isCoverBackground.value) {
                    Column {
                        XMiuixSuperSliderSwitch(
                            switchTitle = stringResource(R.string.is_cover_scale_background_title),
                            switchKey = "is_cover_scale_background",
                            title = stringResource(R.string.cover_scale_background_value_title),
                            key = "cover_scale_background_value",
                            progress = 1.5f,
                            maxValue = 2f,
                            minValue = 1.1f
                        )
                        XMiuixSuperSliderSwitch(
                            switchTitle = stringResource(R.string.is_cover_blur_background_title),
                            switchKey = "is_cover_blur_background",
                            title = stringResource(R.string.cover_blur_background_value_title),
                            key = "cover_blur_background_value",
                            progress = 50f,
                            maxValue = 60f,
                            minValue = 0f
                        )
                        XMiuixSuperSliderSwitch(
                            switchTitle = stringResource(R.string.is_cover_dim_background_title),
                            switchKey = "is_cover_dim_background",
                            title = stringResource(R.string.cover_dim_background_value_title),
                            key = "cover_dim_background_value",
                            progress = 50f,
                            maxValue = 255f,
                            minValue = 0f
                        )

                        XMiuixSuperSwitch(
                            title = "启用封面背景暗边",
                            key = "cover_anciently"
                        )
                    }

                }

            }

            MiuixCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp),
                insideMargin = DpSize(0.dp,14.dp)
            ) {

                MiuixText(
                    text = "扩展详情页",
                    modifier = Modifier
                        .padding(horizontal = 28.dp)
                        .padding(top = 14.dp, bottom = 8.dp),
                    fontSize = 15.sp,
                    color = colorResource(R.color.class_name_color),
                    fontWeight = FontWeight.Medium
                )

                XMiuixSuperDropdown(
                    title = "设备名显示模式",
                    key = "is_local_speaker",
                    activity = activity,
                    option = R.array.is_local_speaker_entire,
                )
            }


        }
    }
}