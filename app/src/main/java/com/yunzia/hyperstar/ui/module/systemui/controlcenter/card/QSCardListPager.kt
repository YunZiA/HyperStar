package com.yunzia.hyperstar.ui.module.systemui.controlcenter.card

import android.content.Context
import android.util.Log
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import yunzia.ui.DraggableGrid
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.ModuleNavPagers
import com.yunzia.hyperstar.ui.base.TopButton
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.ui.base.modifier.elevation
import com.yunzia.hyperstar.utils.SPUtils
import com.yunzia.hyperstar.utils.Utils
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.squircleshape.SquircleShape

data class Card(
    val id : Int,
    val tag: String,
    val name : String,
)

private fun initData(context:Context, cardMap: MutableMap<String, Card>, cardList:()-> List<Card>):List<Card> {

    val cardTagList = context.resources.getStringArray(R.array.card_list)
    val cardNameList = context.resources.getStringArray(R.array.card_tile_name)

    val list = emptyList<Card>().toMutableList()

    for (index in cardTagList.indices) {

        val tag = cardTagList[index]
        cardMap[tag] = Card(index,tag,cardNameList[index])
        list.add(Card(index,tag,cardNameList[index]))

    }
    val cardLists = cardList.invoke()
    list.removeAll(cardLists.toMutableList())
    return list
    //cardLists = list


}

private fun getList(cardMap:MutableMap<String, Card>):List<Card> {

    val mCardStyleTiles = SPUtils.getString("card_tile_list","")

    Log.d("ggc",mCardStyleTiles)
    if (mCardStyleTiles == ""){
        return listOf(cardMap.getValue("wifi"),cardMap.getValue("cell"))
    }
    val listFromString: List<String> = mCardStyleTiles.split("|")

    val cardLists =  emptyList<Card>().toMutableList()
    for (tag in listFromString){
        if (tag.isEmpty()){
            break
        }

        cardLists.add(cardMap.getValue(tag))
    }
    return  cardLists



}

private fun saveList(items: List<Card>) {
    val builder = StringBuilder()
    for (tile in items) {
        builder.append(tile.tag).append("|")
    }
    val mCardStyleTiles = builder.toString()

    //cardList = items

    SPUtils.setString("card_tile_list",mCardStyleTiles)

}

@Composable
fun QSCardListPager(
    navController: NavController
) {
    val mContext = navController.context
    val cardMap = mutableMapOf<String, Card>()
    var items by remember { mutableStateOf(emptyList<Card>()) }
    var lastitems by remember { mutableStateOf(emptyList<Card>()) }
    var itemList by remember { mutableStateOf(emptyList<Card>()) }

    LaunchedEffect(Unit) {
        itemList = initData(mContext,cardMap){
            items = getList(cardMap)
            lastitems = items

            items
        }
    }


    val view = LocalView.current

    ModuleNavPagers(
        activityTitle = stringResource(R.string.card_tile_edit),
        navController = navController,
        endIcon = {

            AnimatedVisibility(
                visible = (items != lastitems),
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()

            ) {
//                .padding(end = 12.dp)
                TopButton(
                    imageVector = ImageVector.vectorResource(R.drawable.save2),
                    contentDescription = "save",
                    tint = colorScheme.primary
                ){
                    saveList(items)
                    lastitems = items
                }


            }


        },
        endClick = {
            Utils.rootShell("killall com.android.systemui")
        },
    ){

            firstClasses(
                title = R.string.card_list_header_title,
                summary = R.string.card_list_header_sub_title
            ) {
                DraggableGrid(
                    modifier = Modifier
                        //.height(getHeight(items.size, 94.dp, 20.dp))
                        .heightIn(0.dp, 1340.dp)
                        .fillMaxWidth(),
                    items = items,
                    column = 2,
                    itemMargin = DpSize(4.dp,4.dp),
                    itemKey = { _, item -> item.id },
                    onMove = { dragingIndex, targetIndex ->
                        val mutableList = items.toMutableList().apply{
                            add(targetIndex, removeAt(dragingIndex))  // 交换位置
                        }
                        items = mutableList  // 更新状态，触发动画
                    }
                ) { index,item, isDragging ->

                    CardItem(
                        R.drawable.ic_qs_tile_mark_remove,
                        item,
                        show =
                            if (items.size > 1){
                                true
                            }else{
                                false
                            }

                    ){
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        if (items.size <= 1){

                            Toast.makeText(mContext,
                                mContext.getString(R.string.delete_warning_toast_description),
                                Toast.LENGTH_SHORT).show()

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
                title = R.string.card_list_no_add_title
            ) {
                LazyVerticalGrid(
                    modifier = Modifier
                        //.height(getHeight(itemList.size,94.dp , 24.dp))
                        .heightIn(0.dp,1270.dp),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = stringResource(R.string.empty_list_description),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            color = colorScheme.onBackgroundVariant,
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
    show : Boolean = true,
    clickable : () -> Unit
) {
    Box(
        modifier = Modifier
            .height(85.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 4.dp, horizontal = 4.dp)
                .elevation(
                    shape = SquircleShape(18.dp),
                    backgroundColor = colorScheme.secondary,
                    shadowElevation = 3f
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .basicMarquee(),//.basicMarquee(),
                text = item.name,
                maxLines = 1,
                textAlign = TextAlign.Center,
                //overflow = TextOverflow.Ellipsis,
                softWrap = false,
                fontWeight = FontWeight(550),
                color = colorScheme.onSurfaceVariantSummary
            )

        }

        AnimatedVisibility (
            show,
            modifier = Modifier
                .align(Alignment.TopEnd),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ){
            Image(
                modifier = Modifier
                    .size(25.dp)
                    .clickable(onClick = clickable),

                painter = painterResource(id = resId), contentDescription = "add"
            )

        }

    }
}