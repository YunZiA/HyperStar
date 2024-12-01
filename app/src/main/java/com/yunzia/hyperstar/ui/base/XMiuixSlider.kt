package com.yunzia.hyperstar.ui.base

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.dialog.SuperDialog
import com.yunzia.hyperstar.ui.base.modifier.bounceAnim
import com.yunzia.hyperstar.ui.base.tool.FilterFloat
import com.yunzia.hyperstar.utils.PreferencesUtil
import com.yunzia.hyperstar.utils.SPUtils
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import kotlin.math.roundToInt


enum class Status{
    CLOSE, OPEN
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun XMiuixSlider(
    host:String? = null,
    isDialog:Boolean = false,
    title: String,
    key: String,
    unit: String = "",
    paddingValues: PaddingValues? = null,
    minValue: Float = 0f,
    maxValue: Float = 1f,
    defValue: Float = 0.5f,
    enabled: Boolean = true,
    progress: MutableFloatState = remember { mutableFloatStateOf(SPUtils.getFloat(key, defValue).coerceIn(minValue, maxValue)) },
    decimalPlaces: Int = 0

) {

    val layoutDirection = LocalLayoutDirection.current

    val effect = PreferencesUtil.getBoolean("is_progress_effect", false)
    //var x_progress by remember { mutableStateOf(SPUtils.getFloat(key, progress)) }
    val dialog = remember { mutableStateOf(false) }
    //ValueDialog(dialog)
    val swappableState = rememberSwipeableState(Status.CLOSE)
    @Suppress("NAME_SHADOWING")
    val paddingValues = remember { paddingValues } ?: remember { PaddingValues(24.dp, 15.dp) }
    //Dialog(dialog,unit)
    val enable = enabled && swappableState.targetValue == Status.CLOSE
    val titleColor = if (enable) colorScheme.onSurface else colorScheme.disabledOnSecondaryVariant
    val valueColor = if (enable) colorScheme.onSurfaceVariantSummary else colorScheme.disabledOnSecondaryVariant
    val squareSize = 120.dp+paddingValues.calculateEndPadding(layoutDirection)

    val sizePx = with(LocalDensity.current) { -squareSize.toPx() }
    val anchors = mapOf(0f to Status.CLOSE, sizePx to Status.OPEN)
    val scope = rememberCoroutineScope()
    Box(
        Modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(squareSize)
                .offset {
                    IntOffset(
                        (squareSize.toPx() + swappableState.offset.value).toInt(),
                        0
                    )
                },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            MiniTextButton(
                text = stringResource(R.string.default_it),
                textColor = colorScheme.onPrimaryContainer,
                modifier = Modifier.size(56.dp, 48.5.dp),
                radius = 12.dp,
                color = colorScheme.primary,
                onClick = {
                    scope.launch {
                        swappableState.animateTo(Status.CLOSE)
                    }
                    progress.floatValue = defValue
                    SPUtils.setFloat(key, progress.floatValue)

                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            MiniTextButton(
                text = stringResource(R.string.cancel),
                modifier = Modifier.size(56.dp, 48.5.dp),
                radius = 12.dp,
                color = colorScheme.secondary,
                onClick = {

                    scope.launch {
                        swappableState.animateTo(Status.CLOSE)
                    }

                },
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
                .offset { IntOffset(swappableState.offset.value.roundToInt(), 0) }
        ){

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(
                        start = paddingValues.calculateStartPadding(layoutDirection),
                        end = paddingValues.calculateEndPadding(layoutDirection)
                    )
                    .swipeable(
                        state = swappableState,
                        anchors = anchors,
                        enabled = enabled,
                        thresholds = { _, _ -> FractionalThreshold(0.3f) },
                        orientation = Orientation.Horizontal,
                        //enabled = false
                    )

            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = title,
                    fontWeight = FontWeight.Medium,
                    color = titleColor,
                    fontSize = TextUnit.Unspecified
                )
                Text(
                    modifier =if (isDialog) Modifier else{
                        Modifier.bounceAnim()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        return@detectTapGestures
                                    }
                                ) {
                                    dialog.value = true
                                }
                            }
                    },
                    color = valueColor,
                    text = if (progress.floatValue == defValue) stringResource(R.string.default_value)
                    else if (decimalPlaces == 0) progress.floatValue.coerceIn(0f, 100f).toInt().toString() + unit
                    else progress.floatValue.toString() + unit,
                    textAlign = TextAlign.End,
                    fontSize = 14.sp
                )
            }
            Slider(
                progress = progress.floatValue,
                onProgressChange = { newProgress ->
                    progress.floatValue = newProgress
                    SPUtils.setFloat(key, progress.floatValue)
                },
                effect = effect,
                maxValue = maxValue,
                minValue = minValue,
                //dragShow = true,
                decimalPlaces = decimalPlaces,
                modifier = Modifier
                    .padding(
                        start = paddingValues.calculateStartPadding(layoutDirection),
                        end = paddingValues.calculateEndPadding(layoutDirection)
                    )
                    .padding(top = 10.dp),
                enabled = enable
            )
        }

    }


    ValueDialog(if ( host == null ){
        title
    }else{
        "$host·$title"
    },key,progress,maxValue,minValue,defValue,unit,decimalPlaces,dialog)


}

