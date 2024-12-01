package com.yunzia.hyperstar.ui.module.systemui.controlcenter

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import yunzia.ui.Card
import yunzia.ui.DraggableGrids
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.ModuleNavPagers
import com.yunzia.hyperstar.ui.base.TopButton
import com.yunzia.hyperstar.ui.base.XMiuixSlider
import com.yunzia.hyperstar.ui.base.XSuperDropdown
import com.yunzia.hyperstar.ui.base.XSuperSwitch
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.item.BrightnessItem
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.item.CardItem
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.item.DeviceCenterItem
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.item.DeviceControlItem
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.item.EditItem
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.item.ListItem
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.item.MediaItem
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.item.VolumeItem
import com.yunzia.hyperstar.utils.SPUtils
import com.yunzia.hyperstar.utils.Utils
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Switch
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

private fun getLists(): List<String> {

    val cardPriority = Pair("cards", SPUtils.getFloat("cards_priority", 30f))
    val mediaPriority = Pair("media", SPUtils.getFloat("media_priority", 31f))
    val brightnessPriority = Pair("brightness", SPUtils.getFloat("brightness_priority", 32f))
    val volumePriority = Pair("volume", SPUtils.getFloat("volume_priority", 33f))
    val deviceControlPriority = Pair("deviceControl", SPUtils.getFloat("deviceControl_priority", 34f))
    val deviceCenterPriority = Pair("deviceCenter", SPUtils.getFloat("deviceCenter_priority", 35f))
    val listPriority = Pair("list", SPUtils.getFloat("list_priority", 36f))
    val editPriority = Pair("edit", SPUtils.getFloat("edit_priority", 37f))

    // 将这些 Pair 放入一个列表中
    val prioritiesList = listOf(cardPriority, mediaPriority, brightnessPriority, volumePriority, deviceControlPriority, deviceCenterPriority, listPriority, editPriority)

    // 按照浮点数值的大小排序
    val sortedPriorities = prioritiesList.sortedBy { it.second }

    val c = emptyList<String>().toMutableList()

    // 打印排序后的结果
    sortedPriorities.forEach {
        c.add(it.first)
    }
    return c

}

fun setLists(list: List<String>){

    list.forEachIndexed { index, s ->
        SPUtils.setFloat(s+"_priority",30f+index)

    }

}

private fun initData(context: Context, itemLists: List<String>) : List<Card> {

    val cardTagList = context.resources.getStringArray(R.array.control_center_item_list)
    val cardNameList = context.resources.getStringArray(R.array.control_center_item_list_name)

    val list = emptyList<Card>().toMutableList()
    val cardMap = mutableMapOf<String, String>()

    cardTagList.forEachIndexed { index, s ->
        cardMap[s] = cardNameList[index]
    }

    itemLists.forEachIndexed { index, value ->
        val tag = value

        var mColumn = 4

        when(tag){
            "cards" ->{
                mColumn = 4
            }
            "media" ->{
                mColumn = 2
            }
            "brightness", "volume" ->{
                mColumn = 1
            }
            "deviceControl"->{
                mColumn = 4
            }
            "deviceCenter"->{
                mColumn = 4
            }
            "list"->{
                mColumn = 4
            }
            "edit"->{
                mColumn = 4
            }
        }

        list.add(
            Card(index, tag, mColumn, cardMap.getValue(value))
        )
    }

    return list

}

