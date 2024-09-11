package com.chaos.hyperstar.ui.base

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.chaos.hyperstar.ui.base.enums.EventState
import com.chaos.hyperstar.utils.SPUtils
import top.yukonga.miuix.kmp.MiuixSuperSwitch

@Composable
fun XMiuixSuperSliderSwitch(
    switchTitle : String,
    switchSummary: String? = null,
    switchKey : String,
    title : String,
    key : String,
    unit :String = "",
    minValue: Float = 0f,
    maxValue: Float = 1f,
    progress: Float = 0.5f,
    decimalPlaces : Int = 0
) {
    var MiuixSuperSwitchState by remember { mutableStateOf(SPUtils.getBoolean(switchKey,false)) }

    //var MiuixSuperProgressState by remember { mutableStateOf(SPUtils.getFloat(key,progress)) }

    MiuixSuperSwitch(
        modifier = Modifier.bounceClick(),
        title = switchTitle,
        summary = switchSummary,
        checked = MiuixSuperSwitchState,
        onCheckedChange = {
            MiuixSuperSwitchState = it
            SPUtils.setBoolean(switchKey,MiuixSuperSwitchState)
        },
    )

    AnimatedVisibility (
        MiuixSuperSwitchState,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        XMiuixSlider(
            title = title,
            key = key,
            progress = progress,
            maxValue = maxValue,
            minValue = minValue,
            unit = unit,
            decimalPlaces = decimalPlaces
        )
    }


}

@Composable
fun XMiuixContentSwitch(
    switchTitle : String,
    switchKey : String,
    content: @Composable (() -> Unit),
) {
    var MiuixSuperSwitchState by remember { mutableStateOf(SPUtils.getBoolean(switchKey,false)) }

    MiuixSuperSwitch(
        modifier = Modifier.bounceClick(),
        title = switchTitle,
        checked = MiuixSuperSwitchState,
        onCheckedChange = {
            MiuixSuperSwitchState = it
            SPUtils.setBoolean(switchKey,MiuixSuperSwitchState)
        },
    )
    AnimatedVisibility (
        MiuixSuperSwitchState,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column{
            content()

        }
    }


}