@Composable
fun XMiuixSliders(
    title: String,
    key: String,
    unit: String = "",
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
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                Text(
                    text = if (x_progress == progress) "默认"
                    else if (decimalPlaces == 0) values(x_progress).toInt().toString() + unit
                    else values(x_progress).toString() + unit,
                    textAlign = TextAlign.End,
                    fontSize = 14.sp
                )
            }
            Slider(
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
                    .padding(horizontal = 24.dp)
                    .padding(top = 10.dp)
            )
        }


    }


}


@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun XSuperSliders(
    host:String? = null,
    title : String,
    key : String,
    unit : String = "",
    insideMargin: DpSize? = null,
    minValue: Float = 0f,
    maxValue: Float = 1f,
    defValue: Float = 0.5f,
    enabled : Boolean = true,
    progress : MutableFloatState = remember { mutableFloatStateOf(SPUtils.getFloat(key, defValue)) },
    decimalPlaces : Int = 0

) {

    val effect = PreferencesUtil.getBoolean("is_progress_effect", false)
    //var x_progress by remember { mutableStateOf(SPUtils.getFloat(key, progress)) }
    val dialog = remember { mutableStateOf(false) }
    //ValueDialog(dialog)
    val swappableState = rememberSwipeableState(Status.CLOSE)
    @Suppress("NAME_SHADOWING")
    val insideMargin = remember { insideMargin } ?: remember { DpSize(24.dp, 15.dp) }
    //Dialog(dialog,unit)
    val enable = enabled && swappableState.targetValue == Status.CLOSE
    val titleColor = if (enable) colorScheme.onSurface else colorScheme.disabledOnSecondaryVariant
    val valueColor = if (enable) colorScheme.onSurfaceVariantSummary else colorScheme.disabledOnSecondaryVariant
    val squareSize = 120.dp+insideMargin.width

    val sizePx = with(LocalDensity.current) { -squareSize.toPx() }
    val anchors = mapOf(0f to Status.CLOSE, sizePx to Status.OPEN)
    val scope = rememberCoroutineScope()
    Box(
        Modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(squareSize)
                .offset {
                    IntOffset(
                        (squareSize.toPx() + swappableState.offset.value).toInt(),
                        0
                    )
                },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            MiniTextButton(
                text = stringResource(R.string.default_it),
                textColor = colorScheme.onPrimaryContainer,
                modifier = Modifier.size(56.dp, 48.5.dp),
                radius = 12.dp,
                color = colorScheme.primary,
                onClick = {
                    scope.launch {
                        swappableState.animateTo(Status.CLOSE)
                    }
                    progress.floatValue = defValue
                    SPUtils.setFloat(key, progress.floatValue)

                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            MiniTextButton(
                text = stringResource(R.string.cancel),
                modifier = Modifier.size(56.dp, 48.5.dp),
                radius = 12.dp,
                color = colorScheme.secondary,
                onClick = {

                    scope.launch {
                        swappableState.animateTo(Status.CLOSE)
                    }

                },
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = insideMargin.height)
                .offset { IntOffset(swappableState.offset.value.roundToInt(), 0) }
        ){

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(horizontal = insideMargin.width)
                    .swipeable(
                        state = swappableState,
                        anchors = anchors,
                        enabled = enabled,
                        thresholds = { _, _ -> FractionalThreshold(0.3f) },
                        orientation = Orientation.Horizontal,
                        //enabled = false
                    )

            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = title,
                    fontWeight = FontWeight.Medium,
                    color = titleColor,
                    fontSize = TextUnit.Unspecified
                )
                Text(
                    modifier = Modifier.bounceAnim()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    return@detectTapGestures
                                }
                            ) {
                                dialog.value = true
                            }
                        },
                    color = valueColor,
                    text = if (progress.floatValue == defValue) stringResource(R.string.default_value)
                    else if (decimalPlaces == 0) progress.floatValue.coerceIn(0f, 100f).toInt().toString() + unit
                    else progress.floatValue.toString() + unit,
                    textAlign = TextAlign.End,
                    fontSize = 14.sp
                )
            }
            Slider(
                progress = progress.floatValue,
                onProgressChange = { newProgress ->
                    progress.floatValue = newProgress
                    SPUtils.setFloat(key, progress.floatValue)
                },
                effect = effect,
                maxValue = maxValue,
                minValue = minValue,
                //dragShow = true,
                decimalPlaces = decimalPlaces,
                modifier = Modifier
                    .padding(horizontal = insideMargin.width)
                    .padding(top = 10.dp),
                enabled = enable
            )
        }

    }

    ValueDialog(if ( host == null ){
        title
    }else{
        "$host·$title"
    },key,progress,maxValue,minValue,defValue,unit,decimalPlaces,dialog)


}

