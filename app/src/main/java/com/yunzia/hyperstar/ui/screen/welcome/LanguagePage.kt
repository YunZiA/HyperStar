package com.yunzia.hyperstar.ui.screen.welcome

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
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
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.TextButton
import com.yunzia.hyperstar.ui.component.modifier.bounceAnimN
import com.yunzia.hyperstar.ui.screen.pagers.titleColor
import com.yunzia.hyperstar.utils.PreferencesUtil
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.CheckboxLocation
import top.yukonga.miuix.kmp.extra.SuperCheckbox
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.G2RoundedCornerShape

@Composable
fun LanguagePage(
    pagerState: PagerState,
) {

    val view = LocalView.current

    val activity = LocalActivity.current as MainActivity
    val languageList = stringArrayResource(R.array.language_list).toList()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val coroutineScope = rememberCoroutineScope()

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
        ) {
            Icon(
                ImageVector.vectorResource(R.drawable.language),
                contentDescription = "language",
                tint = Color(0xFF3482FF)
            )

        }
        Text(
            text = stringResource(R.string.set_language),
            modifier = Modifier.padding(vertical = 10.dp),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 10.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            languageList.forEachIndexed { index, language ->

                item(index) {
                    val isSelected = remember { derivedStateOf { activity.language.intValue == index } }

                    LanguageItem(language, index, isSelected) {
                        if (activity.language.intValue == index) return@LanguageItem
                        activity.language.intValue = index
                        PreferencesUtil.putInt("app_language", activity.language.intValue)
                        activity.setLocale(activity.language.intValue)
                    }

                }
            }
        }

        TextButton(
            stringResource(R.string.next),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 28.dp),
            colors = Color(0xFF3482FF),
            onClick = {

                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                coroutineScope.launch {
                    pagerState.animateScrollToPage(3)
                }

            }
        )
    }

}

@Composable
private fun LanguageItem(
    language: String,
    index: Int,
    isSelected: State<Boolean>,
    onCheckedChange: ((Boolean) -> Unit)
){



    SuperCheckbox(
        title = language,
        titleColor =  titleColor(isSelected.value),
        checked = isSelected.value,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .padding(vertical = 5.dp)
            .bounceAnimN {}
            .clip(G2RoundedCornerShape(CardDefaults.CornerRadius))
            .background(if (isSelected.value) colorScheme.tertiaryContainer else colorScheme.surfaceVariant)
        ,
        checkboxLocation = CheckboxLocation.Right,
        insideMargin = PaddingValues(20.dp),
        onCheckedChange =  {onCheckedChange(it) }
    )





}