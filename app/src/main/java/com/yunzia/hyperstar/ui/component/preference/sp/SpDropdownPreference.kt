package com.yunzia.hyperstar.ui.component.preference.sp

import Searchable
import androidx.annotation.ArrayRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import com.yunzia.hyperstar.prefs.SPUtils
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.rememberPreferenceValue
import com.yunzia.hyperstar.ui.component.preference.searchablePreferenceModifier
import com.yunzia.hyperstar.ui.component.preference.impl.DropdownPreferenceImpl

@Searchable
@Composable
fun PreferenceGroupScope.SpDropdownPreference(
    key: String,
    title: String,
    summary: String? = null,
    entries: List<String>,
    defaultValue: Int = 0,
    enabled: Boolean = true,
    visible: () -> Boolean = { true },
) {
    if (!visible()) return
    val valueState = rememberPreferenceValue(SPUtils.getInt(key, defaultValue))
    DropdownPreferenceImpl(
        modifier = searchablePreferenceModifier(key),
        title = title,
        summary = summary,
        entries = entries,
        selectedIndex = valueState.value,
        enabled = enabled,
        onSelectedIndexChange = {
            valueState.value = it
            SPUtils.putInt(key, it)
        },
    )
}


@Searchable
@Composable
fun PreferenceGroupScope.SpDropdownPreference(
    key: String,
    title: String,
    summary: String? = null,
    @ArrayRes entriesId: Int,
    defaultValue: Int = 0,
    enabled: Boolean = true,
    visible: () -> Boolean = { true },
) {
    if (!visible()) return
    val entries = stringArrayResource(entriesId).toList()
    val valueState = rememberPreferenceValue(SPUtils.getInt(key, defaultValue))
    DropdownPreferenceImpl(
        modifier = searchablePreferenceModifier(key),
        title = title,
        summary = summary,
        entries = entries,
        selectedIndex = valueState.value,
        enabled = enabled,
        onSelectedIndexChange = {
            valueState.value = it
            SPUtils.putInt(key, it)
        },
    )
}
