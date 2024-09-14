package com.chaos.hyperstar.ui.base

import android.util.Log
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.core.text.isDigitsOnly
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.SwipeableState
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.chaos.hyperstar.ui.base.filter.FilterNumber
import com.chaos.hyperstar.ui.pagers.dialog
import com.chaos.hyperstar.utils.PreferencesUtil
import com.chaos.hyperstar.utils.SPUtils
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.MiuixScrollBehavior
import top.yukonga.miuix.kmp.MiuixSuperDialog
import top.yukonga.miuix.kmp.basic.MiuixButton
import top.yukonga.miuix.kmp.basic.MiuixCard
import top.yukonga.miuix.kmp.basic.MiuixSlider
import top.yukonga.miuix.kmp.basic.MiuixText
import top.yukonga.miuix.kmp.basic.MiuixTextField
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.showDialog
import kotlin.math.roundToInt


enum class Status{
    CLOSE, OPEN
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun XMiuixSlider(
    title : String,
    key : String,
    unit : Any = "",
    minValue: Float = 0f,
    maxValue: Float = 1f,
    progress: Float = 0.5f,
    decimalPlaces : Int = 0

) {

    val view = LocalView.current
    val effect = PreferencesUtil.getBoolean("is_progress_effect", false)
    var x_progress by remember { mutableStateOf(SPUtils.getFloat(key, progress)) }
    var dialog = remember { mutableStateOf(false) }


    //Dialog(dialog,unit)

    val squareSize = 150

    val swipeableState = rememberSwipeableState(Status.CLOSE)
    val sizePx = with(LocalDensity.current) { -squareSize.dp.toPx() }
    val anchors = mapOf(0f to Status.CLOSE, sizePx to Status.OPEN) // Maps anchor points (in px) to states
    val scope = rememberCoroutineScope()
    Box(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(((swipeableState.offset.value.roundToInt() / sizePx) * squareSize).dp)
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f)
                    .align(Alignment.CenterVertically),
                colors = ButtonColors(
                    MiuixTheme.colorScheme.primary,
                    MiuixTheme.colorScheme.onPrimary,
                    MiuixTheme.colorScheme.primary,
                    MiuixTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(0.dp, 0.dp),
                shape = RoundedCornerShape(15.dp, 0.dp, 0.dp, 15.dp),
                onClick = {
                    scope.launch {
                        swipeableState.animateTo(Status.CLOSE)
                    }
                    x_progress = progress
                    SPUtils.setFloat(key, x_progress)
                    //swipeableState = SwipeableState(initialValue = 0)

                }
            ) {

                MiuixText(
                    fontSize = 14.sp,
                    color = MiuixTheme.colorScheme.primaryContainer,
                    fontWeight = FontWeight.SemiBold,
                    text = "默认"
                )



            }
            Button(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(1f)
                    .align(Alignment.CenterVertically),
                onClick = {
                    scope.launch {
                        swipeableState.animateTo(Status.CLOSE)
                    }
                    //Toast.makeText()
                },
                colors = ButtonColors(MiuixTheme.colorScheme.background,MiuixTheme.colorScheme.onPrimary,MiuixTheme.colorScheme.background,MiuixTheme.colorScheme.background),
                contentPadding = PaddingValues(0.dp, 0.dp),
                shape = RoundedCornerShape(0.dp, 0.dp, 0.dp, 0.dp)
            ) {
                MiuixText(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    text = "取消"
                )

            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp)
                //.background(MiuixTheme.colorScheme.primaryContainer)
        ) {
            Column(
                Modifier.offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
            ){

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp)
                        .swipeable(
                            state = swipeableState,
                            anchors = anchors,
                            thresholds = { _, _ -> FractionalThreshold(0.3f) },
                            orientation = Orientation.Horizontal,
                            //enabled = false
                        )

                ) {
                    MiuixText(
                        modifier = Modifier.weight(1f),
                        text = title,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                    MiuixText(
                        modifier = Modifier.clickable {
                            dialog.value = true
                        },
                        text = if (x_progress == progress) "默认"
                        else if (decimalPlaces == 0) x_progress.toInt().toString() + unit
                        else x_progress.toString() + unit,
                        textAlign = TextAlign.End,
                        fontSize = 14.sp
                    )
                }
                MiuixSlider(
                    progress = x_progress,
                    onProgressChange = { newProgress ->
                        x_progress = newProgress
                        SPUtils.setFloat(key, x_progress)
                    },
                    effect = effect,
                    maxValue = maxValue,
                    minValue = minValue,
                    //dragShow = true,
                    decimalPlaces = decimalPlaces,
                    modifier = Modifier
                        .padding(horizontal = 28.dp)
                        .padding(top = 10.dp)
                )
            }

        }

    }


}

