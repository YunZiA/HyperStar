package com.yunzia.hyperstar.ui.base

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Switch
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun SuperSwitch(
    title: String,
    titleColor: Color = MiuixTheme.colorScheme.onBackground,
    summary: String? = null,
    leftAction: @Composable (() -> Unit)? = null,
    rightActions: @Composable RowScope.() -> Unit = {},
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    insideMargin: DpSize = DpSize(16.dp, 16.dp),
    enabled: Boolean = true
) {
    var isChecked by remember { mutableStateOf(checked) }
    val updatedOnCheckedChange by rememberUpdatedState(onCheckedChange)

    if (isChecked != checked) {
        isChecked = checked
    }

    BasicComponent(
        modifier = modifier,
        insideMargin = insideMargin,
        title = title,
        titleColor = titleColor,
        summary = summary,
        leftAction = leftAction,
        rightActions = {
            rightActions()
            Switch(
                checked = isChecked,
                onCheckedChange = updatedOnCheckedChange,
                enabled = enabled
            )
        },
        onClick = {
            if (enabled) {
                isChecked = !isChecked
                updatedOnCheckedChange?.invoke(isChecked)
            }
        }
    )
}