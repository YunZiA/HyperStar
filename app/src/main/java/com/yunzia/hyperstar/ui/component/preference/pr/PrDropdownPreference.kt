package com.yunzia.hyperstar.ui.component.preference.pr

import androidx.compose.runtime.Composable
import com.yunzia.hyperstar.prefs.PreferencesUtil
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.rememberPreferenceValue
import com.yunzia.hyperstar.ui.component.preference.impl.DropdownPreferenceImpl

@Composable
fun PreferenceGroupScope.PrDropdownPreference(
    key: String,
    title: String,
    summary: String? = null,
    entries: List<String>,
    defaultValue: Int = 0,
    enabled: Boolean = true,
    visible: () -> Boolean = { true },
) {
    if (!visible()) return
    val valueState = rememberPreferenceValue(PreferencesUtil.getInt(key, defaultValue))
    DropdownPreferenceImpl(
        title = title,
        summary = summary,
        entries = entries,
        selectedIndex = valueState.value,
        enabled = enabled,
        onSelectedIndexChange = {
            valueState.value = it
            PreferencesUtil.putInt(key, it)
        },
    )
}
