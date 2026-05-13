package com.yunzia.hyperstar.ui.component.preference.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
internal fun SwitchSliderPreferenceImpl(
    modifier: Modifier = Modifier,
    switchTitle: String,
    switchSummary: String? = null,
    switchChecked: Boolean,
    onSwitchCheckedChange: (Boolean) -> Unit,
    sliderTitle: String,
    sliderValue: Float,
    sliderValueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    decimalPlaces: Int = 0,
    defaultValue: Float = sliderValueRange.start,
    unit: String = "",
    onSliderValueChange: (Float) -> Unit,
) {
    var switchState by remember(switchChecked) { mutableStateOf(switchChecked) }
    var sliderState by remember(sliderValue) { mutableStateOf(sliderValue) }

    val valueFormatter: (Float) -> String = { v ->
        if (decimalPlaces == 0) v.toInt().toString()
        else String.format("%.${decimalPlaces}f", v)
    }

    SwitchPreferenceImpl(
        modifier = modifier,
        title = switchTitle,
        summary = switchSummary,
        checked = switchState,
        onCheckedChange = {
            switchState = it
            onSwitchCheckedChange(it)
        },
    )

    AnimatedVisibility(
        switchState,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column {
            SliderPreferenceImpl(
                title = sliderTitle,
                value = sliderState,
                valueRange = sliderValueRange,
                decimalPlaces = decimalPlaces,
                defaultValue = defaultValue,
                unit = unit,
                onValueChange = {
                    sliderState = it
                    onSliderValueChange(it)
                },
                valueFormatter = valueFormatter,
            )
        }
    }
}