@Composable
fun ShowDef(
    firstIsDf:MutableState<Boolean>,
    showDialog: MutableState<Boolean>,
    values: Float,
    defValue: Float,
    decimalPlaces: Int,
    filter: FilterFloat

){
    when(showDialog.value){
        true->{
            if (firstIsDf.value && filter.getInputValue().text != String.format("%.${decimalPlaces}f", defValue)){
                firstIsDf.value = false
            }
        }
        false->{
            firstIsDf.value = values == defValue
        }
    }


}

@Composable
fun ValueDialog(
    title: String,
    key: String,
    values: MutableFloatState,
    maxValue: Float,
    minValue: Float,
    defValue: Float,
    unit : String = "",
    decimalPlaces: Int,
    showDialog: MutableState<Boolean>
){

    val kc = LocalSoftwareKeyboardController.current
    val filter = remember(key1 = values.floatValue) { FilterFloat(values.floatValue,minValue,maxValue,decimalPlaces) }
    val focusManager = LocalFocusManager.current
    var hasFocus by remember { mutableStateOf(false) }
    val firstIsDf = remember(values.floatValue) { mutableStateOf(values.floatValue == defValue) }
    ShowDef(firstIsDf,showDialog,values.floatValue,defValue,decimalPlaces,filter)

    val newString = StringBuffer()
    val defValues = TextFieldValue(newString.toString(), TextRange(0))

    SuperDialog(
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
                return@SuperDialog
            }
            //firstIsDf.value = values.floatValue == defValue
            filter.setInputValue(String.format("%.${decimalPlaces}f", values.floatValue))
            dismissDialog(showDialog)
        }
    ) {
//
        Text(
            stringResource(R.string.range_des, String.format("%.${decimalPlaces}f", minValue), unit, String.format("%.${decimalPlaces}f", maxValue), unit),
            Modifier
                .fillMaxWidth()
                .padding(start = 5.dp, top = 5.dp, bottom = 10.dp),
            color = colorScheme.primary,
            textAlign = TextAlign.Start,
            fontSize = 13.sp
        )
        SuperTextField(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .onFocusChanged {
                    hasFocus = it.hasFocus
                },
            //backgroundColor = colorScheme.surfaceVariant,
            label = if (firstIsDf.value) stringResource(R.string.default_value) else "",
            value = if (firstIsDf.value) defValues else filter.getInputValue(),
            maxLines = 1,
            trailingIcon = {
                Text(
                    text = unit,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            },
            onValueChange = filter.onValueChange()
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BaseButton(
                text = stringResource(R.string.cancel),
                modifier = Modifier.weight(1f),
                onClick = {
                    dismissDialog(showDialog)
                    filter.setInputValue(String.format("%.${decimalPlaces}f", values.floatValue))
                    //showDialog.value = false

                }

            )

            Spacer(Modifier.width(20.dp))
            BaseButton(
                text = stringResource(R.string.sure),
                modifier = Modifier.weight(1f),
                submit = true,
                onClick = {
                    values.floatValue = filter.getInputValue().text.toFloat()
                    SPUtils.setFloat(key, values.floatValue)
                    dismissDialog(showDialog)
                    //showDialog.value = false

                }

            )

        }
    }



}

