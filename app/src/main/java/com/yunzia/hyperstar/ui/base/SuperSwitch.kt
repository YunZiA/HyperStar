package com.yunzia.hyperstar.ui.base

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.yunzia.hyperstar.ui.base.modifier.bounceAnim
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.Switch
import top.yukonga.miuix.kmp.basic.SwitchColors
import top.yukonga.miuix.kmp.basic.SwitchDefaults

@Composable
fun SuperSwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    switchColors: SwitchColors = SwitchDefaults.switchColors(),
    leftAction: @Composable (() -> Unit)? = null,
    rightActions: @Composable RowScope.() -> Unit = {},
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    enabled: Boolean = true
) {
    var isChecked by remember { mutableStateOf(checked) }
    val updatedOnCheckedChange by rememberUpdatedState(onCheckedChange)
    val localHapticFeedback = LocalHapticFeedback.current

    if (isChecked != checked) isChecked = checked

    BasicComponent(
        modifier = modifier.bounceAnim(),
        insideMargin = insideMargin,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        leftAction = leftAction,
        rightActions = {
            rightActions()
            Switch(
                checked = isChecked,
                onCheckedChange = updatedOnCheckedChange,
                enabled = enabled,
                colors = switchColors
            )
        },
        onClick = {
            if (enabled) {
                isChecked = !isChecked
                updatedOnCheckedChange?.invoke(isChecked)
                localHapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        },
        enabled = enabled
    )
}