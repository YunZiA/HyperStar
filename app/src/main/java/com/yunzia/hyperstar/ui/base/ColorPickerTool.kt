package com.yunzia.hyperstar.ui.base

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import yunzia.colorpicker.ColorPickerDialog
import yunzia.colorpicker.colorFromHex
import yunzia.colorpicker.toHex
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.utils.SPUtils
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
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

    val insideMargin = remember { DpSize(24.dp, 12.dp) }
    val view = LocalView.current
    val swappableState = rememberSwipeableState(Status.CLOSE)
    val squareSize = 103.dp+insideMargin.width
    val sizePx = with(LocalDensity.current) { -squareSize.toPx() }
    val anchors = mapOf(0f to Status.CLOSE, sizePx to Status.OPEN)
    val scope = rememberCoroutineScope()

    val showDialog = remember { mutableStateOf(false) }
    val color = remember {  mutableStateOf(getDefaultColor(dfColor,key)) }
    val paddingModifier = remember(insideMargin) {
        Modifier.padding(horizontal = insideMargin.width, vertical = insideMargin.height)
    }

    Box(
        Modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .swipeable(
                    state = swappableState,
                    anchors = anchors,
                    thresholds = { _, _ -> FractionalThreshold(0.3f) },
                    orientation = Orientation.Horizontal
                )
                .clickable(
                    enabled = if (swappableState.targetValue == Status.CLOSE) true else false,
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
                    .offset { IntOffset(swappableState.offset.value.roundToInt(), 0) }
                    .graphicsLayer(
                        shadowElevation = 12f,
                        shape = RoundedCornerShape(30.dp),
                        clip = false
                    )
                    .pointerInput(Unit) {
                        detectTapGestures{
                            if (swappableState.targetValue == Status.CLOSE){
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
                            if (color.value == Color.White) Color(0xFFF0F0F0) else Color.Transparent,
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


        }

        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(squareSize)
                .offset {
                    IntOffset((squareSize.toPx()+swappableState.offset.value).toInt(), 0)
                }
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.Start,
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
                        swappableState.animateTo(Status.CLOSE)
                    }
                    color.value = dfColor
                    SPUtils.setString(key,"null")
                }
            )
            Spacer(modifier = Modifier.width(6.dp))
            MiniTextButton(
                text = stringResource(R.string.cancel),
                fontSize = 11.sp,
                modifier = Modifier.size(48.dp, 40.dp),
                radius = 10.dp,
                color = colorScheme.secondary,
                onClick = {
                    scope.launch {
                        swappableState.animateTo(Status.CLOSE)
                    }
                },
            )
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