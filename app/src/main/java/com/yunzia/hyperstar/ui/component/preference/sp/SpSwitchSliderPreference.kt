package com.yunzia.hyperstar.ui.component.preference.sp

import androidx.compose.runtime.Composable
import Searchable
import com.yunzia.hyperstar.prefs.SPUtils
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.rememberPreferenceValue
import com.yunzia.hyperstar.ui.component.preference.searchablePreferenceModifier
import com.yunzia.hyperstar.ui.component.preference.impl.SwitchSliderPreferenceImpl

@Searchable
@Composable
fun PreferenceGroupScope.SpSwitchSliderPreference(
    switchKey: String,
    switchTitle: String,
    switchSummary: String? = null,
    key: String,
    title: String,
    minValue: Float = 0f,
    maxValue: Float = 1f,
    defaultValue: Float = 0.5f,
    decimalPlaces: Int = 0,
    unit: String = "",
    visible: () -> Boolean = { true },
) {
    if (!visible()) return
    val checkedState = rememberPreferenceValue(SPUtils.getBoolean(switchKey, false))
    val valueState = rememberPreferenceValue(SPUtils.getFloat(key, defaultValue))
    SwitchSliderPreferenceImpl(
        modifier = searchablePreferenceModifier(key),
        switchTitle = switchTitle,
        switchSummary = switchSummary,
        switchChecked = checkedState.value,
        onSwitchCheckedChange = {
            checkedState.value = it
            SPUtils.putBoolean(switchKey, it)
        },
        sliderTitle = title,
        sliderValue = valueState.value,
        sliderValueRange = minValue..maxValue,
        decimalPlaces = decimalPlaces,
        unit = unit,
        defaultValue = defaultValue,
        onSliderValueChange = {
            valueState.value = it
            SPUtils.putFloat(key, it)
        },
    )
}
