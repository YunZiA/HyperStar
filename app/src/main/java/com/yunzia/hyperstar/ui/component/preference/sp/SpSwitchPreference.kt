package com.yunzia.hyperstar.ui.component.preference.sp

import Searchable
import androidx.compose.runtime.Composable
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.rememberPreferenceValue
import com.yunzia.hyperstar.ui.component.preference.searchablePreferenceModifier
import com.yunzia.hyperstar.prefs.SPUtils
import com.yunzia.hyperstar.ui.component.preference.impl.SwitchPreferenceImpl

@Searchable
@Composable
fun PreferenceGroupScope.SpSwitchPreference(
    key: String,
    title: String,
    summary: String? = null,
    icon: Int? = null,
    enabled: Boolean = true,
    defaultValue: Boolean = false,
    visible: () -> Boolean = { true },
) {
    if (!visible()) return
    val checkedState = rememberPreferenceValue(SPUtils.getBoolean(key, defaultValue))
    SwitchPreferenceImpl(
        modifier = searchablePreferenceModifier(key),
        title = title,
        summary = summary,
        icon = icon,
        checked = checkedState.value,
        enabled = enabled,
        onCheckedChange = {
            checkedState.value = it
            SPUtils.putBoolean(key, it)
        },
    )
}
