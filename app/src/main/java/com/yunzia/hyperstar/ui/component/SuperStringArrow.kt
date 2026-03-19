package com.yunzia.hyperstar.ui.component

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
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
import com.yunzia.hyperstar.ui.component.modifier.bounceAnim
import com.yunzia.hyperstar.prefs.SPUtils
import top.yukonga.miuix.kmp.basic.InputField
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

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
        endActions = {
            Text(
                text = if (mString.value == "null") stringResource(R.string.default_value) else mString.value,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f, fill = false),
                fontSize = MiuixTheme.textStyles.body2.fontSize,
                color = colorScheme.onSurfaceVariantActions,
                textAlign = TextAlign.End,
            ) },
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

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var hasFocus by remember { mutableStateOf(false) }

//    val interactionSource = remember { MutableInteractionSource() }
    val newString = StringBuffer()
    val defValues = TextFieldValue(newString.toString(), TextRange(0))
    val isChange = remember { mutableStateOf(false) }
    val fText = if (values.value == "null") "" else values.value
    val text = remember {
        mutableStateOf(TextFieldValue(text = fText, selection = TextRange(fText.length)))
    }
    fun clear(){
        focusManager.clearFocus(force = true)
        focusRequester.freeFocus()
        keyboardController?.hide()
    }

    SuperDialog(
        title = title,
        show = showDialog,
        modifier = Modifier,
//            .clickable {
//            clear()
//        }.pointerInput(Unit) {
//            detectTapGestures {
//                clear()
//            }
//        }
        onDismissFinished = {
            text.value = TextFieldValue(text = fText, selection = TextRange(fText.length))
            showDialog.value = false
//            if (hasFocus) {
//                clear()
//            } else{
//            }

        },
        onDismissRequest = {
        }
    ) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
        TextField(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 20.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    hasFocus = it.hasFocus
                }
            ,
            //backgroundColor = colorScheme.surfaceVariant,
            label = stringResource(R.string.default_value),
            value = text.value,
            useLabelAsPlaceholder = true,
//            interactionSource = interactionSource,
            keyboardOptions =  KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text),
            keyboardActions = KeyboardActions(onDone = {
                clear()
            }),
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
                    values.value = "null"
                    isChange.value = false
                    text.value = TextFieldValue(text = "", selection = TextRange(0))
                    SPUtils.putString(key, "null")
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
                    if (text.value.text == ""){
                        values.value = "null"
                        isChange.value = false
                        text.value = TextFieldValue(text = "", selection = TextRange(0))
                    }else{
                        values.value = text.value.text
                    }
                    SPUtils.putString(key, values.value)
                    showDialog.value = false

                }

            )

        }
    }



}