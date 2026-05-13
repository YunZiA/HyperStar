package com.yunzia.hyperstar.ui.component.preference.core

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.impl.NavPreferenceImpl

@Composable
fun PreferenceGroupScope.NavPreference(
    title: String,
    summary: String? = null,
    @DrawableRes icon: Int? = null,
    endText: String? = null,
    onClick: () -> Unit,
) = NavPreferenceImpl(
    title = title,
    summary = summary,
    icon = icon,
    endText = endText,
    onClick = onClick,
)
