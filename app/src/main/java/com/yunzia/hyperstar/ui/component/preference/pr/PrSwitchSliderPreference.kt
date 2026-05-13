package com.yunzia.hyperstar.ui.component.preference.pr

import androidx.compose.runtime.Composable
import com.yunzia.hyperstar.prefs.PreferencesUtil
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.rememberPreferenceValue
import com.yunzia.hyperstar.ui.component.preference.impl.SwitchSliderPreferenceImpl

@Composable
fun PreferenceGroupScope.PrSwitchSliderPreference(
    switchKey: String,
    switchTitle: String,
    switchSummary: String? = null,
    key: String,
    title: String,
    minValue: Float = 0f,
    maxValue: Float = 1f,
    defaultValue: Float = 0.5f,
    decimalPlaces: Int = 0,
) {
    val checkedState = rememberPreferenceValue(PreferencesUtil.getBoolean(switchKey, false))
    val valueState = rememberPreferenceValue(PreferencesUtil.getFloat(key, defaultValue))

    SwitchSliderPreferenceImpl(
        switchTitle = switchTitle,
        switchSummary = switchSummary,
        switchChecked = checkedState.value,
        onSwitchCheckedChange = {
            checkedState.value = it
            PreferencesUtil.putBoolean(switchKey, it)
        },
        sliderTitle = title,
        sliderValue = valueState.value,
        sliderValueRange = minValue..maxValue,
        decimalPlaces = decimalPlaces,
        defaultValue = defaultValue,
        onSliderValueChange = {
            valueState.value = it
            PreferencesUtil.putFloat(key, it)
        },
    )
}
