package com.yunzia.hyperstar.ui.component.preference.core

import Searchable
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.searchablePreferenceModifier
import com.yunzia.hyperstar.ui.component.preference.impl.NavPreferenceImpl

@Searchable
@Composable
fun PreferenceGroupScope.SearchableNavPreference(
    key: String,
    title: String,
    summary: String? = null,
    @DrawableRes icon: Int? = null,
    endText: String? = null,
    visible: () -> Boolean = { true },
    onClick: () -> Unit,
) {
    if (!visible()) return
    NavPreferenceImpl(
        modifier = searchablePreferenceModifier(key),
        title = title,
        summary = summary,
        icon = icon,
        endText = endText,
        onClick = onClick,
    )
}
