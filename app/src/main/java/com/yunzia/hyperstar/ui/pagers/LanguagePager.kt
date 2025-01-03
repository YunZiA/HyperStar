package com.yunzia.hyperstar.ui.pagers

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.NavPager
import com.yunzia.hyperstar.ui.base.modifier.bounceAnimN
import com.yunzia.hyperstar.utils.PreferencesUtil
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.extra.CheckboxLocation
import top.yukonga.miuix.kmp.extra.SuperCheckbox
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
fun LanguagePager(
    navController: NavController,
    currentStartDestination: SnapshotStateList<String>

) {
    val context = LocalContext.current
    val activity = context as MainActivity
    val selectedItem = remember { mutableIntStateOf(PreferencesUtil.getInt("app_language",0)) }

    val languageList = stringArrayResource(R.array.language_list).toList()

    NavPager(
        activityTitle = stringResource(R.string.language),
        navController = navController,
        currentStartDestination = currentStartDestination,
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
                .bounceAnimN {
                    Log.d("ggc", "languageItem: $isSelected")
                    if (isSelected) {
                        Log.d("ggc", "languageItem: isSelected $isSelected")
                        PreferencesUtil.putInt("app_language", selectedItem.intValue)
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