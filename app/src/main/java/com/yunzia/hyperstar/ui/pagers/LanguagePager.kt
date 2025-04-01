package com.yunzia.hyperstar.ui.pagers

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.modifier.bounceAnimN
import com.yunzia.hyperstar.ui.base.pager.NavPager
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
    currentStartDestination: MutableState<String>

) {
    val activity = LocalActivity.current as MainActivity

    val languageList = stringArrayResource(R.array.language_list).toList()

    val recompose = currentRecomposeScope
    LaunchedEffect(activity.language.intValue) {
        activity.setLocale(activity.language.intValue)
        recompose.invalidate()
    }


    NavPager(
        activityTitle = stringResource(R.string.language),
        navController = navController,
        parentRoute = currentStartDestination,
    ) {


        languageList.forEachIndexed { index, language ->

            languageItem(language, index,activity.language)

        }


    }

}


private fun LazyListScope.languageItem(
    language:String,
    index:Int,
    selectedItem: MutableIntState
){

    val isSelected =  index == selectedItem.intValue


    item(index){

        val activity = LocalActivity.current as MainActivity
        SuperCheckbox(
            title = language,
            titleColor =  titleColor(isSelected),
            checked = isSelected,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .padding(top = 10.dp)
                .bounceAnimN {}
                .clip(SmoothRoundedCornerShape(CardDefaults.CornerRadius))
                .background(if (isSelected) colorScheme.tertiaryContainer else colorScheme.surfaceVariant)
            ,
            checkboxLocation = CheckboxLocation.Right,
            insideMargin = PaddingValues(20.dp),
            onCheckedChange = {
                selectedItem.intValue = index
                PreferencesUtil.putInt("app_language", selectedItem.intValue)
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