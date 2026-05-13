package com.yunzia.hyperstar.ui.component.preference.pr

import androidx.annotation.ArrayRes
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import com.yunzia.hyperstar.prefs.PreferencesUtil
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.rememberPreferenceValue
import com.yunzia.hyperstar.ui.component.preference.impl.ContentDropdownPreferenceImpl

@Composable
fun PreferenceGroupScope.PrContentDropdownPreference(
    key: String,
    title: String,
    @ArrayRes option: Int,
    showOption: Int,
    summary: String? = null,
    content: @Composable () -> Unit,
) {
    val selectedIndexState = rememberPreferenceValue(PreferencesUtil.getInt(key, 0))
    val dropdownItems = stringArrayResource(id = option).toList()
    ContentDropdownPreferenceImpl(
        title = title,
        items = dropdownItems,
        summary = summary,
        selectedIndex = selectedIndexState.value,
        showOption = showOption,
        onSelectedIndexChange = {
            selectedIndexState.value = it
            PreferencesUtil.putInt(key, it)
        },
        content = content,
    )
}


@Composable
fun PreferenceGroupScope.PrContentDropdownPreference(
    key: String,
    title: String,
    @ArrayRes option: Int,
    summary: String? = null,
    content: @Composable (AnimatedContentScope.(Int) -> Unit),
) {
    val selectedIndexState = rememberPreferenceValue(PreferencesUtil.getInt(key, 0))
    val dropdownItems = stringArrayResource(id = option).toList()
    ContentDropdownPreferenceImpl(
        title = title,
        items = dropdownItems,
        summary = summary,
        selectedIndex = selectedIndexState.value,
        onSelectedIndexChange = {
            selectedIndexState.value = it
            PreferencesUtil.putInt(key, it)
        },
        content = content,
    )
}
