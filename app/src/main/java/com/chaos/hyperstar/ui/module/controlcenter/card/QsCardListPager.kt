package com.chaos.hyperstar.ui.module.controlcenter.card

import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import chaos.ui.DraggableGrid
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ActivityPagers
import com.chaos.hyperstar.ui.base.classes
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.utils.Utils
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.squircleshape.SquircleShape

@Composable
fun QsCardListPager(
    activity: QsCardListActivity,
) {
    var items by remember { mutableStateOf(activity.cardList) }
    var itemList by remember { mutableStateOf(activity.cardLists) }
    var lastitems by remember { mutableStateOf(activity.cardList) }

    val view = LocalView.current

    ActivityPagers(
        activityTitle = "卡片磁贴编辑",
        activity = activity,
        endIcon = {

            AnimatedVisibility(
                visible = (!items.equals(lastitems)),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()

            ) {
//                .padding(end = 12.dp)
                IconButton(
                    modifier = Modifier,
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        activity.saveList(items)
                        lastitems = items
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
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            Utils.rootShell("killall com.android.systemui")
        },
    ){

            firstClasses(
                title = "已添加",
                summary = "拖拽已添加的开关调整顺序"
            ) {
                DraggableGrid(
                    modifier = Modifier
                        .height(getHeight(items.size, 94.dp, 20.dp))
                        .fillMaxWidth(),
                    items = items,
                    column = 2,
                    itemMargin = DpSize(4.dp,4.dp),
                    itemKey = { index, item -> item.id },
                    onMove = { dragingIndex, targetIndex ->
                        val mutableList = items.toMutableList().apply{
                            add(targetIndex, removeAt(dragingIndex))  // 交换位置
                        }
                        items = mutableList  // 更新状态，触发动画
                    }
                ) { index,item, isDragging ->

                    CardItem(
                        R.drawable.ic_qs_tile_mark_remove,
                        item
                    ){
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        if (items.size <= 1){

                            Toast.makeText(activity,"你们不要再删啦！人家会坏掉的~\n/(ㄒoㄒ)/~~",Toast.LENGTH_SHORT).show()

                            return@CardItem
                        }
                        val lastList = itemList.toMutableList().apply {
                            add(item)  // 交换位置
                        }
                        //activity.cardLists.add(item)
                        itemList = lastList

                        val mutableList = items.toMutableList().apply {
                            removeAt(index)
                        }
                        items = mutableList

                    }

                }
            }

            classes (
                title = "未添加"
            ) {
                LazyVerticalGrid(
                    modifier = Modifier.height(getHeight(itemList.size,94.dp , 24.dp)),
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(24.dp,12.dp),
                    userScrollEnabled = false,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    itemsIndexed(itemList, key = { index, item ->
                        item.id
                    }) { index, item ->

                        CardItem(R.drawable.ic_qs_tile_mark_add, item) {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            val mutableList = items.toMutableList().apply {
                                add(itemList[index])  // 交换位置
                            }
                            items = mutableList
                            val lastList = itemList.toMutableList().apply {
                                removeAt(index)  // 交换位置
                            }

                            itemList = lastList

                        }
                    }
                }
                AnimatedVisibility(
                    visible = (itemList.isEmpty()),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()

                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "已经没有可以添加的了~\no(╥﹏╥)o",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            color = colorResource(R.color.class_name_color),
                            fontWeight = FontWeight.Medium
                        )

                    }

                }
            }




    }

}


fun getHeight(
    size: Int,
    itemHeight: Dp,
    padding: Dp
):Dp {
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
    resId : Int,
    item: Card,
    clickable : () -> Unit
) {
    Box(
        modifier = Modifier
            .height(90.dp)
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
            fontWeight = FontWeight.ExtraBold,
            color = colorScheme.onSurface
        )
        Image(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(25.dp)
                .clickable(onClick = clickable),

            painter = painterResource(id = resId), contentDescription = "add"
        )
    }
}