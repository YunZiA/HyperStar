package com.chaos.hyperstar.ui.base

import android.util.Log
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
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
import androidx.compose.material3.Shapes
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.SwipeableState
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.chaos.hyperstar.R
import com.chaos.hyperstar.utils.PreferencesUtil
import com.chaos.hyperstar.utils.SPUtils
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.showDialog
import top.yukonga.miuix.kmp.utils.squircleshape.CornerSmoothing
import top.yukonga.miuix.kmp.utils.squircleshape.SquircleShape
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
    x_progress : MutableFloatState = remember { mutableFloatStateOf(SPUtils.getFloat(key, progress)) },
    decimalPlaces : Int = 0

) {

    val view = LocalView.current
    val effect = PreferencesUtil.getBoolean("is_progress_effect", false)
    //var x_progress by remember { mutableStateOf(SPUtils.getFloat(key, progress)) }
    var dialog = remember { mutableStateOf(false) }

    val insideMargin = remember { insideMargin } ?: remember { DpSize(24.dp, 15.dp) }
    //Dialog(dialog,unit)

    val squareSize = 130

    val swipeableState = rememberSwipeableState(Status.CLOSE)
    val sizePx = with(LocalDensity.current) { -squareSize.dp.toPx() }
    val anchors = mapOf(0f to Status.CLOSE, sizePx to Status.OPEN)
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
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(
                modifier = Modifier
                    .size(56.dp, 48.5.dp)
                    .align(Alignment.CenterVertically),
                colors = ButtonColors(
                    MiuixTheme.colorScheme.primary,
                    MiuixTheme.colorScheme.onPrimary,
                    MiuixTheme.colorScheme.primary,
                    MiuixTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(0.dp, 0.dp),
                shape = SquircleShape(16.dp,CornerSmoothing.Medium),
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    scope.launch {
                        swipeableState.animateTo(Status.CLOSE)
                    }
                    x_progress.floatValue = progress
                    SPUtils.setFloat(key, x_progress.floatValue)

                }
            ) {

                Text(
                    fontSize = 12.sp,
                    color = MiuixTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold,
                    text = stringResource(R.string.default_it)
                )



            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                modifier = Modifier
                    .size(56.dp, 48.5.dp)
                    .align(Alignment.CenterVertically),
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    scope.launch {
                        swipeableState.animateTo(Status.CLOSE)
                    }
                    //Toast.makeText()
                },
                colors = ButtonColors(
                    MiuixTheme.colorScheme.secondary,
                    MiuixTheme.colorScheme.onPrimary,
                    MiuixTheme.colorScheme.background,
                    MiuixTheme.colorScheme.background
                ),
                contentPadding = PaddingValues(0.dp, 0.dp),
                shape = SquircleShape(16.dp,CornerSmoothing.Medium)
            ) {
                Text(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    text = stringResource(R.string.cancel)
                )

            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = insideMargin.height)
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
                        .padding(horizontal = insideMargin.width)
                        .swipeable(
                            state = swipeableState,
                            anchors = anchors,
                            thresholds = { _, _ -> FractionalThreshold(0.3f) },
                            orientation = Orientation.Horizontal,
                            //enabled = false
                        )

                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = title,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                    Text(
                        modifier = Modifier.clickable {
                            dialog.value = true
                        },
                        text = if (x_progress.floatValue == progress) "默认"
                        else if (decimalPlaces == 0) x_progress.floatValue.toInt().toString() + unit
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
                    enabled = if (swipeableState.targetValue == Status.CLOSE) true else false
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

