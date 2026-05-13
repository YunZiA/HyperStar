package com.yunzia.hyperstar.ui.component.preference.widget

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.preference.impl.ColorPickerPreferenceImpl
import com.yunzia.hyperstar.ui.component.preference.impl.ContentDropdownPreferenceImpl
import com.yunzia.hyperstar.ui.component.preference.impl.DropdownPreferenceImpl
import com.yunzia.hyperstar.ui.component.preference.impl.ListPreferenceImpl
import com.yunzia.hyperstar.ui.component.preference.impl.SpinnerPreferenceImpl
import com.yunzia.hyperstar.ui.component.preference.impl.NavPreferenceImpl
import com.yunzia.hyperstar.ui.component.preference.impl.PreferenceImpl
import com.yunzia.hyperstar.ui.component.preference.impl.PreferenceWithContentImpl
import com.yunzia.hyperstar.ui.component.preference.impl.PreferenceWithValueImpl
import com.yunzia.hyperstar.ui.component.preference.impl.SliderPreferenceImpl
import com.yunzia.hyperstar.ui.component.preference.impl.StringPreferenceImpl
import com.yunzia.hyperstar.ui.component.preference.impl.SwitchFolderPreferenceImpl
import com.yunzia.hyperstar.ui.component.preference.impl.SwitchPreferenceImpl
import com.yunzia.hyperstar.ui.component.preference.impl.SwitchSliderPreferenceImpl

// =============================================================================
// Standalone @Composable items — no PreferenceScope/PreferenceGroupScope dependency
// =============================================================================

@Composable
fun SwitchPreference(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    @DrawableRes icon: Int? = null,
    checked: Boolean = false,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit = {},
) = SwitchPreferenceImpl(modifier, title, summary, icon, checked, enabled, onCheckedChange)

@Composable
fun ListPreference(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    @DrawableRes icon: Int? = null,
    entries: List<String>,
    entryValues: List<String>,
    value: String = "",
    enabled: Boolean = true,
    onValueChange: (String) -> Unit = {},
) = ListPreferenceImpl(modifier, title, summary, icon, entries, entryValues, value, enabled, onValueChange)

@Composable
fun SliderPreference(
    modifier: Modifier = Modifier,
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
) = SliderPreferenceImpl(modifier, title, summary, icon, value, valueRange, steps, enabled, decimalPlaces, defaultValue, unit, onValueChange, valueFormatter)

@Composable
fun Preference(
    title: String,
    summary: String? = null,
    @DrawableRes icon: Int? = null,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) = PreferenceImpl(title, summary, icon, enabled, onClick)

@Composable
fun PreferenceWithValue(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    @DrawableRes icon: Int? = null,
    value: String = "",
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) = PreferenceWithValueImpl(modifier, title, summary, icon, value, enabled, onClick)

@Composable
fun PreferenceWithContent(
    title: String,
    summary: String? = null,
    @DrawableRes icon: Int? = null,
    enabled: Boolean = true,
    content: @Composable () -> Unit = {},
) = PreferenceWithContentImpl(title, summary, icon, enabled, content)

@Composable
fun NavPreference(
    title: String,
    summary: String? = null,
    @DrawableRes icon: Int? = null,
    endText: String? = null,
    onClick: () -> Unit,
) = NavPreferenceImpl(Modifier, title, summary, icon, endText, onClick)

@Composable
fun ColorPickerPreference(
    modifier: Modifier = Modifier,
    title: String,
    dfColor: Color = Color.Transparent,
    key: String,
) = ColorPickerPreferenceImpl(modifier, title, dfColor, key)

@Composable
fun StringPreference(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    value: String,
    onValueChange: (String) -> Unit,
) = StringPreferenceImpl(modifier, title, summary, value, onValueChange)

@Composable
fun DropdownPreference(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    entries: List<String>,
    selectedIndex: Int,
    enabled: Boolean = true,
    onSelectedIndexChange: (Int) -> Unit,
) = DropdownPreferenceImpl(modifier, title, summary, entries, selectedIndex, enabled, onSelectedIndexChange)

@Composable
fun SpinnerPreference(
    modifier: Modifier = Modifier,
    popupModifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    entries: List<String>,
    selectedIndex: Int,
    dialogButtonString: String = stringResource(R.string.cancel),
    enabled: Boolean = true,
    showValue: Boolean = true,
    onSelectedIndexChange: (Int) -> Unit,
) = SpinnerPreferenceImpl(modifier, popupModifier, title, summary, entries, selectedIndex, dialogButtonString, enabled = enabled, showValue = showValue, onSelectedIndexChange = onSelectedIndexChange)

@Composable
fun ContentDropdownPreference(
    modifier: Modifier = Modifier,
    title: String,
    items: List<String>,
    summary: String? = null,
    selectedIndex: Int,
    showOption: Int,
    onSelectedIndexChange: (Int) -> Unit,
    content: @Composable () -> Unit,
) = ContentDropdownPreferenceImpl(modifier, title, items, summary, selectedIndex, showOption, onSelectedIndexChange, content)

@Composable
fun ContentDropdownPreferenceImpl(
    modifier: Modifier = Modifier,
    title: String,
    items: List<String>,
    summary: String? = null,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    content: @Composable (AnimatedContentScope.(Int) -> Unit),
) = ContentDropdownPreferenceImpl(modifier, title, items, summary, selectedIndex, onSelectedIndexChange, content)

@Composable
fun SwitchFolderPreference(
    modifier: Modifier = Modifier,
    title: String,
    checked: Boolean,
    contrary: Boolean = false,
    onCheckedChange: (Boolean) -> Unit,
    content: @Composable () -> Unit,
) = SwitchFolderPreferenceImpl(modifier, title, checked, contrary, onCheckedChange, content)

@Composable
fun SwitchSliderPreference(
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
) = SwitchSliderPreferenceImpl(modifier, switchTitle, switchSummary, switchChecked, onSwitchCheckedChange, sliderTitle, sliderValue, sliderValueRange, decimalPlaces, defaultValue, unit, onSliderValueChange)
