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
import com.chaos.hyperstar.ui.base.PMiuixSuperDropdown
import com.chaos.hyperstar.ui.base.PMiuixSuperSwitch
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.utils.PreferencesUtil
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.utils.enableOverscroll
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun SettingsPage(
    activity : ComponentActivity,
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    colorMode: MutableState<Int>,
    showFPSMonitor: MutableState<Boolean>,
    enablePageUserScroll:MutableState<Boolean>,
    enableOverScroll: MutableState<Boolean>,
) {
    LazyColumn(
        modifier = Modifier.height(getWindowSize().height.dp),
        enableOverScroll = enableOverScroll.value,
        contentPadding = PaddingValues(top = padding.calculateTopPadding()+14.dp, bottom = padding.calculateBottomPadding()+14.dp),
        topAppBarScrollBehavior = topAppBarScrollBehavior
    ) {
        firstClasses(
            title = R.string.show_title
        ){
            PMiuixSuperSwitch(
                title = stringResource(R.string.is_hide_icon_title),
                key = "is_hide_icon"
            )

            PMiuixSuperDropdown(
                title = stringResource(R.string.color_mode_title),
                option = R.array.color_mode_items,
                activity = activity,
                selectedIndex = colorMode.value,
                onSelectedIndexChange = {
                    colorMode.value = it
                    PreferencesUtil.putInt("color_mode",colorMode.value)
                }
            )

            PMiuixSuperSwitch(
                title = stringResource(R.string.show_FPS_Monitor_title),
                checked = showFPSMonitor.value,
                onCheckedChange = {
                    showFPSMonitor.value = it
                    PreferencesUtil.putBoolean("show_FPS_Monitor",showFPSMonitor.value)
                }
            )
            PMiuixSuperSwitch(
                title = stringResource(R.string.page_user_scroll_title),
                checked = enablePageUserScroll.value,
                onCheckedChange = {
                    enablePageUserScroll.value = it
                    PreferencesUtil.putBoolean("page_user_scroll",enablePageUserScroll.value)
                }
            )
            PMiuixSuperSwitch(
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






