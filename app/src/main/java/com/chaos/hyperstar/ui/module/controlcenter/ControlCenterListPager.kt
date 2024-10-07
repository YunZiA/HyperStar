package com.chaos.hyperstar.ui.module.controlcenter

import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import chaos.ui.Card
import chaos.ui.DraggableGrids
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ActivityPagers
import com.chaos.hyperstar.ui.base.MSuperDialog
import com.chaos.hyperstar.ui.base.XMiuixSlider
//import com.chaos.hyperstar.ui.base.XPopupUtil.Companion.showDialog
import com.chaos.hyperstar.ui.base.XSuperDropdown
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.utils.SPUtils
import com.chaos.hyperstar.utils.Utils
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.showDialog
//import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.showDialog
import top.yukonga.miuix.kmp.utils.squircleshape.SquircleShape

@Composable
fun ControlCenterListPager(
    activity: ControlCenterListSettings,
) {

    val items = remember { mutableStateOf(activity.cardList) }
    var itemList by remember { mutableStateOf(activity.itemLists) }
    var lastitems by remember { mutableStateOf(activity.itemLists) }


    val view = LocalView.current
    LaunchedEffect(items.value) {

        val just = emptyList<String>().toMutableList()

        for (i in items.value){
            just.add(i.tag)
        }
        //itemList = just



    }

    ActivityPagers(
        activityTitle = stringResource(R.string.controlcenter),
        activity = activity,
        endIcon = {

            AnimatedVisibility(
                visible = (itemList != lastitems),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()

            ) {
//                .padding(end = 12.dp)
                IconButton(
                    modifier = Modifier,
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        activity.setLists(itemList)
                        lastitems = itemList
                    }
                ) {

                    Icon(
                        ImageVector.vectorResource(R.drawable.save2),
                        contentDescription = "save",
                        tint = colorScheme.primary)

                }

            }


        },
        endClick = {
            //view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            Utils.rootShell("killall com.android.systemui")
        },
    ){
        firstClasses(
            summary = "①长按拖拽换位\n②双击部分控件即可更改有关属性"
        ) {
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
                        CardItem(items,index,item,view)
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
                        DeviceControlItem(items,index,item,view)
                    }
                    "deviceCenter"->{
                        DeviceCenterItem(items,index,item,view)
                    }
                    "list"->{
                        ListItem(item)
                    }
                    "edit"->{
                        EditItem(items,index,item,view)
                    }

                    else -> {90.dp}
                }

//                CardItem(
//                    item
//                )

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

@Composable
fun CardItem(
    items: MutableState<List<Card>>,
    index: Int,
    item: Card,
    view: View,
) {
//
    val showDialog = remember { mutableStateOf(false) }
    val spanSize =  remember { mutableFloatStateOf(SPUtils.getFloat("cards_span_size", 2f)) }
    val span = remember { mutableIntStateOf(2) }
    LaunchedEffect(showDialog.value) {
        if (showDialog.value){
            return@LaunchedEffect
        }
        var mutableList = emptyList<Card>()
        if (spanSize.floatValue == 1f){
            mutableList = items.value.toMutableList().apply{
                set(index,Card(item.id,item.tag, 2,item.name))  // 交换位置
            }

        }else{
            mutableList = items.value.toMutableList().apply{
                set(index,Card(item.id,item.tag, 4,item.name))  // 交换位置
            }
        }
        span.intValue = spanSize.floatValue.toInt()
        items.value = mutableList
    }
    LazyVerticalGrid(modifier = Modifier
        .fillMaxWidth()
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = {
                    showDialog.value = true
                }
            )
        }
        .heightIn(85.dp, 170.dp),
        columns = GridCells.Fixed(4),
        userScrollEnabled = false
    ) {
        items(2,
            span = {
                GridItemSpan(if (span.intValue != 1) span.intValue else 2)
                //GridItemSpan(2)
            }
        ) {
            Box(
                modifier = Modifier
                    .height(85.dp)
                    .fillMaxWidth(),
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 6.dp, horizontal = 6.dp),
                    shape = SquircleShape(18.dp),
                    color = colorScheme.background,
                    shadowElevation = 3f

                ) {

                }
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = item.name,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurface
                )
            }
        }
    }


    if (showDialog.value){

        showDialog(){
            MSuperDialog(
                title = item.name,
                show = showDialog,
                showAction = true,
                color = colorScheme.background,
                onDismissRequest = {
                    showDialog.value = false
                }
            ) {
                top.yukonga.miuix.kmp.basic.Card() {
                    Column {
                        XSuperDropdown(
                            title = "横屏占用面板",
                            insideMargin = DpSize(15.dp, 15.dp),
                            key = "cards_land_rightOrLeft",
                            dfOpt = 1,
                            option = R.array.land_rightOrLeft_entire,
                        )
                        XMiuixSlider(
                            title = "横向占格",
                            key = "cards_span_size",
                            insideMargin = DpSize(15.dp, 15.dp),
                            maxValue = 4f,
                            minValue = 1f,
                            progress = 2f,
                            x_progress = spanSize
                        )

                    }
                }
            }
        }

    }

}

