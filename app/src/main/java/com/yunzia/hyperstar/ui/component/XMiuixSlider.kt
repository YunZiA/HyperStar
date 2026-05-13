
package com.yunzia.hyperstar.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.shapes.SmoothRoundedCornerShape
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.prefs.PreferencesUtil
import com.yunzia.hyperstar.prefs.SPUtils
import com.yunzia.hyperstar.ui.component.dialog.OverlayDialogs
import com.yunzia.hyperstar.ui.component.enums.EventState
import com.yunzia.hyperstar.ui.component.modifier.bounceClick
import com.yunzia.hyperstar.ui.component.modifier.bounceScale
import com.yunzia.hyperstar.ui.component.tool.FilterFloat
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import kotlin.math.pow
import kotlin.math.roundToInt


enum class Status{
    CLOSE, OPEN
}


@Composable
fun XMiuixSlider(
    host:String? = null,
    isDialog:Boolean = false,
    title: String,
    key: String,
    unit: String = "",
    paddingValues: PaddingValues? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    defValue: Float = 0.5f,
    enabled: Boolean = true,
    value: MutableFloatState = remember { mutableFloatStateOf(SPUtils.getFloat(key, defValue).coerceIn(valueRange)) },
    decimalPlaces: Int = 0

) {

    val layoutDirection = LocalLayoutDirection.current

    val dialog = remember { mutableStateOf(false) }
    @Suppress("NAME_SHADOWING")
    val paddingValues = remember { paddingValues } ?: remember { BasicComponentDefaults.InsideMargin }

    val titleColor = if (enabled) colorScheme.onSurface else colorScheme.disabledOnSecondaryVariant
    val valueColor = if (enabled) colorScheme.onSurfaceVariantSummary else colorScheme.disabledOnSecondaryVariant
    val click = remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }

    val enable = PreferencesUtil.getBoolean("bounce_anim_enable",true)
    val eventState = remember { mutableStateOf(EventState.Idle) }

    Column(
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
            .bounceScale(eventState)
            .clip(SmoothRoundedCornerShape(8.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        if (click.value) {
                            val press = PressInteraction.Press(offset)
                            interactionSource.emit(press)
                            tryAwaitRelease()
                            interactionSource.emit(PressInteraction.Release(press))
                            click.value = false
                        }

                    }
                ) {
                    if (click.value) {
                        dialog.value = true
                    }

                }
            }
            .indication(interactionSource, LocalIndication.current)
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            )
        //.offset { IntOffset(swappableState.offset.value.roundToInt(), 0) }
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
                .bounceClick(eventState, (enable && !isDialog && !click.value && enabled))
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (enabled && !isDialog) {
                            val event = awaitPointerEvent()
                            when (event.type) {
                                PointerEventType.Press -> {
                                    click.value = true
                                }
                            }

                        }
                    }

                }

        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                fontWeight = FontWeight.Medium,
                color = titleColor,
                fontSize = TextUnit.Unspecified
            )
            Text(
                modifier = Modifier,
                color = valueColor,
                text = if (value.floatValue == defValue) stringResource(R.string.default_value)
                else if (decimalPlaces == 0) value.floatValue.coerceIn(valueRange).toInt().toString() + unit
                else value.floatValue.coerceIn(valueRange).toString() + unit,
                textAlign = TextAlign.End,
                fontSize = 14.sp
            )
        }
        Slider(
            value = value.floatValue,
            onValueChange = { newValue ->
                value.floatValue = newValue
                SPUtils.putFloat(key, value.floatValue)
            },
            valueRange = valueRange,
            steps = calcSteps(valueRange,decimalPlaces),
//            steps = ((valueRange.endInclusive - valueRange.start)*(100f * (10f.pow(decimalPlaces)))).toInt(),
            modifier = Modifier
                .padding(
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    end = paddingValues.calculateEndPadding(layoutDirection)
                )
                .padding(top = 10.dp)
            ,
            enabled = enabled
        )
    }




    ValueDialog(if ( host == null ){
        title
    }else{
        "$host·$title"
    },key,value,valueRange,defValue,unit,decimalPlaces,dialog)


}

@Composable
fun XMiuixSliders(
    title: String,
    key: String,
    unit: String = "",
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    progress: Float = 0.5f,
    decimalPlaces: Int = 0,
    values: (Float) -> Float = { progress }
) {

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
                value = x_progress,
                onValueChange = { newValue ->
                    x_progress = newValue
                    SPUtils.putFloat(key, x_progress)
                },
                valueRange = valueRange,
                steps = calcSteps(valueRange,decimalPlaces),
//                steps = ((valueRange.endInclusive - valueRange.start)*(100f * (10f.pow(decimalPlaces)))).toInt(),
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 10.dp)
            )
        }


    }


}


