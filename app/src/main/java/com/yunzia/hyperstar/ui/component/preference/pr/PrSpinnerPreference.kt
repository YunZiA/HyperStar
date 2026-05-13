package com.yunzia.hyperstar.ui.component.preference.pr

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.prefs.PreferencesUtil
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.rememberPreferenceValue
import com.yunzia.hyperstar.ui.component.preference.impl.SpinnerPreferenceImpl

@Composable
fun PreferenceGroupScope.PrSpinnerPreference(
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
    SpinnerPreferenceImpl(
        title = title,
        summary = summary,
        entries = entries,
        selectedIndex = valueState.value,
        dialogButtonString = stringResource(R.string.cancel),
        enabled = enabled,
        onSelectedIndexChange = {
            valueState.value = it
            PreferencesUtil.putInt(key, it)
        },
    )
}
