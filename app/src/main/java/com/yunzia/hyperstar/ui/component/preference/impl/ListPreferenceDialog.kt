package com.yunzia.hyperstar.ui.component.preference.impl

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.layout.DialogDefaults
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
internal fun ListPreferenceDialog(
    title: String,
    entries: List<String>,
    selectedIndex: Int,
    showDialog: MutableState<Boolean>,
    onItemSelected: (Int) -> Unit,
) {
    var currentSelectedIndex by remember(selectedIndex) { mutableStateOf(selectedIndex) }

    OverlayDialog(
        title = title,
        show = showDialog.value,
        insideMargin = DpSize(0.dp, 24.dp),
        onDismissRequest = {
            showDialog.value = false
        },
        onDismissFinished = {
            showDialog.value = false
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
//                .padding(vertical = 8.dp)
        ) {
            entries.forEachIndexed { index, entry ->
                ListPreferenceItem(
                    text = entry,
                    isSelected = currentSelectedIndex == index,
                    onClick = {
                        currentSelectedIndex = index
                        onItemSelected(index)
                    }
                )
            }
        }
    }
}

@Composable
private fun ListPreferenceItem(
    text: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
            },
        color = if (isSelected) colorScheme.primaryContainer else colorScheme.surface
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(vertical = 8.dp),
            fontSize = 14.sp,
            color = if (isSelected) colorScheme.onPrimaryContainer else colorScheme.onSurface,
        )
    }
}