@Composable
fun XSuperSliders(
    host:String? = null,
    title : String,
    key : String,
    unit : String = "",
    insideMargin: PaddingValues? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    defValue: Float = 0.5f,
    enabled : Boolean = true,
    value : MutableFloatState = remember { mutableFloatStateOf(SPUtils.getFloat(key, defValue).coerceIn(valueRange)) },
    decimalPlaces : Int = 0

) {

    val dialog = remember { mutableStateOf(false) }

    @Suppress("NAME_SHADOWING")
    val insideMargin = remember { insideMargin } ?: remember { BasicComponentDefaults.InsideMargin }
    val titleColor = if (enabled) colorScheme.onSurface else colorScheme.disabledOnSecondaryVariant
    val valueColor =
        if (enabled) colorScheme.onSurfaceVariantSummary else colorScheme.disabledOnSecondaryVariant
    val click = remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }

    val enable = PreferencesUtil.getBoolean("bounce_anim_enable",true)
    val eventState = remember { mutableStateOf(EventState.Idle) }

    val layoutDirection = LocalLayoutDirection.current


    Column(
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
            .bounceScale(eventState)
            .clip(SmoothRoundedCornerShape(8.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        if (click.value) {

                            val press = PressInteraction.Press(offset)
                            interactionSource.emit(press)
                            tryAwaitRelease()
                            interactionSource.emit(PressInteraction.Release(press))
                            click.value = false
                        }


                    }
                ) {
                    if (click.value) {
                        dialog.value = true

                    }

                }
            }
            .indication(interactionSource, LocalIndication.current)
            .padding(insideMargin)

    ) {

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                //.padding(start = insideMargin.calculateStartPadding(layoutDirection), end = insideMargin.calculateEndPadding(layoutDirection))
                .bounceClick(eventState, (enable && !click.value && enabled))
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (enabled) {
                            val event = awaitPointerEvent()
                            when (event.type) {
                                PointerEventType.Press -> {
                                    click.value = true
                                }
                            }
                        }
                    }

                }

        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                fontWeight = FontWeight.Medium,
                color = titleColor,
                fontSize = TextUnit.Unspecified
            )
            Text(
                modifier = Modifier,
                color = valueColor,
                text = if (value.floatValue == defValue) stringResource(R.string.default_value)
                else if (decimalPlaces == 0) value.floatValue.toInt().toString() + unit
                else value.floatValue.toString() + unit,
                textAlign = TextAlign.End,
                fontSize = 14.sp
            )
        }
        Slider(
            value = value.floatValue,
            onValueChange = { newValue ->
                value.floatValue = newValue
                SPUtils.putFloat(key, value.floatValue)
            },
            valueRange = valueRange,
            steps = calcSteps(valueRange,decimalPlaces),
//                ((valueRange.endInclusive - valueRange.start)*(100f * (10f.pow(decimalPlaces)))).toInt()
            modifier = Modifier
                //.padding(start = insideMargin.calculateStartPadding(layoutDirection), end = insideMargin.calculateEndPadding(layoutDirection))
                .padding(top = 10.dp),
            enabled = enabled
        )

    }

    ValueDialog(if ( host == null ){
        title
    }else{
        "$host·$title"
    },key,value,valueRange,defValue,unit,decimalPlaces,dialog)


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
    valueRange: ClosedFloatingPointRange<Float>,
    defValue: Float,
    unit: String = "",
    decimalPlaces: Int,
    showDialog: MutableState<Boolean>
){

    val kc = LocalSoftwareKeyboardController.current
    val filter = remember(key1 = values.floatValue) { FilterFloat(values.floatValue,valueRange.start,valueRange.endInclusive,decimalPlaces) }
    val focusManager = LocalFocusManager.current
    var hasFocus by remember { mutableStateOf(false) }
    val firstIsDf = remember(values.floatValue) { mutableStateOf(values.floatValue == defValue) }
    ShowDef(firstIsDf,showDialog,values.floatValue,defValue,decimalPlaces,filter)

    val defValues = TextFieldValue("", TextRange(0))

    OverlayDialog(
        title = title,
        show = showDialog.value,
//        onFocus = {
//            kc?.hide()
//            focusManager.clearFocus()
//        },
        onDismissRequest = {
            if (hasFocus){
                kc?.hide()
                focusManager.clearFocus()
                return@OverlayDialog
            }
            filter.setInputValue(String.format("%.${decimalPlaces}f", values.floatValue))
            showDialog.value = false
        }
    ) {
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            kc?.show()
        }
//
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
                .onFocusChanged {
                    hasFocus = it.hasFocus
                },
            //backgroundColor = colorScheme.surfaceVariant,
            label = stringResource(R.string.default_value),
            value = if (firstIsDf.value) defValues else filter.getInputValue(),
            useLabelAsPlaceholder = true,
            maxLines = 1,
            keyboardOptions =  KeyboardOptions(imeAction = ImeAction.Done,keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }),
            trailingIcon = {
                Text(
                    text = unit,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            },
            onValueChange = filter.onValueChange()
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
                    if (filter.getInputValue().text == ""){
                        filter.setInputValue(String.format("%.${decimalPlaces}f", defValue))
                    }else{
                        filter.setInputValue(String.format("%.${decimalPlaces}f", values.floatValue))
                    }

                }

            )
            Spacer(Modifier.height(10.dp))
            BaseButton(
                text = stringResource(R.string.recovery_default),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    focusManager.clearFocus()
                    filter.setInputValue(String.format("%.${decimalPlaces}f", defValue))
                    values.floatValue = defValue
                    SPUtils.putFloat(key, defValue)
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
                    if (filter.getInputValue().text == ""){
                        filter.setInputValue(String.format("%.${decimalPlaces}f", defValue))
                        values.floatValue = defValue
                        SPUtils.putFloat(key, defValue)
                    }else{
                        values.floatValue = filter.getInputValue().text.toFloat()
                        SPUtils.putFloat(key, values.floatValue)

                    }
                    showDialog.value = false
                    //showDialog.value = false

                }

            )

        }
    }
}

fun calcSteps(
    range: ClosedFloatingPointRange<Float>,
    decimals: Int
): Int {
    val factor = 10.0.pow(decimals).toInt()

    val points = ((range.endInclusive - range.start).toInt() * factor)
//        .roundToInt()

    val result =  points - 1  // ✅ steps 定义
    return  result
//    return 19
}
