package com.yunzia.hyperstar.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.dialog.SuperDialogs
import com.yunzia.hyperstar.ui.component.modifier.bounceAnim
import com.yunzia.hyperstar.utils.SPUtils
import top.yukonga.miuix.kmp.extra.SuperArrow

@Composable
fun SuperStringArrow(
    title : String,
    summary : String ? = null,
    key:String
) {
    val mString = remember { mutableStateOf(SPUtils.getString(key,"null")) }

    val show = remember { mutableStateOf(false) }

    SuperArrow(
        modifier = Modifier.bounceAnim(),
        title = title,
        summary = summary,
        rightText = if (mString.value == "null") stringResource(R.string.default_value) else mString.value,
        onClick = {
            show.value = true
        }
    )

    StringDialog(title,key,mString,show)


}

@Composable
private fun StringDialog(
    title: String,
    key: String,
    values: MutableState<String>,
    showDialog: MutableState<Boolean>
){

    val kc = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var hasFocus by remember { mutableStateOf(false) }

    val newString = StringBuffer()
    val defValues = TextFieldValue(newString.toString(), TextRange(0))
    val isChange = remember { mutableStateOf(false) }
    val fText = if (values.value == "null") "" else values.value
    val text = remember {
        mutableStateOf(TextFieldValue(text = fText, selection = TextRange(fText.length)))
    }

    SuperDialogs(
        title = title,
        show = showDialog,
        onFocus = {
            kc?.hide()
            focusManager.clearFocus()
        },
        onDismissRequest = {
            if (hasFocus){
                kc?.hide()
                focusManager.clearFocus()
                return@SuperDialogs
            }
            text.value = TextFieldValue(text = fText, selection = TextRange(fText.length))
            showDialog.value = false
        }
    ) {
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            kc?.show()
        }

        SuperTextField(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 20.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    hasFocus = it.hasFocus
                },
            //backgroundColor = colorScheme.surfaceVariant,
            label = stringResource(R.string.default_value),
            value = text.value,
            useLabelAsPlaceholder = true,
            keyboardOptions =  KeyboardOptions(imeAction = ImeAction.Done,keyboardType = KeyboardType.Text),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            onValueChange = {
                if (!isChange.value) isChange.value = true
                text.value = it
            }
        )
        Column (
            verticalArrangement = Arrangement.Bottom,
        ) {
            BaseButton(
                text = stringResource(R.string.cancel),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    focusManager.clearFocus()
                    showDialog.value = false
                    isChange.value = false
                    text.value = TextFieldValue(text = fText, selection = TextRange(fText.length))

                }

            )
            Spacer(Modifier.height(10.dp))
            BaseButton(
                text = stringResource(R.string.recovery_default),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    focusManager.clearFocus()
                    values.value = "null"
                    isChange.value = false
                    text.value = TextFieldValue(text = "", selection = TextRange(0))
                    SPUtils.setString(key, "null")
                    showDialog.value = false
                    //showDialog.value = false

                }

            )
            Spacer(Modifier.height(10.dp))
            BaseButton(
                text = stringResource(R.string.sure),
                modifier = Modifier.fillMaxWidth(),
                submit = true,
                onClick = {
                    focusManager.clearFocus()
                    if (text.value.text == ""){
                        values.value = "null"
                        isChange.value = false
                        text.value = TextFieldValue(text = "", selection = TextRange(0))

                    }else{
                        values.value = text.value.text

                    }
                    SPUtils.setString(key, values.value)
                    showDialog.value = false

                }

            )

        }
    }



}