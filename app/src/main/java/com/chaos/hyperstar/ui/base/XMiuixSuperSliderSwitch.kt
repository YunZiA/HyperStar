package com.chaos.hyperstar.ui.base

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.chaos.hyperstar.utils.SPUtils
import top.yukonga.miuix.kmp.MiuixSuperSwitch

@Composable
fun XMiuixSuperSliderSwitch(
    switchTitle : String,
    switchKey : String,
    title : String,
    key : String,
    minValue: Float = 0f,
    maxValue: Float = 1f,
    progress: Float = 0.5f
) {
    var MiuixSuperSwitchState by remember { mutableStateOf(SPUtils.getBoolean(switchKey,false)) }
    MiuixSuperSwitch(
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
        XMiuixSlider(
            title = title,
            key = key,
            progress = progress,
            maxValue = maxValue,
            minValue = minValue
        )
    }


}