@Composable
fun MediaItem(
    item: Card,
) {
    val showDialog = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .height(160.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        showDialog.value = true
                    }
                )
            }
            .fillMaxWidth(),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 4.dp, horizontal = 4.dp),
            shape = SquircleShape(18.dp),
            color = colorScheme.background,
            shadowElevation = 3f

        ) {

        }
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = item.name,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurface
        )
    }

    if (showDialog.value){

        showDialog(){
            MSuperDialog(
                title = item.name,
                show = showDialog,
                showAction = true,
                color = colorScheme.background,
                onDismissRequest = {
                    showDialog.value = false
                }
            ) {
                top.yukonga.miuix.kmp.basic.Card() {
                    Column {
                        XSuperDropdown(
                            title = "横屏占用面板",
                            insideMargin = DpSize(15.dp, 15.dp),
                            key = "media_land_rightOrLeft",
                            dfOpt = 1,
                            option = R.array.land_rightOrLeft_entire
                        )

                    }
                }
            }
        }

    }
}

@Composable
fun BrightnessItem(
    item: Card,
) {
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value){

        showDialog(){
            MSuperDialog(
                title = item.name,
                show = showDialog,
                showAction = true,
                color = colorScheme.background,
                onDismissRequest = {
                    showDialog.value = false
                }
            ) {
                top.yukonga.miuix.kmp.basic.Card() {
                    Column {
                        XSuperDropdown(
                            title = "横屏占用面板",
                            insideMargin = DpSize(15.dp, 15.dp),
                            key = "brightness_land_rightOrLeft",
                            dfOpt = 1,
                            option = R.array.land_rightOrLeft_entire
                        )


                    }
                }
            }
        }

    }

    Box(
        modifier = Modifier
            .height(160.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        showDialog.value = true
                    }
                )
            }
            .fillMaxWidth(),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 4.dp, horizontal = 4.dp),
            shape = SquircleShape(18.dp),
            color = colorScheme.background,
            shadowElevation = 3f

        ) {

        }
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = item.name,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurface
        )
    }

}

@Composable
fun VolumeItem(
    item: Card,
) {

    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value){

        showDialog(){
            MSuperDialog(
                title = item.name,
                show = showDialog,
                showAction = true,
                color = colorScheme.background,
                onDismissRequest = {
                    showDialog.value = false
                }
            ) {
                top.yukonga.miuix.kmp.basic.Card() {
                    Column {
                        XSuperDropdown(
                            title = "横屏占用面板",
                            insideMargin = DpSize(15.dp, 15.dp),
                            key = "volume_land_rightOrLeft",
                            dfOpt = 1,
                            option = R.array.land_rightOrLeft_entire
                        )

                    }
                }
            }
        }

    }

    Box(
        modifier = Modifier
            .height(160.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        showDialog.value = true
                    }
                )
            }
            .fillMaxWidth(),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 4.dp, horizontal = 4.dp),
            shape = SquircleShape(18.dp),
            color = colorScheme.background,
            shadowElevation = 3f

        ) {

        }
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = item.name,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurface
        )
    }

}

