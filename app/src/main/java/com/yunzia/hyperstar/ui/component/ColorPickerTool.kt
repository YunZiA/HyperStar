package com.yunzia.hyperstar.ui.component

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.prefs.SPUtils
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import yunzia.colorpicker.ColorPickerDialog
import yunzia.colorpicker.colorFromHex
import yunzia.colorpicker.toHex

fun getDefaultColor(
    dfColor:Color,
    key : String
):Color{
    val loaclColor = SPUtils.getString(key,"null")
    return if (loaclColor == "null"){
        dfColor
    }else {
        loaclColor.colorFromHex()
    }
}

@Composable
fun ColorPickerTool(
    modifier: Modifier = Modifier,
    title:String,
    dfColor:Color = Color.Transparent,
    key : String
) {
    val view = LocalView.current
    val scope = rememberCoroutineScope()

    val showDialog = remember { mutableStateOf(false) }
    val colorState = remember { mutableStateOf(getDefaultColor(dfColor, key)) }

    val status = remember { mutableStateOf(Status.CLOSE) }
    val offsetX = remember { Animatable(0f) }

    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val insideMargin = BasicComponentDefaults.InsideMargin

    val squareSize = 103.dp + insideMargin.calculateEndPadding(layoutDirection)
    val maxOffset = with(density) { -squareSize.toPx() }


    val draggableState = rememberDraggableState { delta ->
        scope.launch {
            val new = (offsetX.value + delta).coerceIn(maxOffset, 0f)
            offsetX.snapTo(new)
        }
    }

    fun settle() {
        scope.launch {
            val target = if (offsetX.value < maxOffset / 2) {
                status.value = Status.OPEN
                maxOffset
            } else {
                status.value = Status.CLOSE
                0f
            }
            offsetX.animateTo(target)
        }
    }

    Box(
        modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = { settle() }
                )
                .clickable(
                    enabled = status.value == Status.CLOSE
                ) {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    showDialog.value = true
                }
                .padding(insideMargin),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                modifier = Modifier.weight(1f),
                text = title,
                fontWeight = FontWeight.Medium,
                color = if (status.value == Status.CLOSE)
                    colorScheme.onSurface
                else
                    colorScheme.disabledOnSecondaryVariant
            )

            val colorPreviewShape  = remember { RoundedCornerShape(30.dp) }
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationX = offsetX.value
                        shadowElevation = 12f
                        shape = colorPreviewShape
                    }
                    .size(30.dp)
                    .border(3.dp, Color.White, colorPreviewShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier
                        .size(27.dp)
                        .clip(colorPreviewShape)
                        .border(
                            width = 3.dp,
                            color = if (colorState.value == Color.White) Color(0xFFF0F0F0) else Color.Transparent,
                            shape = colorPreviewShape
                        ),
                    painter = painterResource(R.drawable.transparent),
                    colorFilter = ColorFilter.tint(colorState.value, BlendMode.SrcOver),
                    contentDescription = "ColorImage"
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(squareSize)
                .graphicsLayer {
                    translationX = offsetX.value + squareSize.toPx()
                    alpha = if (offsetX.value == 0f) 0f else 1f
                },
            verticalAlignment = Alignment.CenterVertically
        ) {

            MiniTextButton(
                text = stringResource(R.string.default_it),
                textColor = colorScheme.onPrimaryContainer,
                fontSize = 11.sp,
                modifier = Modifier.size(48.dp, 40.dp),
                radius = 10.dp,
                color = colorScheme.primary,
                onClick = {
                    scope.launch {
                        status.value = Status.CLOSE
                        offsetX.animateTo(0f)
                    }
                    colorState.value = dfColor
                    SPUtils.putString(key, "null")
                }
            )

            Spacer(Modifier.width(6.dp))

            MiniTextButton(
                text = stringResource(R.string.cancel),
                fontSize = 11.sp,
                modifier = Modifier.size(48.dp, 40.dp),
                radius = 10.dp,
                color = colorScheme.secondary,
                onClick = {
                    scope.launch {
                        status.value = Status.CLOSE
                        offsetX.animateTo(0f)
                    }
                }
            )
        }
    }

    ColorPickerDialog(
        title = title,
        fColor = colorState.value,
        showDialog = showDialog
    ) {
        colorState.value = it
        SPUtils.putString(key, it.toHex())
    }
}