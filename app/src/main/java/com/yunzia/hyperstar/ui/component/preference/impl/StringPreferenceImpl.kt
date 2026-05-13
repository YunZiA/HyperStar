package com.yunzia.hyperstar.ui.component.preference.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.BaseButton
import com.yunzia.hyperstar.ui.component.modifier.bounceAnim
import com.yunzia.hyperstar.ui.component.dialog.OverlayDialog
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
internal fun StringPreferenceImpl(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    value: String,
    onValueChange: (String) -> Unit,
) {
    val show = remember { mutableStateOf(false) }

    ArrowPreference(
        modifier = modifier.bounceAnim(),
        title = title,
        summary = summary,
        endActions = {
            Text(
                text = if (value == "null") stringResource(R.string.default_value) else value,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f, fill = false),
                fontSize = MiuixTheme.textStyles.body2.fontSize,
                color = colorScheme.onSurfaceVariantActions,
                textAlign = TextAlign.End,
            )
        },
        onClick = { show.value = true }
    )

    if (show.value) {
        StringDialog(
            title = title,
            currentValue = value,
            showDialog = show,
            onValueChange = onValueChange,
        )
    }
}

@Composable
private fun StringDialog(
    title: String,
    currentValue: String,
    showDialog: MutableState<Boolean>,
    onValueChange: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    val fText = if (currentValue == "null") "" else currentValue
    val textState = remember(currentValue) {
        mutableStateOf(TextFieldValue(text = fText, selection = TextRange(fText.length)))
    }

    fun clear() {
        focusManager.clearFocus(force = true)
        focusRequester.freeFocus()
        keyboardController?.hide()
    }

    OverlayDialog(
        show = showDialog,
        title = title,
        onDismissRequest = {
            textState.value = TextFieldValue(text = fText, selection = TextRange(fText.length))
            showDialog.value = false
        }
    ) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        TextField(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 20.dp)
                .focusRequester(focusRequester),
            label = stringResource(R.string.default_value),
            value = textState.value,
            useLabelAsPlaceholder = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text),
            keyboardActions = KeyboardActions(onDone = { clear() }),
            onValueChange = { textState.value = it }
        )

        Column(verticalArrangement = Arrangement.Bottom) {
            BaseButton(
                text = stringResource(R.string.cancel),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    showDialog.value = false
                    textState.value = TextFieldValue(text = fText, selection = TextRange(fText.length))
                }
            )
            Spacer(Modifier.height(10.dp))
            BaseButton(
                text = stringResource(R.string.recovery_default),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onValueChange("null")
                    textState.value = TextFieldValue(text = "", selection = TextRange(0))
                    showDialog.value = false
                }
            )
            Spacer(Modifier.height(10.dp))
            BaseButton(
                text = stringResource(R.string.sure),
                modifier = Modifier.fillMaxWidth(),
                submit = true,
                onClick = {
                    val newValue = if (textState.value.text == "") "null" else textState.value.text
                    onValueChange(newValue)
                    showDialog.value = false
                }
            )
        }
    }
}
