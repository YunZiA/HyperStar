package com.yunzia.hyperstar.ui.component.preference.sp

import androidx.compose.runtime.Composable
import Searchable
import com.yunzia.hyperstar.prefs.SPUtils
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.rememberPreferenceValue
import com.yunzia.hyperstar.ui.component.preference.searchablePreferenceModifier
import com.yunzia.hyperstar.ui.component.preference.impl.StringPreferenceImpl

@Searchable
@Composable
fun PreferenceGroupScope.SpStringPreference(
    key: String,
    title: String,
    summary: String? = null,
    visible: () -> Boolean = { true },
) {
    if (!visible()) return
    val valueState = rememberPreferenceValue(SPUtils.getString(key, "null"))
    StringPreferenceImpl(
        modifier = searchablePreferenceModifier(key),
        title = title,
        summary = summary,
        value = valueState.value,
        onValueChange = {
            valueState.value = it
            SPUtils.putString(key, it)
        },
    )
}
