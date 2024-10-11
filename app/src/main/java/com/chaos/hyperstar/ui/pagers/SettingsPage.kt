package com.chaos.hyperstar.ui.pagers

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaos.hyperstar.MainActivity
import com.chaos.hyperstar.R
import com.chaos.hyperstar.State
import com.chaos.hyperstar.ui.base.BaseActivity
import com.chaos.hyperstar.ui.base.PMiuixSuperDropdown
import com.chaos.hyperstar.ui.base.PMiuixSuperSwitch
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.utils.LanguageHelper
import com.chaos.hyperstar.utils.PreferencesUtil
import top.yukonga.miuix.kmp.basic.Checkbox
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.utils.enableOverscroll
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun SettingsPage(
    activity : MainActivity,
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    colorMode: MutableState<Int>,
    showFPSMonitor: MutableState<Boolean>,
    enablePageUserScroll:MutableState<Boolean>,
) {

    val language = remember { mutableIntStateOf(PreferencesUtil.getInt("app_language",0)) }

    val state = remember {
        mutableStateOf(activity.state)
    }
    val paddinged by remember { mutableStateOf(activity.paddings)}
    LaunchedEffect(padding) {
        if (state.value == State.Recreate){
            state.value = State.Start
        }

    }

    LazyColumn(
        modifier = Modifier.height(getWindowSize().height.dp),
        contentPadding =if (state.value == State.Recreate) PaddingValues(top = paddinged.calculateTopPadding()+14.dp, bottom = paddinged.calculateBottomPadding()+14.dp)
        else PaddingValues(top = padding.calculateTopPadding()+14.dp, bottom = padding.calculateBottomPadding()+14.dp),
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
                title = stringResource(R.string.language),
                option = R.array.language_list,
                activity = activity,
                selectedIndex = language.intValue,
                onSelectedIndexChange = {
                    language.intValue = it
                    PreferencesUtil.putInt("app_language",language.intValue)
                    activity.savePadding(padding)
                    activity.recreate()
                }
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
//            PMiuixSuperSwitch(
//                title = stringResource(R.string.over_scroll_title),
//                checked = if (enableOverscroll()) enableOverScroll.value else false,
//                onCheckedChange = {
//                    enableOverScroll.value = it
//                    PreferencesUtil.putBoolean("over_scroll",enableOverScroll.value)
//                },
//                enabled = enableOverscroll(),
//            )
            PMiuixSuperSwitch(
                title = stringResource(R.string.progress_effect_title),
                key = "is_progress_effect"
            )

        }

    }
}






