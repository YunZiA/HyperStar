package com.chaos.hyperstar.ui.module.controlcenter

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import chaos.ui.DraggableGrids
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ModulePagers
import com.chaos.hyperstar.ui.base.TopButton
import com.chaos.hyperstar.ui.base.XMiuixSlider
import com.chaos.hyperstar.ui.base.XSuperDialogDropdown
import com.chaos.hyperstar.ui.base.XSuperSwitch
import com.chaos.hyperstar.ui.module.controlcenter.item.BrightnessItem
import com.chaos.hyperstar.ui.module.controlcenter.item.CardItem
import com.chaos.hyperstar.ui.module.controlcenter.item.DeviceCenterItem
import com.chaos.hyperstar.ui.module.controlcenter.item.DeviceControlItem
import com.chaos.hyperstar.ui.module.controlcenter.item.EditItem
import com.chaos.hyperstar.ui.module.controlcenter.item.ListItem
import com.chaos.hyperstar.ui.module.controlcenter.item.MediaItem
import com.chaos.hyperstar.ui.module.controlcenter.item.VolumeItem
import com.chaos.hyperstar.utils.SPUtils
import com.chaos.hyperstar.utils.Utils
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Switch
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
fun ControlCenterListPager(
    activity: ControlCenterListSettings,
) {

    val items = remember { mutableStateOf(activity.cardList) }
    var itemList by remember { mutableStateOf(activity.itemLists) }
    var lastitems by remember { mutableStateOf(activity.itemLists) }

    val switch = remember {
        mutableStateOf(SPUtils.getBoolean("controlCenter_priority_enable",false))
    }


    val view = LocalView.current
    LaunchedEffect(items.value) {

        val just = emptyList<String>().toMutableList()

        for (i in items.value){
            just.add(i.tag)
        }

    }

    ModulePagers(
        activityTitle = stringResource(R.string.control_center_edit),
        activity = activity,
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
                    activity.setLists(itemList)
                    lastitems = itemList
                }
//                Box(
//                    Modifier
//                        //.padding(10.dp)
//                        .clickable {
//                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
//                        activity.setLists(itemList)
//                        lastitems = itemList
//
//                    }
//                ){
//                    Icon(
//                        ImageVector.vectorResource(R.drawable.save2),
//                        modifier = Modifier.padding(6.dp),
//                        contentDescription = "save",
//                        tint = colorScheme.primary)
//                }
//                IconButton(
//                    modifier = Modifier,
//                    onClick = {
//                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
//                        activity.setLists(itemList)
//                        lastitems = itemList
//                    }
//                ) {
//
//                    Icon(
//                        ImageVector.vectorResource(R.drawable.save2),
//                        contentDescription = "save",
//                        tint = colorScheme.primary)
//
//                }

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
                insideMargin = DpSize(0.dp,14.dp),
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

val insideMargin  = DpSize(16.dp, 16.dp)

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

    XSuperDialogDropdown(
        title = stringResource(R.string.land_rightOrLeft),
        enabled = state.value,
        popupHorizontalPadding = 38.dp,
        insideMargin = insideMargin,
        key = key,
        dfOpt = dfOpt,
        option = R.array.land_rightOrLeft_entire
    )


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
        enabled = state.value,
        insideMargin = insideMargin,
        maxValue = 4f,
        minValue = 1f,
        progress = progress,
        x_progress = progressState
    )

}


