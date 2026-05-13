package com.yunzia.hyperstar.ui.component.preference.core

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.impl.SliderPreferenceImpl

@Composable
fun PreferenceGroupScope.SliderPreference(
    title: String,
    summary: String? = null,
    @DrawableRes icon: Int? = null,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    steps: Int = 0,
    enabled: Boolean = true,
    decimalPlaces: Int = 0,
    defaultValue: Float = valueRange.start,
    unit: String = "",
    onValueChange: (Float) -> Unit = {},
    valueFormatter: (Float) -> String = if (decimalPlaces == 0) { { it.toInt().toString() } } else { { String.format("%.${decimalPlaces}f", it) } },
) = SliderPreferenceImpl(
    title = title,
    summary = summary,
    icon = icon,
    value = value,
    valueRange = valueRange,
    steps = steps,
    enabled = enabled,
    decimalPlaces = decimalPlaces,
    defaultValue = defaultValue,
    unit = unit,
    onValueChange = onValueChange,
    valueFormatter = valueFormatter,
)
