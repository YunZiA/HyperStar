package com.yunzia.hyperstar.ui.screen.welcome

import android.view.HapticFeedbackConstants
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.Button
import com.yunzia.hyperstar.ui.component.PMiuixSuperDropdown
import com.yunzia.hyperstar.ui.component.PMiuixSuperSwitch
import com.yunzia.hyperstar.ui.component.XSuperDropdown
import com.yunzia.hyperstar.ui.component.classes
import com.yunzia.hyperstar.utils.PreferencesUtil
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Text

@Composable
fun BaseSettingPage(pagerState: PagerState) {
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()
    val selectedItem = remember { mutableIntStateOf(PreferencesUtil.getInt("app_language",0)) }
    val activity = LocalActivity.current as MainActivity
    val rebootStyle = activity.rebootStyle

    val languageList = stringArrayResource(R.array.language_list).toList()
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
        ) {
            Icon(
                ImageVector.vectorResource(R.drawable.basic_settings),
                contentDescription = "language",
                tint = Color(0xFF3482FF)
            )

        }
        Text(
            text = stringResource(R.string.basic_settings),
            modifier = Modifier.padding(vertical = 10.dp),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        LazyColumn(modifier = Modifier
            .weight(1f)
            .padding(bottom = 10.dp)
            .padding(horizontal = 4.dp)) {

            item{
                Spacer(modifier = Modifier.height(10.dp))
            }
            classes(
                title = R.string.show_title
            ){
                PMiuixSuperSwitch(
                    title = stringResource(R.string.is_hide_icon_title),
                    key = "is_hide_icon"
                )

                PMiuixSuperDropdown(
                    title = stringResource(R.string.title_reboot_menus_style),
                    option = R.array.reboot_menus_style,
                    selectedIndex = rebootStyle.intValue,
                    onSelectedIndexChange = {
                        rebootStyle.intValue = it
                        PreferencesUtil.putInt("reboot_menus_style", rebootStyle.intValue)
                    }
                )
                PMiuixSuperSwitch(
                    title = stringResource(R.string.click_bounce),
                    key = "bounce_anim_enable",
                    defValue = true
                )


            }
            classes(
                title = R.string.err_find
            ){
                XSuperDropdown(
                    title = stringResource(R.string.title_log_level),
                    summary = stringResource(R.string.summary_log_level),
                    dfOpt = 0,
                    option = R.array.log_level,
                    key = "log_level"
                )

            }


        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 28.dp),
            colors = Color(0xFF3482FF),
            onClick = {

                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                coroutineScope.launch {
                    pagerState.animateScrollToPage(5)
                }

            }
        ) {
            Text(
                stringResource(R.string.next),
                modifier = Modifier.padding(horizontal = 12.dp),
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

    }
}