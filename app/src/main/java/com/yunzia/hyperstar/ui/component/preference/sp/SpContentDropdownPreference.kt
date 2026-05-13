package com.yunzia.hyperstar.ui.component.preference.sp

import androidx.compose.runtime.Composable
import Searchable
import androidx.annotation.ArrayRes
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.ui.res.stringArrayResource
import com.yunzia.hyperstar.prefs.PreferencesUtil
import com.yunzia.hyperstar.prefs.SPUtils
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.rememberPreferenceValue
import com.yunzia.hyperstar.ui.component.preference.searchablePreferenceModifier
import com.yunzia.hyperstar.ui.component.preference.impl.ContentDropdownPreferenceImpl

@Searchable
@Composable
fun PreferenceGroupScope.SpContentDropdownPreference(
    key: String,
    title: String,
    @ArrayRes option: Int,
    showOption: Int,
    summary: String? = null,
    visible: () -> Boolean = { true },
    content: @Composable () -> Unit,
) {
    if (!visible()) return
    val dropdownItems = stringArrayResource(id = option).toList()
    val selectedIndexState = rememberPreferenceValue(SPUtils.getInt(key, 0))
    ContentDropdownPreferenceImpl(
        modifier = searchablePreferenceModifier(key),
        title = title,
        items = dropdownItems,
        summary = summary,
        selectedIndex = selectedIndexState.value,
        showOption = showOption,
        onSelectedIndexChange = {
            selectedIndexState.value = it
            SPUtils.putInt(key, it)
        },
        content = content,
    )
}


@Composable
fun PreferenceGroupScope.SpContentDropdownPreference(
    key: String,
    title: String,
    @ArrayRes option: Int,
    summary: String? = null,
    content: @Composable (AnimatedContentScope.(Int) -> Unit),
) {
    val selectedIndexState = rememberPreferenceValue(SPUtils.getInt(key, 0))
    val dropdownItems = stringArrayResource(id = option).toList()
    ContentDropdownPreferenceImpl(
        title = title,
        items = dropdownItems,
        summary = summary,
        selectedIndex = selectedIndexState.value,
        onSelectedIndexChange = {
            selectedIndexState.value = it
            SPUtils.putInt(key, it)
        },
        content = content,
    )
}