@Composable
fun DeviceControlItem(
    items: MutableState<List<Card>>,
    index: Int,
    item: Card,
    view: View,
) {

    val showDialog = remember { mutableStateOf(false) }
    val spanSize =  remember { mutableFloatStateOf(SPUtils.getFloat("deviceControl_span_size", 4f)) }

    LaunchedEffect(showDialog.value) {
        if (showDialog.value){
            return@LaunchedEffect
        }
        val mutableList = items.value.toMutableList().apply{
                set(index,Card(item.id,item.tag, spanSize.floatValue.toInt(),item.name))  // 交换位置
            }

        items.value = mutableList
    }
    if (showDialog.value){

        showDialog(){
            MSuperDialog(
                title = item.name,
                show = showDialog,
                showAction = true,
                color = colorScheme.background,
                onDismissRequest = {
                    showDialog.value = false
                }
            ) {
                top.yukonga.miuix.kmp.basic.Card() {
                    Column {
                        XSuperDropdown(
                            title = "横屏占用面板",
                            insideMargin = DpSize(15.dp, 15.dp),
                            key = "deviceControl_land_rightOrLeft",
                            option = R.array.land_rightOrLeft_entire
                        )
                        XMiuixSlider(
                            title = "横向占格",
                            key = "deviceControl_span_size",
                            insideMargin = DpSize(15.dp, 15.dp),
                            x_progress = spanSize,
                            maxValue = 4f,
                            minValue = 1f,
                            progress = 4f
                        )

                    }
                }
            }
        }

    }

    Box(
        modifier = Modifier
            .height(85.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        showDialog.value = true
                    }
                )
            }
            .fillMaxWidth(),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 4.dp, horizontal = 4.dp),
            shape = SquircleShape(18.dp),
            color = colorScheme.background,
            shadowElevation = 3f

        ) {

        }
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = item.name,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurface
        )
    }
}

@Composable
fun DeviceCenterItem(
    items: MutableState<List<Card>>,
    index: Int,
    item: Card,
    view: View,
) {

    val showDialog = remember { mutableStateOf(false) }
    val spanSize =  remember { mutableFloatStateOf(SPUtils.getFloat("deviceCenter_span_size", 4f)) }

    LaunchedEffect(showDialog.value) {
        if (showDialog.value){
            return@LaunchedEffect
        }
        val mutableList = items.value.toMutableList().apply{
            set(index,Card(item.id,item.tag, spanSize.floatValue.toInt(),item.name))  // 交换位置
        }

        items.value = mutableList
    }

    if (showDialog.value){

        showDialog(){
            MSuperDialog(
                title = item.name,
                show = showDialog,
                showAction = true,
                color = colorScheme.background,
                onDismissRequest = {
                    showDialog.value = false
                }
            ) {
                top.yukonga.miuix.kmp.basic.Card() {
                    Column {
                        XSuperDropdown(
                            title = "横屏占用面板",
                            insideMargin = DpSize(15.dp, 15.dp),
                            key = "deviceCenter_land_rightOrLeft",
                            option = R.array.land_rightOrLeft_entire
                        )
                        XMiuixSlider(
                            title = "横向占格",
                            key = "deviceCenter_span_size",
                            insideMargin = DpSize(15.dp, 15.dp),
                            x_progress = spanSize,
                            maxValue = 4f,
                            minValue = 1f,
                            progress = 4f
                        )

                    }
                }
            }
        }

    }

    Box(
        modifier = Modifier
            .height(85.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        showDialog.value = true
                    }
                )
            }
            .fillMaxWidth(),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 4.dp, horizontal = 4.dp),
            shape = SquircleShape(18.dp),
            color = colorScheme.background,
            shadowElevation = 3f

        ) {

        }
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = item.name,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurface
        )
    }
}

