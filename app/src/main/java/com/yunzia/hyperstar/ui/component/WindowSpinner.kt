package com.yunzia.hyperstar.ui.component

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.prefs.SPUtils
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.extra.SpinnerColors
import top.yukonga.miuix.kmp.extra.SpinnerDefaults
import top.yukonga.miuix.kmp.extra.SpinnerEntry
import top.yukonga.miuix.kmp.extra.WindowSpinner

@Composable
fun WindowSpinner(
    title: String,
    items: Array<String>,
    key: String,
    defIndex:Int,
    dialogButtonString: String = stringResource(R.string.cancel),
    modifier: Modifier = Modifier,
    popupModifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    spinnerColors: SpinnerColors = SpinnerDefaults.spinnerColors(),
    startAction: @Composable (() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    enabled: Boolean = true,
    showValue: Boolean = true,
    onSelectedIndexChange: ((Int) -> Unit)?,
) {

    val spinnerItems = mutableListOf<SpinnerEntry>()
    val selected = remember { mutableIntStateOf(SPUtils.getInt(key,defIndex))}

    for (item in items){
        spinnerItems.add(SpinnerEntry(title = item))
    }

    WindowSpinner(
        items = spinnerItems,
        selectedIndex = selected.intValue,
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
        showValue = showValue
    ) {
        selected.intValue = it
        Log.d("ggc", "$key: ${selected.intValue}")
        SPUtils.putInt(key,selected.intValue)
        onSelectedIndexChange?.invoke(it)
    }
}
