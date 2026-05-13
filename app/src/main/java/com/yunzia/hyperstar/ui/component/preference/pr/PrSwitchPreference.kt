package com.yunzia.hyperstar.ui.component.preference.pr

import androidx.compose.runtime.Composable
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.rememberPreferenceValue
import com.yunzia.hyperstar.prefs.PreferencesUtil
import com.yunzia.hyperstar.ui.component.preference.impl.SwitchPreferenceImpl

@Composable
fun PreferenceGroupScope.PrSwitchPreference(
    key: String,
    title: String,
    summary: String? = null,
    icon: Int? = null,
    enabled: Boolean = true,
    defaultValue: Boolean = false,
) {
    val checkedState = rememberPreferenceValue(PreferencesUtil.getBoolean(key, defaultValue))
    SwitchPreferenceImpl(
        title = title,
        summary = summary,
        icon = icon,
        checked = checkedState.value,
        enabled = enabled,
        onCheckedChange = {
            checkedState.value = it
            PreferencesUtil.putBoolean(key, it)
        },
    )
}
