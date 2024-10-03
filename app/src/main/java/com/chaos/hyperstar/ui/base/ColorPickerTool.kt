package com.chaos.hyperstar.ui.base

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import chaos.colorpicker.ColorPickerDialog
import chaos.colorpicker.colorFromHex
import chaos.colorpicker.toHex
import com.chaos.hyperstar.R
import com.chaos.hyperstar.utils.SPUtils
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.createRipple
import top.yukonga.miuix.kmp.utils.squircleshape.CornerSmoothing
import top.yukonga.miuix.kmp.utils.squircleshape.SquircleShape
import kotlin.math.roundToInt

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

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun ColorPickerTool(
    title:String,
    dfColor:Color = Color.Transparent,
    key : String

) {

    val view = LocalView.current
    val swipeableState = rememberSwipeableState(Status.CLOSE)
    val squareSize = 125
    val sizePx = with(LocalDensity.current) { -squareSize.dp.toPx() }
    val anchors = mapOf(0f to Status.CLOSE, sizePx to Status.OPEN)
    val scope = rememberCoroutineScope()

    val interactionSource =  remember { MutableInteractionSource() }
    val indication = createRipple()
    val showDialog = remember { mutableStateOf(false) }
    val color = remember {  mutableStateOf(getDefaultColor(dfColor,key)) }
    val insideMargin = remember { DpSize(24.dp, 10.dp) }
    val paddingModifier = remember(insideMargin) {
        Modifier.padding(horizontal = insideMargin.width, vertical = insideMargin.height)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal,
                //enabled = false
            )
            .clickable(
                enabled = if (swipeableState.targetValue == Status.CLOSE) true else false,
                interactionSource = interactionSource,
                indication = indication
            ) {
                showDialog.value = true
            }
            .then(paddingModifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurface
        )

        Box(
            modifier = Modifier
                .graphicsLayer(
                    shadowElevation = 12f,
                    shape = RoundedCornerShape(30.dp),
                    clip = false
                )
                .pointerInput(Unit) {
                    detectTapGestures{
                        if (swipeableState.targetValue == Status.CLOSE){
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            showDialog.value = true

                        }
                    }

                }
                .border(3.dp, Color.White, RoundedCornerShape(30.dp))
                .size(30.dp)
                .clip(RoundedCornerShape(30.dp)),
            contentAlignment = Alignment.Center

        ){
            Image(
                modifier = Modifier
                    .size(27.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .border(
                        3.dp,
                        if (color.value == colorScheme.surfaceVariant) colorScheme.secondaryContainer else Color.Transparent,
                        RoundedCornerShape(30.dp)
                    ),
                imageVector = ImageVector.vectorResource(R.drawable.transparent),
                colorFilter = ColorFilter.tint(
                    color.value,
                    BlendMode.SrcOver
                ),
                contentDescription = "ColorImage"
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .width(((swipeableState.offset.value.roundToInt() / sizePx) * squareSize).dp)
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                modifier = Modifier.size(48.dp, 40.dp)
                    .align(Alignment.CenterVertically),
                colors = ButtonColors(
                    colorScheme.primary,
                    colorScheme.onPrimary,
                    colorScheme.primary,
                    colorScheme.primary
                ),
                contentPadding = PaddingValues(0.dp, 0.dp),
                shape = SquircleShape(18.dp, CornerSmoothing.High),
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    scope.launch {
                        swipeableState.animateTo(Status.CLOSE)
                    }
                    color.value = dfColor
                    SPUtils.setString(key,"null")


                }
            ) {

                Text(
                    fontSize = 12.sp,
                    color = colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold,
                    text = stringResource(R.string.default_it)
                )

            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                modifier = Modifier.size(48.dp, 40.dp)
                    .align(Alignment.CenterVertically),
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    scope.launch {
                        swipeableState.animateTo(Status.CLOSE)
                    }
                    //Toast.makeText()
                },
                colors = ButtonColors(
                    colorScheme.secondary,
                    colorScheme.onPrimary,
                    colorScheme.background,
                    colorScheme.background
                ),
                contentPadding = PaddingValues(0.dp, 0.dp),
                shape = SquircleShape(18.dp, CornerSmoothing.High)
            ) {
                Text(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    text = stringResource(R.string.cancel)
                )

            }
            Spacer(modifier = Modifier.weight(1f))
        }


    }

    ColorPickerDialog(
        title = title,
        fColor = color.value,
        showDialog = showDialog
    ) {
        color.value = it
        SPUtils.setString(key,color.value.toHex())

    }
}