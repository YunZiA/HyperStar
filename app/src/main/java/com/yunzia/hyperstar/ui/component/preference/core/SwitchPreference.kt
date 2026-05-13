package com.yunzia.hyperstar.ui.component.preference.core

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.impl.SwitchPreferenceImpl

@Composable
fun PreferenceGroupScope.SwitchPreference(
    title: String,
    summary: String? = null,
    @DrawableRes icon: Int? = null,
    checked: Boolean = false,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit = {},
) = SwitchPreferenceImpl(
    title = title,
    summary = summary,
    icon = icon,
    checked = checked,
    enabled = enabled,
    onCheckedChange = onCheckedChange,
)
