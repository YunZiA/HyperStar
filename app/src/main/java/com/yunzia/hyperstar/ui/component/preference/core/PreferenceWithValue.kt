package com.yunzia.hyperstar.ui.component.preference.core

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.impl.PreferenceWithValueImpl

@Composable
fun PreferenceGroupScope.PreferenceWithValue(
    title: String,
    summary: String? = null,
    @DrawableRes icon: Int? = null,
    value: String = "",
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) = PreferenceWithValueImpl(
    title = title,
    summary = summary,
    icon = icon,
    value = value,
    enabled = enabled,
    onClick = onClick,
)
