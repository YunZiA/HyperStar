package com.yunzia.hyperstar.ui.pagers

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.NavPager
import com.yunzia.hyperstar.ui.base.enums.EventState
import com.yunzia.hyperstar.ui.base.modifier.bounceAnimN
import com.yunzia.hyperstar.ui.base.modifier.bounceClick
import com.yunzia.hyperstar.ui.base.modifier.bounceScale
import com.yunzia.hyperstar.utils.PreferencesUtil
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Checkbox
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.CheckboxLocation
import top.yukonga.miuix.kmp.extra.SuperCheckbox
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
fun LanguagePager(
    activity : MainActivity,
    navController: NavController

) {
    val selectedItem = remember { mutableIntStateOf(PreferencesUtil.getInt("app_language",0)) }

    val languageList = stringArrayResource(R.array.language_list).toList()

    NavPager(
        activityTitle = stringResource(R.string.language),
        navController = navController,
    ) {


        languageList.forEachIndexed { index, language ->

            languageItem(activity,language, index,selectedItem)

        }


    }
}


fun LazyListScope.languageItem(
    activity : MainActivity,
    language:String,
    index:Int,
    selectedItem: MutableIntState
){

    val isSelected =  index == selectedItem.intValue


    item(index){

        SuperCheckbox(
            title = language,
            titleColor =  titleColor(isSelected),
            checked = isSelected,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .padding(top = 10.dp)
                .bounceAnimN{
                    if (isSelected){
                        selectedItem.intValue = index
                        PreferencesUtil.putInt("app_language",selectedItem.intValue)
                        activity.recreate()
                    }
                }
                .clip(SmoothRoundedCornerShape(CardDefaults.ConorRadius))
                .background(if (isSelected) colorScheme.tertiaryContainer else colorScheme.surfaceVariant)
            ,
            checkboxLocation = CheckboxLocation.Right,
            insideMargin = PaddingValues(20.dp),
            onCheckedChange = {
                selectedItem.intValue = index

            }
        )

    }



}

@Composable
fun titleColor(
    isSelected: Boolean
): BasicComponentColors {
    return BasicComponentColors(
        color = if (isSelected) colorScheme.primary else colorScheme.onSurface,
        disabledColor = colorScheme.disabledOnSecondaryVariant
    )
}