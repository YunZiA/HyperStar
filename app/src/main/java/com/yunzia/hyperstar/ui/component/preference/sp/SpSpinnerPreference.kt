package com.yunzia.hyperstar.ui.component.preference.sp

import Searchable
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.prefs.SPUtils
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.rememberPreferenceValue
import com.yunzia.hyperstar.ui.component.preference.searchablePreferenceModifier
import com.yunzia.hyperstar.ui.component.preference.impl.SpinnerPreferenceImpl

@Searchable
@Composable
fun PreferenceGroupScope.SpSpinnerPreference(
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
    SpinnerPreferenceImpl(
        modifier = searchablePreferenceModifier(key),
        title = title,
        summary = summary,
        entries = entries,
        selectedIndex = valueState.value,
        dialogButtonString = stringResource(R.string.cancel),
        enabled = enabled,
        onSelectedIndexChange = {
            valueState.value = it
            SPUtils.putInt(key, it)
        },
    )
}