@Composable
fun XMiuixSliders(
    title: String,
    key: String,
    unit: Any = "",
    minValue: Float = 0f,
    maxValue: Float = 1f,
    progress: Float = 0.5f,
    decimalPlaces: Int = 0,
    values: (Float) -> Float = { progress },

    ) {
    val effect = PreferencesUtil.getBoolean("is_progress_effect", false)

    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp)
    ) {
        Column {
            var x_progress by remember { mutableStateOf(SPUtils.getFloat(key, progress)) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
            ) {
                MiuixText(
                    modifier = Modifier.weight(1f),
                    text = title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                MiuixText(
                    text = if (x_progress == progress) "默认"
                    else if (decimalPlaces == 0) values(x_progress).toInt().toString() + unit
                    else values(x_progress).toString() + unit,
                    textAlign = TextAlign.End,
                    fontSize = 14.sp
                )
            }
            MiuixSlider(
                progress = x_progress,
                onProgressChange = { newProgress ->
                    x_progress = newProgress
                    SPUtils.setFloat(key, x_progress)
                },
                effect = effect,
                maxValue = maxValue,
                minValue = minValue,
                //dragShow = true,
                decimalPlaces = decimalPlaces,
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .padding(top = 10.dp)
            )
        }


    }
}


@Composable
fun Dialog(
    showDialog: MutableState<Boolean>,
    unit: Any = "",
) {
    if (showDialog.value) {
        Popup(
            alignment = Alignment.Center,
            offset = IntOffset(-0, -100)
        ) {
            Row(
                modifier = Modifier.width(100.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MiuixButton(
                    modifier = Modifier.weight(1f),
                    text = "恢复默认",
                    onClick = {
                        //dismissDialog()
                        showDialog.value = false
                    }
                )
            }
        }
    }


}

@Composable
fun Dialogs(
    showDialog: MutableState<Boolean>,
    unit: Any = "",
) {
    var text by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val filter = remember { FilterNumber() }
    if (showDialog.value) {
        showDialog(
            content = {
                MiuixSuperDialog(
                    title = "Title",
                    onDismissRequest = {
                        showDialog.value = false
                    },
                ) {
                    MiuixTextField(
                        value = filter.getInputValue(),
                        cornerRadius = 18.dp,
                        onValueChange = {
                            //it.byteInputStream()
                            filter.onValueChange()
                            //if (it.isDigitsOnly()) text = it
                            //text = it
                        },
                        label = "进度值",
                        modifier = Modifier
                            .padding(vertical = 0.dp),
                        //keyboardType = KeyboardType.Number,
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                        }),//,keyboardType = KeyboardType.Number
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        singleLine = true,
                        trailingIcon = {
                            MiuixText(
                                text = unit.toString(),
                                Modifier.padding(horizontal = 18.dp)
                            )
                        }
                    )
                    Spacer(Modifier.height(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MiuixButton(
                            modifier = Modifier.weight(1f),
                            text = "Cancel",
                            onClick = {
                                dismissDialog()
                                showDialog.value = false
                            }
                        )
                        Spacer(Modifier.width(20.dp))
                        MiuixButton(
                            modifier = Modifier.weight(1f),
                            text = "Confirm",
                            submit = true,
                            onClick = {
                                dismissDialog()
                                showDialog.value = false
                            }
                        )
                    }
                }
            }
        )
    }
}




