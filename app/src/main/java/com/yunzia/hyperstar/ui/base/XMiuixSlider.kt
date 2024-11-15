package com.yunzia.hyperstar.ui.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.yunzia.hyperstar.utils.PreferencesUtil
import com.yunzia.hyperstar.utils.SPUtils
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
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
    insideMargin: DpSize? = null,
    minValue: Float = 0f,
    maxValue: Float = 1f,
    progress: Float = 0.5f,
    enabled : Boolean = true,
    x_progress : MutableFloatState = remember { mutableFloatStateOf(SPUtils.getFloat(key, progress).coerceIn(minValue, maxValue)) },
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
                    x_progress.floatValue = progress
                    SPUtils.setFloat(key, x_progress.floatValue)

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
                    modifier = Modifier.clickable {
                        dialog.value = true
                    },
                    color = valueColor,
                    text = if (x_progress.floatValue == progress) stringResource(R.string.default_value)
                    else if (decimalPlaces == 0) x_progress.floatValue.coerceIn(0f, 100f).toInt().toString() + unit
                    else x_progress.floatValue.toString() + unit,
                    textAlign = TextAlign.End,
                    fontSize = 14.sp
                )
            }
            Slider(
                progress = x_progress.floatValue,
                onProgressChange = { newProgress ->
                    x_progress.floatValue = newProgress
                    SPUtils.setFloat(key, x_progress.floatValue)
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


}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun XSuperSliders(
    title : String,
    key : String,
    unit : Any = "",
    insideMargin: DpSize? = null,
    minValue: Float = 0f,
    maxValue: Float = 1f,
    progress: Float = 0.5f,
    enabled : Boolean = true,
    x_progress : MutableFloatState = remember { mutableFloatStateOf(SPUtils.getFloat(key, progress)) },
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
                    x_progress.floatValue = progress
                    SPUtils.setFloat(key, x_progress.floatValue)

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
                    modifier = Modifier.clickable {
                        dialog.value = true
                    },
                    color = valueColor,
                    text = if (x_progress.floatValue == progress) stringResource(R.string.default_value)
                    else if (decimalPlaces == 0) x_progress.floatValue.coerceIn(0f, 100f).toInt().toString() + unit
                    else x_progress.floatValue.toString() + unit,
                    textAlign = TextAlign.End,
                    fontSize = 14.sp
                )
            }
            Slider(
                progress = x_progress.floatValue,
                onProgressChange = { newProgress ->
                    x_progress.floatValue = newProgress
                    SPUtils.setFloat(key, x_progress.floatValue)
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

@Composable
fun ValueDialog(
    showDialog: MutableState<Boolean>
){
    val value = remember { mutableStateOf("") }

    if (!showDialog.value){
        return
    }
    showDialog{
        SuperDialog(
            title = "Dialog 2",
            show = showDialog,
            onDismissRequest = {
                showDialog.value = false
            }
        ){
            TextField(
                modifier = Modifier.padding(bottom = 16.dp),
                value = value.value,
                maxLines = 1,
                onValueChange = { value.value = it }
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    text = "Cancel",
                    onClick = {
                        dismissDialog()
                        showDialog.value = false
                    }
                )
                Spacer(Modifier.width(20.dp))
                Button(
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

}