@Composable
fun ControlCenterListPager(
    navController: NavController
) {

    var itemList by remember { mutableStateOf(emptyList<String>()) }
    val items = remember { mutableStateOf(emptyList<Card>()) }
    var lastitems by remember { mutableStateOf(emptyList<String>()) }

    val switch = remember {
        mutableStateOf(SPUtils.getBoolean("controlCenter_priority_enable",false))
    }

    LaunchedEffect(Unit) {
        itemList = getLists()
        lastitems = itemList
        items.value = initData(navController.context,itemList)


    }

    LaunchedEffect(items.value) {

        val just = emptyList<String>().toMutableList()

        for (i in items.value){
            just.add(i.tag)
        }

    }

    ModuleNavPagers(
        activityTitle = stringResource(R.string.control_center_edit),
        navController = navController,
        endIcon = {

            AnimatedVisibility(
                visible = (itemList != lastitems),
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()

            ) {
//                .padding(end = 12.dp)
                TopButton(
                    imageVector = ImageVector.vectorResource(R.drawable.save2),
                    contentDescription = "save",
                    tint = colorScheme.primary
                ){
                    setLists(itemList)
                    lastitems = itemList
                }


            }


        },
        endClick = {
            //view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            Utils.rootShell("killall com.android.systemui")
        },
    ){
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                insideMargin =  PaddingValues(0.dp,14.dp),
                cornerRadius = 21.dp
            ) {

                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 10.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

//                    ③
                    Text(
                        text = stringResource(R.string.control_center_edit_summary),
                        modifier = Modifier.weight(1f),
                        fontSize = 11.sp,
                        lineHeight =  1.5.em,
                        color = colorResource(R.color.class_name_color),
                        fontWeight = FontWeight.Medium
                    )

                    Switch(
                        checked = switch.value,
                        onCheckedChange = {
                            switch.value = !switch.value
                            SPUtils.setBoolean("controlCenter_priority_enable",switch.value)
                        },
                        enabled = true
                    )

                }
                DraggableGrids(
                    modifier = Modifier
                        //.height(800.dp)
                        .heightIn(640.dp, 1090.dp)
                        .fillMaxWidth(),
                    items = items.value,
                    column = 4,
                    userScrollEnabled = false,
                    itemMargin = DpSize(4.dp,4.dp),
                    itemKey = { index, item -> item.id },
                    onMove = { dragingIndex, targetIndex ->
                        val mutableList = items.value.toMutableList().apply{
                            add(targetIndex, removeAt(dragingIndex))  // 交换位置
                        }
                        items.value = mutableList  // 更新状态，触发动画
                        val mutableLists = itemList.toMutableList().apply{
                            add(targetIndex, removeAt(dragingIndex))  // 交换位置
                        }
                        itemList = mutableLists
                    }
                ) { index,item, isDragging ->

                    when(item.tag){
                        "cards" ->{
                            CardItem(items,index,item)
                        }
                        "media" ->{
                            MediaItem(item)
                        }
                        "brightness"->{
                            BrightnessItem(item)
                        }
                        "volume" ->{
                            VolumeItem(item)
                        }
                        "deviceControl"->{
                            DeviceControlItem(items,index,item)
                        }
                        "deviceCenter"->{
                            DeviceCenterItem(items,index,item)
                        }
                        "list"->{
                            ListItem(item)
                        }
                        "edit"->{
                            EditItem(items,index,item)
                        }

                        else -> {90.dp}
                    }


                }


            }
        }


    }
}

fun getHeight(
    size: Int,
    itemHeight: Dp,
    padding: Dp
): Dp {
    if (size == 0){
        return 0.dp
    }

    var num = size/2
    val remainder = size % 2
    if (remainder != 0){
        num+=1
    }

    return itemHeight * num + padding

}

val insideMargin  =  PaddingValues(16.dp, 16.dp)

@Composable
fun EnableItemDropdown(
    key: String,
    dfOpt: Int = 0,
){

    val state = remember { mutableStateOf(SPUtils.getBoolean("${key}_enable",false)) }

    XSuperSwitch(
        title = stringResource(R.string.enable),
        key = "${key}_enable",
        state = state,
        insideMargin = insideMargin
    )

    XSuperDropdown(
        title = stringResource(R.string.land_rightOrLeft),
        enabled = state.value,
        insideMargin = insideMargin,
        key = key,
        dfOpt = dfOpt,
        option = R.array.land_rightOrLeft_entire

    )

//    XSuperDialogDropdown(
//        title = stringResource(R.string.land_rightOrLeft),
//        enabled = state.value,
//        insideMargin = insideMargin,
//        key = key,
//        dfOpt = dfOpt,
//        option = R.array.land_rightOrLeft_entire
//    )


}

@Composable
fun EnableItemSlider(
    key: String,
    state: MutableState<Boolean>,
    progress : Float,
    progressState: MutableFloatState,
){

    //val state = remember { mutableStateOf(SPUtils.getBoolean("${key}_enable",false)) }

    //onStateChanged(state.value)

    XSuperSwitch(
        title = stringResource(R.string.enable),
        key = "${key}_enable",
        state = state,
        insideMargin = insideMargin
    )

    XMiuixSlider(
        title = stringResource(R.string.span_size),
        key = key,
        isDialog = true,
        enabled = state.value,
        paddingValues = insideMargin,
        maxValue = 4f,
        minValue = 1f,
        defValue = progress,
        progress = progressState
    )

}


