package com.yunzia.hyperstar.ui.component.preference.sp

import Searchable
import androidx.compose.runtime.Composable
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.rememberPreferenceValue
import com.yunzia.hyperstar.ui.component.preference.searchablePreferenceModifier
import com.yunzia.hyperstar.prefs.SPUtils
import com.yunzia.hyperstar.ui.component.preference.impl.ListPreferenceImpl

@Searchable
@Composable
fun PreferenceGroupScope.SpListPreference(
    key: String,
    title: String,
    summary: String? = null,
    icon: Int? = null,
    entries: List<String>,
    entryValues: List<String>,
    enabled: Boolean = true,
    defaultValue: String = "",
    visible: () -> Boolean = { true },
) {
    if (!visible()) return
    val valueState = rememberPreferenceValue(SPUtils.getString(key, defaultValue))
    ListPreferenceImpl(
        modifier = searchablePreferenceModifier(key),
        title = title,
        summary = summary,
        icon = icon,
        entries = entries,
        entryValues = entryValues,
        value = valueState.value,
        enabled = enabled,
        onValueChange = {
            valueState.value = it
            SPUtils.putString(key, it)
        },
    )
}
