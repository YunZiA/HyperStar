package com.yunzia.hyperstar.ui.component.preference.core

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.impl.PreferenceWithContentImpl

@Composable
fun PreferenceGroupScope.PreferenceWithContent(
    title: String,
    summary: String? = null,
    @DrawableRes icon: Int? = null,
    enabled: Boolean = true,
    content: @Composable () -> Unit = {},
) = PreferenceWithContentImpl(
    title = title,
    summary = summary,
    icon = icon,
    enabled = enabled,
    content = content,
)
