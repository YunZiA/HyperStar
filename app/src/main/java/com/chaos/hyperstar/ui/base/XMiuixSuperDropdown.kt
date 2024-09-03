package com.chaos.hyperstar.ui.base

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.chaos.hyperstar.R
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
    //val optionsList = activity.resources.getStringArray(option).toList()

    //val selectedOption = remember { mutableStateOf(optionsList[SPUtils.getInt(key,0)]) }

    val dropdownOptions = activity.resources.getStringArray(option).toList()
    val dropdownSelectedOption = remember { mutableStateOf(SPUtils.getInt(key,0)) }

    MiuixSuperDropdown(
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