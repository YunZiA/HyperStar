package com.yunzia.hyperstar.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.component.modifier.bounceAnim
import com.yunzia.hyperstar.utils.SPUtils
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.extra.DropDownMode
import top.yukonga.miuix.kmp.extra.SuperDropdown

@Composable
fun XSuperDropdown(
    key : String,
    option: Int,
    dfOpt : Int = 0,
    title : String,
    enabled: Boolean = true,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    summary : String ?= null,
    selectedIndex : MutableIntState = remember { mutableIntStateOf(SPUtils.getInt(key,dfOpt)) }
) {

    val dropdownOptions = stringArrayResource(id = option).toList()

    SuperDropdown(
        modifier = Modifier.bounceAnim(enabled),
        title = title,
        summary = summary,
        items = dropdownOptions,
        mode = DropDownMode.AlwaysOnRight,
        enabled = enabled,
        insideMargin = insideMargin,
        selectedIndex = selectedIndex.intValue,
        onSelectedIndexChange = { newOption ->
            selectedIndex.intValue = newOption
            SPUtils.setInt(key,newOption)}
    )
}

@Composable
fun XSuperDialogDropdown(
    key : String,
    option: Int,
    dfOpt : Int = 0,
    title : String,
    enabled: Boolean = true,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    summary : String ?= null,
    selectedIndex : MutableIntState = remember { mutableIntStateOf(SPUtils.getInt(key,dfOpt)) }
) {

    val dropdownOptions = stringArrayResource(id = option).toList()

    SuperDropdown(
        modifier = Modifier.bounceAnim(enabled),
        title = title,
        summary = summary,
        items = dropdownOptions,
        mode = DropDownMode.AlwaysOnRight,
        enabled = enabled,
        insideMargin = insideMargin,
        selectedIndex = selectedIndex.intValue,
        onSelectedIndexChange = { newOption ->
            selectedIndex.intValue = newOption
            SPUtils.setInt(key,newOption)
        }
    )

}

@Composable
fun XMiuixContentDropdown(
    title : String,
    option: Int,
    key : String,
    showOption : Int,
    summary : String ?= null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    content: @Composable (() -> Unit),
) {

    val dropdownOptions = stringArrayResource(id = option).toList()
    val dropdownSelectedOption = remember { mutableStateOf(SPUtils.getInt(key,0)) }


    SuperDropdown(
        modifier = Modifier.bounceAnim(),
        title = title,
        summary = summary,
        items = dropdownOptions,
        mode = DropDownMode.AlwaysOnRight,
        insideMargin = insideMargin,
        selectedIndex = dropdownSelectedOption.value,
        onSelectedIndexChange = { newOption ->
            dropdownSelectedOption.value = newOption
            SPUtils.setInt(key,newOption)
        }
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
fun XMiuixContentDropdown(
    title : String,
    option: Int,
    key : String,
    showOption : Int,
    showOptions : Int,
    summary : String ?= null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    contents: @Composable (() -> Unit),
    content: @Composable (() -> Unit),
) {

    val dropdownOptions = stringArrayResource(id = option).toList()
    val dropdownSelectedOption = remember { mutableStateOf(SPUtils.getInt(key,0)) }


    SuperDropdown(
        modifier = Modifier.bounceAnim(),
        title = title,
        summary = summary,
        items = dropdownOptions,
        mode = DropDownMode.AlwaysOnRight,
        insideMargin = insideMargin,
        selectedIndex = dropdownSelectedOption.value,
        onSelectedIndexChange = { newOption ->
            dropdownSelectedOption.value = newOption
            SPUtils.setInt(key,newOption)
        }
    )

    AnimatedVisibility (
        (dropdownSelectedOption.value == showOptions),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column{
            contents()

        }
    }

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
fun SuperContentDropdown(
    title : String,
    option: Int,
    key : String,
    summary : String ?= null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    content: @Composable ((MutableState<Int>) -> Unit),
) {

    val dropdownOptions = stringArrayResource(id = option).toList()
    val dropdownSelectedOption = remember { mutableStateOf(SPUtils.getInt(key,0)) }
//    popupHorizontalPadding+insideMargin.width/2
    SuperDropdown(
        modifier = Modifier.bounceAnim(),
        title = title,
        summary = summary,
        items = dropdownOptions,
        mode = DropDownMode.AlwaysOnRight,
        insideMargin = insideMargin,
        selectedIndex = dropdownSelectedOption.value,
        onSelectedIndexChange = { newOption ->
            dropdownSelectedOption.value = newOption
            SPUtils.setInt(key,newOption)
        }
    )
    content(dropdownSelectedOption)

//    AnimatedVisibility (
//        (dropdownSelectedOption.value == showOption),
//        enter = fadeIn() + expandVertically(),
//        exit = fadeOut() + shrinkVertically()
//    ) {
//        Column{
//            content()
//
//        }
//    }
}

@Composable
fun PMiuixSuperDropdown(
    title: String,
    option: Int,
    summary : String ?= null,
    selectedIndex: Int,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    onSelectedIndexChange: (Int) -> Unit
) {

    val dropdownOptions = stringArrayResource(id = option).toList()

    SuperDropdown(
        modifier = Modifier.bounceAnim(),
        title = title,
        summary = summary,
        items = dropdownOptions,
        mode = DropDownMode.AlwaysOnRight,
        selectedIndex = selectedIndex,
        insideMargin = insideMargin,
        onSelectedIndexChange =
        { newOption ->
            onSelectedIndexChange(newOption)
        }

    )

}