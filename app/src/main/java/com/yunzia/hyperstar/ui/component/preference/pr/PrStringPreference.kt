package com.yunzia.hyperstar.ui.component.preference.pr

import androidx.compose.runtime.Composable
import com.yunzia.hyperstar.prefs.PreferencesUtil
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.rememberPreferenceValue
import com.yunzia.hyperstar.ui.component.preference.impl.StringPreferenceImpl

@Composable
fun PreferenceGroupScope.PrStringPreference(
    key: String,
    title: String,
    summary: String? = null,
) {
    val valueState = rememberPreferenceValue(PreferencesUtil.getString(key, "null"))
    StringPreferenceImpl(
        title = title,
        summary = summary,
        value = valueState.value,
        onValueChange = {
            valueState.value = it
            PreferencesUtil.putString(key, it)
        },
    )
}
