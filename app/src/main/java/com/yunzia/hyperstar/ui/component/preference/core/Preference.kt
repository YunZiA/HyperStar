package com.yunzia.hyperstar.ui.component.preference.core

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.impl.PreferenceImpl

@Composable
fun PreferenceGroupScope.Preference(
    title: String,
    summary: String? = null,
    @DrawableRes icon: Int? = null,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) = PreferenceImpl(
    title = title,
    summary = summary,
    icon = icon,
    enabled = enabled,
    onClick = onClick,
)
