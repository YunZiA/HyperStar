package com.yunzia.hyperstar.ui.screen.pagers

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.modifier.bounceAnimN
import com.yunzia.hyperstar.prefs.PreferencesUtil
import com.yunzia.hyperstar.ui.component.preference.widget.PreferenceListPage
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.preference.CheckboxLocation
import top.yukonga.miuix.kmp.preference.CheckboxPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.shapes.SmoothRoundedCornerShape
import com.yunzia.hyperstar.ui.navigation.LocalNavigator

@Composable
fun LanguagePager(
) {
    val activity = LocalActivity.current as MainActivity
    val navController = LocalNavigator.current
    val languageList = stringArrayResource(R.array.language_list).toList()

    PreferenceListPage(
        title = stringResource(R.string.language),
        navController = navController,
    ) {
        languageList.forEachIndexed { index, language ->
            item(index){
                val isSelected = remember { derivedStateOf { activity.language.intValue == index } }

                LanguageItem(language, index,isSelected){
                    if (activity.language.intValue == index) return@LanguageItem
                    activity.language.intValue = index
                    PreferencesUtil.putInt("app_language", activity.language.intValue)
                    activity.setLocale(activity.language.intValue)
                }
            }
        }
    }


}

@Composable
private fun LanguageItem(
    language: String,
    index: Int,
    isSelected: State<Boolean>,
    onCheckedChange: (Boolean) -> Unit
){

    CheckboxPreference(
        title = language,
        titleColor =  titleColor(isSelected.value),
        checked = isSelected.value,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(top = 10.dp)
            .bounceAnimN {}
            .clip(SmoothRoundedCornerShape(CardDefaults.CornerRadius))
            .background(if (isSelected.value) colorScheme.tertiaryContainer else colorScheme.surfaceVariant)
        ,
        checkboxLocation = CheckboxLocation.End,
        insideMargin = PaddingValues(20.dp),
        onCheckedChange = { onCheckedChange(it) }
    )





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


@Composable
fun summaryColor(
    isSelected: Boolean
): BasicComponentColors {
    return BasicComponentColors(
        color = if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariantSummary,
        disabledColor = colorScheme.disabledOnSecondaryVariant
    )
}