package com.yunzia.hyperstar.ui.component.preference.sp

import Searchable
import androidx.compose.runtime.Composable
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.rememberPreferenceValue
import com.yunzia.hyperstar.ui.component.preference.searchablePreferenceModifier
import com.yunzia.hyperstar.prefs.SPUtils
import com.yunzia.hyperstar.ui.component.preference.impl.SliderPreferenceImpl

@Searchable
@Composable
fun PreferenceGroupScope.SpSliderPreference(
    key: String,
    title: String,
    summary: String? = null,
    icon: Int? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    steps: Int = 0,
    enabled: Boolean = true,
    defaultValue: Float = 0f,
    decimalPlaces: Int = 0,
    unit: String = "",
    valueFormatter: (Float) -> String = if (decimalPlaces == 0) { { it.toInt().toString() } } else { { String.format("%.${decimalPlaces}f", it) } },
    visible: () -> Boolean = { true },
) {
    if (!visible()) return
    val valueState = rememberPreferenceValue(SPUtils.getFloat(key, defaultValue))
    SliderPreferenceImpl(
        modifier = searchablePreferenceModifier(key),
        title = title,
        summary = summary,
        icon = icon,
        value = valueState.value,
        valueRange = valueRange,
        steps = steps,
        enabled = enabled,
        decimalPlaces = decimalPlaces,
        defaultValue = defaultValue,
        unit = unit,
        onValueChange = {
            valueState.value = it
            SPUtils.putFloat(key, it)
        },
        valueFormatter = valueFormatter,
    )
}
