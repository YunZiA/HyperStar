package com.yunzia.hyperstar.ui.component.preference.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yunzia.hyperstar.ui.component.modifier.bounceAnim
import top.yukonga.miuix.kmp.preference.WindowDropdownPreference

@Composable
internal fun DropdownPreferenceImpl(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    entries: List<String>,
    selectedIndex: Int,
    enabled: Boolean = true,
    onSelectedIndexChange: (Int) -> Unit,
) {
    WindowDropdownPreference(
        modifier = modifier.bounceAnim(),
        title = title,
        summary = summary,
        items = entries,
        selectedIndex = selectedIndex,
        enabled = enabled,
        onSelectedIndexChange = onSelectedIndexChange
    )
}
