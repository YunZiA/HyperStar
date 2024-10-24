package com.yunzia.hyperstar.ui.base

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.base.SuperSwitch
import com.yunzia.hyperstar.ui.base.XMiuixSlider
import com.yunzia.hyperstar.utils.SPUtils

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

    SuperSwitch(
        modifier = Modifier,
        title = switchTitle,
        summary = switchSummary,
        checked = MiuixSuperSwitchState,
        insideMargin = DpSize(24.dp, 16.dp),
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
fun SwitchContentFolder(
    switchTitle: String,
    switchKey: String,
    contrary: Boolean = false,
    insideMargin : DpSize = DpSize(24.dp, 16.dp),
    content: @Composable (() -> Unit),
) {
    var MiuixSuperSwitchState by remember { mutableStateOf(SPUtils.getBoolean(switchKey,false)) }

    SuperSwitch(
        modifier = Modifier,
        title = switchTitle,
        checked = MiuixSuperSwitchState,
        insideMargin = insideMargin,
        onCheckedChange = {
            MiuixSuperSwitchState = it
            SPUtils.setBoolean(switchKey,MiuixSuperSwitchState)
        },
    )

    AnimatedVisibility (
        if (contrary)!MiuixSuperSwitchState else MiuixSuperSwitchState,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column{
            content()

        }
    }


}