@Composable
fun ListItem(
    item: Card,
) {

    val showDialog = remember { mutableStateOf(false) }
    val spanSize =  remember { mutableFloatStateOf(SPUtils.getFloat("list_span_size", 4f)) }
    val span = remember { mutableIntStateOf(1) }

    LaunchedEffect(showDialog.value) {
        if (showDialog.value){
            return@LaunchedEffect
        }
        span.intValue = spanSize.floatValue.toInt()

    }

    if (showDialog.value){

        showDialog(){
            MSuperDialog(
                title = item.name,
                show = showDialog,
                showAction = true,
                color = colorScheme.background,
                onDismissRequest = {
                    showDialog.value = false
                }
            ) {
                top.yukonga.miuix.kmp.basic.Card() {
                    Column {
                        XSuperDropdown(
                            title = "横屏占用面板",
                            insideMargin = DpSize(15.dp, 15.dp),
                            key = "list_land_rightOrLeft",
                            option = R.array.land_rightOrLeft_entire
                        )
                        XMiuixSlider(
                            title = "横向占格",
                            key = "list_span_size",
                            insideMargin = DpSize(15.dp, 15.dp),
                            x_progress = spanSize,
                            maxValue = 4f,
                            minValue = 1f,
                            progress = 1f
                        )

                    }
                }
            }
        }

    }

    LazyVerticalGrid(modifier = Modifier
        .height(170.dp)
        .fillMaxWidth()
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = {
                    showDialog.value = true
                }
            )
        },
        columns = GridCells.Fixed(4),
        userScrollEnabled = false
    ) {
        items(
            8/span.intValue,
            span = {
                GridItemSpan(span.intValue)
            }
        ){
            Box(
                modifier = Modifier
                    .height(85.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .size(85.dp)
                        .padding(vertical = 6.dp, horizontal = 6.dp),
                    shape = SquircleShape(18.dp),
                    color = colorScheme.background,
                    shadowElevation = 3f

                ){

                }
            }
        }
    }
}

@Composable
fun EditItem(
    items: MutableState<List<Card>>,
    index: Int,
    item: Card,
    view: View,
) {

    val showDialog = remember { mutableStateOf(false) }
    val spanSize =  remember { mutableFloatStateOf(SPUtils.getFloat("edit_span_size", 4f)) }

    LaunchedEffect(showDialog.value) {
        if (showDialog.value){
            return@LaunchedEffect
        }
        val mutableList = items.value.toMutableList().apply{
            set(index,Card(item.id,item.tag, spanSize.floatValue.toInt(),item.name))  // 交换位置
        }

        items.value = mutableList
    }

    if (showDialog.value){

        showDialog(){
            MSuperDialog(
                title = item.name,
                show = showDialog,
                showAction = true,
                color = colorScheme.background,
                onDismissRequest = {
                    showDialog.value = false
                }
            ) {
                top.yukonga.miuix.kmp.basic.Card() {
                    Column {
//                        XSuperDropdown(
//                            title = "横屏占用面板",
//                            insideMargin = DpSize(15.dp, 15.dp),
//                            key = "edit_land_rightOrLeft",
//                            option = R.array.land_rightOrLeft_entire
//                        )
                        XMiuixSlider(
                            title = "横向占格",
                            key = "edit_span_size",
                            insideMargin = DpSize(15.dp, 15.dp),
                            x_progress = spanSize,
                            maxValue = 4f,
                            minValue = 1f,
                            progress = 4f
                        )

                    }
                }
            }
        }

    }

    Box(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .width(70.dp)
                .padding(vertical = 4.dp, horizontal = 4.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            showDialog.value = true
                        }
                    )
                },
            shape = SquircleShape(18.dp),
            color = colorScheme.background,
            shadowElevation = 3f

        ) {

        }
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = item.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurface
        )
    }
}

