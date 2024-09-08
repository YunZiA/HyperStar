package com.chaos.hyperstar.ui.pagers

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.PMiuixSuperSwitch
import com.chaos.hyperstar.ui.base.XMiuixClasser
import com.chaos.hyperstar.utils.PreferencesUtil
import getWindowSize
import top.yukonga.miuix.kmp.MiuixScrollBehavior
import top.yukonga.miuix.kmp.MiuixSuperDropdown
import top.yukonga.miuix.kmp.MiuixSuperSwitch
import top.yukonga.miuix.kmp.basic.MiuixCard
import top.yukonga.miuix.kmp.basic.MiuixLazyColumn
import top.yukonga.miuix.kmp.basic.MiuixText
import top.yukonga.miuix.kmp.utils.enableOverscroll

@Composable
fun SettingsPage(
    activity : ComponentActivity,
    topAppBarScrollBehavior: MiuixScrollBehavior,
    padding: PaddingValues,
    colorMode: MutableState<Int>,
    showFPSMonitor: MutableState<Boolean>,
    enablePageUserScroll:MutableState<Boolean>,
    enableTopBarBlur: MutableState<Boolean>,
    enableBottomBarBlur: MutableState<Boolean>,
    enableOverScroll: MutableState<Boolean>,
) {
    MiuixLazyColumn(
        modifier = Modifier.height(getWindowSize().height.dp),
        enableOverScroll = enableOverScroll.value,
        contentPadding = PaddingValues(top = padding.calculateTopPadding()+14.dp, bottom = padding.calculateBottomPadding()+14.dp),
        topAppBarScrollBehavior = topAppBarScrollBehavior
    ) {
        item {
            XMiuixClasser(
                title = stringResource(R.string.show_title)
            ){
                PMiuixSuperSwitch(
                    title = stringResource(R.string.is_hide_icon_title),
                    key = "is_hide_icon"
                )

                MiuixSuperDropdown(
                    title = stringResource(R.string.color_mode_title),
                    items = activity.resources.getStringArray(R.array.color_mode_items).toList(),
                    selectedIndex = colorMode.value,
                    onSelectedIndexChange = {
                        colorMode.value = it
                        PreferencesUtil.putInt("color_mode",colorMode.value)
                    }
                )

                MiuixSuperSwitch(
                    title = stringResource(R.string.show_FPS_Monitor_title),
                    checked = showFPSMonitor.value,
                    onCheckedChange = {
                        showFPSMonitor.value = it
                        PreferencesUtil.putBoolean("show_FPS_Monitor",showFPSMonitor.value)
                    }
                )
                MiuixSuperSwitch(
                    title = stringResource(R.string.page_user_scroll_title),
                    checked = enablePageUserScroll.value,
                    onCheckedChange = {
                        enablePageUserScroll.value = it
                        PreferencesUtil.putBoolean("page_user_scroll",enablePageUserScroll.value)
                    }
                )
                MiuixSuperSwitch(
                    title = stringResource(R.string.top_Bar_blur_title),
                    checked = enableTopBarBlur.value,
                    onCheckedChange = {
                        enableTopBarBlur.value = it
                        PreferencesUtil.putBoolean("top_Bar_blur",enableTopBarBlur.value)
                    }
                )
                MiuixSuperSwitch(
                    title = stringResource(R.string.bottom_Bar_blur_title),
                    checked = enableBottomBarBlur.value,
                    onCheckedChange = {
                        enableBottomBarBlur.value = it
                        PreferencesUtil.putBoolean("bottom_Bar_blur",enableBottomBarBlur.value)
                    }
                )
                MiuixSuperSwitch(
                    title = stringResource(R.string.over_scroll_title),
                    checked = if (enableOverscroll()) enableOverScroll.value else false,
                    onCheckedChange = {
                        enableOverScroll.value = it
                        PreferencesUtil.putBoolean("over_scroll",enableOverScroll.value)
                    },
                    enabled = enableOverscroll(),
                )
                PMiuixSuperSwitch(
                    title = stringResource(R.string.progress_effect_title),
                    key = "is_progress_effect"
                )

            }

        }
    }
}