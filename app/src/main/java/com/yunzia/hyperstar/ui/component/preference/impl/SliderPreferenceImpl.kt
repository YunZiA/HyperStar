package com.yunzia.hyperstar.ui.component.preference.impl

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.BaseButton
import com.yunzia.hyperstar.ui.component.SuperTextField
import com.yunzia.hyperstar.ui.component.dialog.OverlayDialog
import com.yunzia.hyperstar.ui.component.tool.FilterFloat
import kotlin.math.pow
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

private fun calcSteps(
    range: ClosedFloatingPointRange<Float>,
    decimals: Int
): Int {
    val factor = 10.0.pow(decimals)
    val points = ((range.endInclusive - range.start) * factor).toInt()
    return (points - 1).coerceAtLeast(0)
}

@Composable
internal fun SliderPreferenceImpl(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    @androidx.annotation.DrawableRes icon: Int? = null,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    steps: Int = 0,
    enabled: Boolean = true,
    decimalPlaces: Int = 0,
    defaultValue: Float = valueRange.start,
    unit: String = "",
    onValueChange: (Float) -> Unit = {},
    valueFormatter: (Float) -> String = if (decimalPlaces == 0) { { it.toInt().toString() } } else { { String.format("%.${decimalPlaces}f", it) } },
) {
    val resolvedSteps = if (steps > 0) steps else if (decimalPlaces > 0) calcSteps(valueRange, decimalPlaces) else 0
    val showDialog = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.5f)
            .clickable(enabled = enabled) { showDialog.value = true }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (summary != null) {
                    Text(
                        text = summary,
                        fontSize = 13.sp,
                        color = colorScheme.onSurfaceVariantSummary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = valueFormatter(value) + unit,
                fontSize = 14.sp,
                color = colorScheme.onSurfaceVariantActions
            )
        }

        Slider(
            value = value,
            onValueChange = { if (enabled) onValueChange(it) },
            valueRange = valueRange,
            steps = resolvedSteps,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            enabled = enabled
        )
    }

    SliderValueDialog(
        title = title,
        value = value,
        valueRange = valueRange,
        defaultValue = defaultValue,
        unit = unit,
        decimalPlaces = decimalPlaces,
        showDialog = showDialog,
        onValueChange = onValueChange
    )
}

@Composable
private fun SliderValueDialog(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    defaultValue: Float,
    unit: String,
    decimalPlaces: Int,
    showDialog: androidx.compose.runtime.MutableState<Boolean>,
    onValueChange: (Float) -> Unit,
) {
    val kc = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val filter = remember(value) { FilterFloat(value, valueRange.start, valueRange.endInclusive, decimalPlaces) }
    var hasFocus by remember { mutableStateOf(false) }
    val isDefault = remember(value) { mutableStateOf(value == defaultValue) }
    val emptyField = TextFieldValue("", TextRange(0))

    if (showDialog.value) {
        if (isDefault.value && filter.getInputValue().text != String.format("%.${decimalPlaces}f", defaultValue)) {
            isDefault.value = false
        }
    } else {
        isDefault.value = value == defaultValue
    }

    OverlayDialog(
        show = showDialog,
        title = title,
        onDismissRequest = {
            if (hasFocus) {
                kc?.hide()
                focusManager.clearFocus()
                return@OverlayDialog
            }
            filter.setInputValue(String.format("%.${decimalPlaces}f", value))
            showDialog.value = false
        }
    ) {
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            kc?.show()
        }

        Text(
            stringResource(R.string.range_des, String.format("%.${decimalPlaces}f", valueRange.start), unit, String.format("%.${decimalPlaces}f", valueRange.endInclusive), unit),
            Modifier
                .fillMaxWidth()
                .padding(start = 5.dp, top = 8.dp, bottom = 10.dp),
            color = colorScheme.primary,
            textAlign = TextAlign.Start,
            fontSize = 13.sp
        )
        SuperTextField(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { hasFocus = it.hasFocus },
            label = stringResource(R.string.default_value),
            value = if (isDefault.value) emptyField else filter.getInputValue(),
            useLabelAsPlaceholder = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            trailingIcon = {
                Text(
                    text = unit,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            },
            onValueChange = filter.onValueChange()
        )
        Column(verticalArrangement = Arrangement.Bottom) {
            BaseButton(
                text = stringResource(R.string.cancel),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    focusManager.clearFocus()
                    showDialog.value = false
                    if (filter.getInputValue().text == "") {
                        filter.setInputValue(String.format("%.${decimalPlaces}f", defaultValue))
                    } else {
                        filter.setInputValue(String.format("%.${decimalPlaces}f", value))
                    }
                }
            )
            Spacer(Modifier.height(10.dp))
            BaseButton(
                text = stringResource(R.string.recovery_default),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    focusManager.clearFocus()
                    filter.setInputValue(String.format("%.${decimalPlaces}f", defaultValue))
                    onValueChange(defaultValue)
                    showDialog.value = false
                }
            )
            Spacer(Modifier.height(10.dp))
            BaseButton(
                text = stringResource(R.string.sure),
                modifier = Modifier.fillMaxWidth(),
                submit = true,
                onClick = {
                    focusManager.clearFocus()
                    val inputText = filter.getInputValue().text
                    if (inputText == "") {
                        filter.setInputValue(String.format("%.${decimalPlaces}f", defaultValue))
                        onValueChange(defaultValue)
                    } else {
                        onValueChange(inputText.toFloat())
                    }
                    showDialog.value = false
                }
            )
        }
    }
}