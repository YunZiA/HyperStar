package com.yunzia.hyperstar.ui.pagers

import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.drawablepainter.DrawablePainter
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.NavPager
import com.yunzia.hyperstar.ui.base.SuperIntentArrow
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.ui.base.enums.EventState
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.ui.base.modifier.bounceClick
import com.yunzia.hyperstar.ui.base.modifier.bounceScale
import com.yunzia.hyperstar.utils.PreferencesUtil
import com.yunzia.hyperstar.utils.SPUtils
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Checkbox
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

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

        val view = LocalView.current
        val eventState = remember { mutableStateOf(EventState.Idle) }

        val click = remember { mutableStateOf(false) }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .padding(top = 10.dp)
                .bounceScale(eventState){
                    if (!isSelected && click.value){
                        selectedItem.intValue = index
                        PreferencesUtil.putInt("app_language",selectedItem.intValue)
                        activity.recreate()
                    }
                    click.value = false
                },
            color = if (isSelected) colorScheme.tertiaryContainer  else colorScheme.surfaceVariant
        ) {

            Row(

                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick(eventState)
                    .clickable {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        click.value = true
                    }

            ) {

                Text(
                    text = language,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    color = if (isSelected) colorScheme.primary else colorScheme.onBackground
                )
                Box(
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .padding(start = 8.dp, end = 20.dp)
                ){
                    Checkbox(
                        modifier = Modifier
                            .padding(start = 8.dp),
                        enabled = true,
                        checked = isSelected,
                        onCheckedChange = {
                            click.value = true
                        }
                    )

                }

            }
        }

    }



}