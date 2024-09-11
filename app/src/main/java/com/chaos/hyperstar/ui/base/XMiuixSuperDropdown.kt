package com.chaos.hyperstar.ui.base

import androidx.activity.ComponentActivity
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
import top.yukonga.miuix.kmp.MiuixSuperDropdown

@Composable
fun XMiuixSuperDropdown(
    key : String,
    option: Int,
    title : String,
    summary : String ?= null,
    activity : ComponentActivity,
    ) {

    val dropdownOptions = activity.resources.getStringArray(option).toList()
    val dropdownSelectedOption = remember { mutableStateOf(SPUtils.getInt(key,0)) }

    MiuixSuperDropdown(
        modifier = Modifier.bounceClick(),
        title = title,
        summary = summary,
        items = dropdownOptions,
        selectedIndex = dropdownSelectedOption.value,
        onSelectedIndexChange = { newOption ->
            dropdownSelectedOption.value = newOption
            SPUtils.setInt(key,newOption)},
        alwaysRight = true,

    )
}

@Composable
fun XMiuixContentDropdown(
    title : String,
    option: Int,
    key : String,
    showOption : Int,
    summary : String ?= null,
    activity : ComponentActivity,
    content: @Composable (() -> Unit),
) {

    val dropdownOptions = activity.resources.getStringArray(option).toList()
    val dropdownSelectedOption = remember { mutableStateOf(SPUtils.getInt(key,0)) }

    MiuixSuperDropdown(
        modifier = Modifier.bounceClick(),
        title = title,
        summary = summary,
        items = dropdownOptions,
        selectedIndex = dropdownSelectedOption.value,
        onSelectedIndexChange = { newOption ->
            dropdownSelectedOption.value = newOption
            SPUtils.setInt(key,newOption)},
        alwaysRight = true,
        )

    AnimatedVisibility (
        (dropdownSelectedOption.value == showOption),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column{
            content()

        }
    }
}

@Composable
fun PMiuixSuperDropdown(
    title: String,
    option: Int,
    summary : String ?= null,
    selectedIndex: Int,
    activity : ComponentActivity,
    onSelectedIndexChange: (Int) -> Unit
) {

    val dropdownOptions = activity.resources.getStringArray(option).toList()

    MiuixSuperDropdown(
        modifier = Modifier.bounceClick(),
        title = title,
        summary = summary,
        items = dropdownOptions,
        selectedIndex = selectedIndex,
        onSelectedIndexChange =
        { newOption ->
            onSelectedIndexChange(newOption)
        },
        alwaysRight = true,

    )

}