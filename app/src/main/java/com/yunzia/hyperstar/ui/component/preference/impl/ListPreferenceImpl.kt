package com.yunzia.hyperstar.ui.component.preference.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
internal fun ListPreferenceImpl(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    @androidx.annotation.DrawableRes icon: Int? = null,
    entries: List<String>,
    entryValues: List<String>,
    value: String = "",
    enabled: Boolean = true,
    onValueChange: (String) -> Unit = {},
) {
    val selectedIndex = entryValues.indexOf(value).let { if (it < 0) 0 else it }
    val displayValue = if (selectedIndex in entries.indices) entries[selectedIndex] else ""

    val showDialog = remember { mutableStateOf(false) }

    PreferenceWithValueImpl(
        modifier = modifier,
        title = title,
        summary = summary,
        icon = icon,
        value = displayValue,
        enabled = enabled,
        onClick = {
            showDialog.value = true
        }
    )

    if (showDialog.value) {
        ListPreferenceDialog(
            title = title,
            entries = entries,
            selectedIndex = selectedIndex,
            showDialog = showDialog,
            onItemSelected = { index ->
                if (index in entryValues.indices) {
                    onValueChange(entryValues[index])
                }
                showDialog.value = false
            }
        )
    }
}
