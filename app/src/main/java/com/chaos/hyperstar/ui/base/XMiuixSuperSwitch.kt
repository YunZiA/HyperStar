package com.chaos.hyperstar.ui.base

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.chaos.hyperstar.ui.base.enums.EventState
import com.chaos.hyperstar.utils.PreferencesUtil
import com.chaos.hyperstar.utils.SPUtils
import top.yukonga.miuix.kmp.MiuixSuperSwitch

@Composable
fun XMiuixSuperSwitch(
    title : String,
    summary: String? = null,
    key : String,
    state : MutableState<Boolean> = remember { mutableStateOf(SPUtils.getBoolean(key,false)) }
) {

    MiuixSuperSwitch(
        modifier = Modifier.bounceClick(),
        title = title,
        summary = summary,
        checked = state.value,
        onCheckedChange = {
            state.value = it
            SPUtils.setBoolean(key,state.value)
        },
    )
}

@Composable
fun PMiuixSuperSwitch(
    title : String,
    key : String
) {

    var MiuixSuperSwitchState by remember { mutableStateOf(PreferencesUtil.getBoolean(key,false)) }
    MiuixSuperSwitch(
        modifier = Modifier.bounceClick(),
        title = title,
        checked = MiuixSuperSwitchState,
        onCheckedChange = {
            MiuixSuperSwitchState = it
            PreferencesUtil.putBoolean(key,MiuixSuperSwitchState)
        },
    )
}

@Composable
fun PMiuixSuperSwitch(
    title : String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean = true
) {
    val updatedOnCheckedChange by rememberUpdatedState(onCheckedChange)

    MiuixSuperSwitch(
        modifier = Modifier.bounceClick(),
        title = title,
        checked = checked,
        onCheckedChange = {
            updatedOnCheckedChange?.invoke(it)
        },
    )
}