package com.chaos.hyperstar.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.chaos.hyperstar.utils.PreferencesUtil
import com.chaos.hyperstar.utils.SPUtils
import top.yukonga.miuix.kmp.MiuixSuperSwitch

@Composable
fun XMiuixSuperSwitch(
    title : String,
    key : String,
    state : MutableState<Boolean> = remember { mutableStateOf(SPUtils.getBoolean(key,false)) }
) {
    MiuixSuperSwitch(
        title = title,
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
        title = title,
        checked = MiuixSuperSwitchState,
        onCheckedChange = {
            MiuixSuperSwitchState = it
            PreferencesUtil.putBoolean(key,MiuixSuperSwitchState)
        },
    )
}