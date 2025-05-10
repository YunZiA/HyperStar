package yunzia.colorpicker

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults.buttonColors
import androidx.wear.compose.material.Icon
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.BaseButton
import com.yunzia.hyperstar.ui.component.MTextField
import com.yunzia.hyperstar.ui.component.dialog.SuperDialog
import com.yunzia.hyperstar.ui.component.tool.FilterColorHex
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape


@Composable
fun ColorPickerDialog(
    title:String,
    fColor: Color,
    showDialog: MutableState<Boolean>,
    onColorListener:(Color)->Unit
) {
    val kc = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var hasFocus by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val color = rememberSaveable(
        fColor,
        stateSaver = HsvColor.Saver
    ) {
        mutableStateOf(HsvColor.from(fColor))
    }
    val filter = remember(key1 = color.value) { FilterColorHex(color.value.toHex()) }


    SuperDialog(
        title = title,
        show = showDialog,
        imePadding = false,
        onFocus = {
            kc?.hide()
            focusManager.clearFocus()
        },
        onDismissRequest = {
            if (hasFocus){
                kc?.hide()
                focusManager.clearFocus()
                return@SuperDialog
            }
            if (doTextFieldValue(filter.getInputValue(),hasFocus,focusManager,color,context)){

                color.value = HsvColor.from(fColor)
                showDialog.value = false
            }
        },
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row {
                Image(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(SmoothRoundedCornerShape(10.dp,0.8f))
                        .border(
                            2.dp,
                            if (color.value.toColor() == colorScheme.surfaceVariant) colorScheme.secondaryContainer else Color.Transparent,
                            SmoothRoundedCornerShape(10.dp,0.8f)
                        ),
                    imageVector = ImageVector.vectorResource(R.drawable.transparent),
                    colorFilter = ColorFilter.tint(
                        color.value.toColor(),
                        BlendMode.SrcOver
                    ),
                    contentDescription = "ColorImage"
                )
                MTextField(
                    modifier = Modifier
                        .height(60.dp)
                        .padding(start = 20.dp)
                        .onFocusChanged {
                            hasFocus = it.hasFocus
                        },
                    value = filter.getInputValue(),
                    singleLine = true,
                    cornerRadius = 12.dp,
                    onValueChange = filter.onValueChange(),
                    trailingIcon = {
                        if (hasFocus){
                            Button(
                                modifier = Modifier.size(60.dp).padding(10.dp,16.dp),
                                onClick = {
                                    doTextFieldValue(filter.getInputValue(),hasFocus,focusManager,color,context)
                                },
                                //contentPadding = PaddingValues(10.dp,16.dp),
                                shape = SmoothRoundedCornerShape(12.dp,0.8f),
                                colors = buttonColors(
                                    backgroundColor = Color.Transparent,
                                    contentColor = Color.Transparent,
                                    disabledContentColor = Color.Transparent
                                )
                            ) {
                                Icon(
                                    ImageVector.vectorResource(R.drawable.yes),
                                    contentDescription = "yes",
                                    Modifier.size(25.dp),
                                    tint = colorScheme.outline
                                )

                            }

                        }
                    },
                    keyboardActions = KeyboardActions(onDone = {
                        doTextFieldValue(filter.getInputValue(),hasFocus,focusManager,color,context)


                    }),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            ClassicColorPicker(
                modifier = Modifier.wrapContentHeight(),
                color = color
            ) {
                color.value = it
                clearFocus(hasFocus,focusManager)
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BaseButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.cancel),
                cornerRadius = 15.dp,
                onClick = {
                    showDialog.value = false
                    color.value = HsvColor.from(fColor)
                }
            )
            Spacer(Modifier.width(20.dp))
            BaseButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.save),
                cornerRadius = 15.dp,
                submit = true,
                onClick = {
                    doTextFieldValue(filter.getInputValue(),hasFocus,focusManager,color,context)
                    onColorListener(color.value.toColor())
                    showDialog.value = false
                }
            )
        }
    }




}



fun doTextFieldValue(
    value: TextFieldValue,
    hasFocus : Boolean,
    focusManager: FocusManager,
    color : MutableState<HsvColor>,
    context : Context
):Boolean{
//    if (!hasFocus) {
//        return
//    }
    val local = value.text

    if ((local.length != 7) && (local.length != 9)) {
        Toast.makeText(
            context,
            "[长度不对]请重新输入！",
            Toast.LENGTH_SHORT
        ).show()
        return false
    }

    focusManager.clearFocus()
    color.value = HsvColor.from(local.colorFromHex())
    return true

}


fun clearFocus(
    hasFocus : Boolean,
    focusManager: FocusManager
){
    if (hasFocus) {
        focusManager.clearFocus()
    }
}



