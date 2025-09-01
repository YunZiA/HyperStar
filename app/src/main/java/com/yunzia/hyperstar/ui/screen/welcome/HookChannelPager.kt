package com.yunzia.hyperstar.ui.screen.welcome

import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.yunzia.hyperstar.ui.component.modifier.bounceAnimN
import com.yunzia.hyperstar.ui.screen.pagers.titleColor
import com.yunzia.hyperstar.utils.OSVersion
import com.yunzia.hyperstar.utils.SPUtils
import com.yunzia.hyperstar.utils.getHookChannel
import com.yunzia.hyperstar.utils.getSettingChannel
import com.yunzia.hyperstar.utils.isBetaOS
import com.yunzia.hyperstar.utils.isOS2
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.CheckboxLocation
import top.yukonga.miuix.kmp.extra.SuperCheckbox
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.G2RoundedCornerShape

@Composable
fun HookChannelPager(
    pagerState: PagerState
) {
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()
    val hookChannel = remember { mutableIntStateOf(getSettingChannel()) }
    val activity = LocalActivity.current as MainActivity

    val hookChannelItems = stringArrayResource(R.array.hook_channel_items).toList()
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier.size(99.dp)
        ) {
            Icon(
                ImageVector.vectorResource(R.drawable.hook_channe_go),
                contentDescription = "language",
                tint = Color(0xFF3482FF)
            )

        }

        Text(
            text = stringResource(R.string.title_hook_channel),
            modifier = Modifier.padding(vertical = 10.dp),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 10.dp)
                .padding(horizontal = 4.dp)
        ) {

            item{
                Spacer(modifier = Modifier.height(10.dp))
            }
            hookChannelItems.forEachIndexed { index, s ->
                channelItem(
                    s,index + 1,hookChannel
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
                    pagerState.animateScrollToPage(6)
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


private fun LazyListScope.channelItem(
    channel:String,
    index:Int,
    selectedItem: MutableIntState
) {

    val isSelected = index == selectedItem.intValue

    item(channel) {
        SuperCheckbox(
            title = "Xiaomi HyperOS $index.0",
            titleColor = titleColor(isSelected),
            checked = isSelected,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .padding(vertical = 5.dp)
                .bounceAnimN {
                    Log.d("ggc", "channelItem: $isSelected")
                    if (isSelected) {
                        Log.d("ggc", "channelItem: isSelected $isSelected")
                        SPUtils.setInt("is_Hook_Channel", selectedItem.intValue)
                    }
                }
                .clip(G2RoundedCornerShape(CardDefaults.CornerRadius))
                .background(if (isSelected) colorScheme.tertiaryContainer else colorScheme.surfaceVariant),
            checkboxLocation = CheckboxLocation.Right,
            insideMargin = PaddingValues(20.dp),
            onCheckedChange = {
                selectedItem.intValue = index

            }
        )

    }


}