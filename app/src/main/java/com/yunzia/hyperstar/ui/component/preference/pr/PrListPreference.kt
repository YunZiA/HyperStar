package com.yunzia.hyperstar.ui.component.preference.pr

import androidx.compose.runtime.Composable
import com.yunzia.hyperstar.prefs.PreferencesUtil
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.rememberPreferenceValue
import com.yunzia.hyperstar.ui.component.preference.impl.ListPreferenceImpl

@Composable
fun PreferenceGroupScope.PrListPreference(
    key: String,
    title: String,
    summary: String? = null,
    icon: Int? = null,
    entries: List<String>,
    entryValues: List<String>,
    enabled: Boolean = true,
    defaultValue: String = "",
) {
    val valueState = rememberPreferenceValue(PreferencesUtil.getString(key, defaultValue))
    ListPreferenceImpl(
        title = title,
        summary = summary,
        icon = icon,
        entries = entries,
        entryValues = entryValues,
        value = valueState.value,
        enabled = enabled,
        onValueChange = {
            valueState.value = it
            PreferencesUtil.putString(key, it)
        },
    )
}
