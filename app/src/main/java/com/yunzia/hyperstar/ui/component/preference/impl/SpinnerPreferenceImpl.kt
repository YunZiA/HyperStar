package com.yunzia.hyperstar.ui.component.preference.impl

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.SpinnerColors
import top.yukonga.miuix.kmp.basic.SpinnerDefaults
import top.yukonga.miuix.kmp.basic.SpinnerEntry
import top.yukonga.miuix.kmp.preference.WindowSpinnerPreference

@Composable
internal fun SpinnerPreferenceImpl(
    modifier: Modifier = Modifier,
    popupModifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    entries: List<String>,
    selectedIndex: Int,
    dialogButtonString: String,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    spinnerColors: SpinnerColors = SpinnerDefaults.dialogSpinnerColors(),
    startAction: @Composable (() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    enabled: Boolean = true,
    showValue: Boolean = true,
    onSelectedIndexChange: (Int) -> Unit,
) {
    val spinnerItems = entries.map { SpinnerEntry(title = it) }
    WindowSpinnerPreference(
        items = spinnerItems,
        selectedIndex = selectedIndex,
        title = title,
        dialogButtonString = dialogButtonString,
        modifier = modifier,
        popupModifier = popupModifier,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        spinnerColors = spinnerColors,
        enabled = enabled,
        startAction = startAction,
        bottomAction = bottomAction,
        insideMargin = insideMargin,
        showValue = showValue,
    ) {
        onSelectedIndexChange(it)
    }
}
