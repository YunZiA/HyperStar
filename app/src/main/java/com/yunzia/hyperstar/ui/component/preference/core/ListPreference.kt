package com.yunzia.hyperstar.ui.component.preference.core

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.impl.ListPreferenceImpl

@Composable
fun PreferenceGroupScope.ListPreference(
    title: String,
    summary: String? = null,
    @DrawableRes icon: Int? = null,
    entries: List<String>,
    entryValues: List<String>,
    value: String = "",
    enabled: Boolean = true,
    onValueChange: (String) -> Unit = {},
) = ListPreferenceImpl(
    title = title,
    summary = summary,
    icon = icon,
    entries = entries,
    entryValues = entryValues,
    value = value,
    enabled = enabled,
    onValueChange = onValueChange,
)
