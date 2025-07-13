package com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter

import android.app.Application
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
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.topbar.TopButton
import com.yunzia.hyperstar.ui.component.XMiuixSlider
import com.yunzia.hyperstar.ui.component.XSuperDropdown
import com.yunzia.hyperstar.ui.component.XSuperSwitch
import com.yunzia.hyperstar.ui.component.pager.ModuleNavPagers
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.item.BrightnessItem
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.item.CardItem
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.item.DeviceCenterItem
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.item.DeviceControlItem
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.item.EditItem
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.item.ListItem
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.item.MediaItem
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.item.VolumeItem
import com.yunzia.hyperstar.utils.Helper
import com.yunzia.hyperstar.utils.SPUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Switch
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import yunzia.ui.Card
import yunzia.ui.DraggableGrids


data class ItemState(
    val enable: Boolean = false,
    val spanSize: Float = 2f
){
    companion object {
        fun loadFromSP(tag: String): ItemState {
            val defaultColumn = when(tag) {
                "cards", "deviceControl", "deviceCenter", "list", "edit" -> 4f
                "media" -> 2f
                "brightness", "volume" -> 1f
                else -> 4f
            }
            return ItemState(
                enable = SPUtils.getBoolean("${tag}_span_size_enable", false),
                spanSize = SPUtils.getFloat("${tag}_span_size", defaultColumn)
            )
        }
    }
}


class ControlCenterListViewModel(application: Application) : AndroidViewModel(application) {
    private val _items = MutableStateFlow<List<Card>>(emptyList())
    val items: StateFlow<List<Card>> = _items.asStateFlow()

    private val _orderChanged = MutableStateFlow(false)
    val orderChanged: StateFlow<Boolean> = _orderChanged.asStateFlow()

    private val _switchEnabled = MutableStateFlow(
        SPUtils.getBoolean("controlCenter_priority_enable", false)
    )
    val switchEnabled: StateFlow<Boolean> = _switchEnabled.asStateFlow()

    private var originalOrder: List<String> = emptyList()

    private val _itemStates = MutableStateFlow<Map<String, ItemState>>(emptyMap())
    val itemStates: StateFlow<Map<String, ItemState>> = _itemStates


    fun updateCardItemSpan(
        index: Int,
        item: Card,
        spanSize: Float,
        enable: Boolean,
    ) {
        val currentItems = _items.value.toMutableList()

        // 处理 span 大小逻辑
        val spanIsOne = spanSize == 1f && enable
        currentItems[index] = item.copy(
            type = if (spanIsOne) 2 else 4
        )

        // 更新 items
        _items.value = currentItems
    }

    // 添加新的方法处理 span 更新
    fun updateItemSpan(
        index: Int,
        item: Card,
        spanSize: Float,
        enable: Boolean
    ) {
        val currentItems = _items.value.toMutableList()
        currentItems[index] = if (enable) {
            item.copy(type = spanSize.toInt())
        } else {
            // 根据 tag 设置默认的 column 值
            val defaultColumn = when(item.tag) {
                "cards", "deviceControl", "deviceCenter", "list", "edit" -> 4
                "media" -> 2
                "brightness", "volume" -> 1
                else -> 4
            }
            item.copy(type = defaultColumn)
        }

        _items.value = currentItems
    }

    // 更新指定 item 的 dialog 状态
    fun updateItemDialogState(
        itemTag: String,
        enable: Boolean? = null,
        spanSize: Float? = null
    ) {
        _itemStates.update { currentStates ->
            val currentState = currentStates[itemTag] ?: ItemState.loadFromSP(itemTag)
            val newState = currentState.copy(
                enable = enable ?: currentState.enable,
                spanSize = spanSize ?: currentState.spanSize
            )

            // 更新 SPUtils
            enable?.let { SPUtils.setBoolean("${itemTag}_span_size_enable", it) }
            spanSize?.let { SPUtils.setFloat("${itemTag}_span_size", it) }

            currentStates + (itemTag to newState)

        }
    }

    init {// 初始化时从 SPUtils 加载所有项的状态
        val initialStates = listOf("cards", "media", "brightness", "volume", "deviceControl", "deviceCenter", "list", "edit")
            .associate { tag ->
                tag to ItemState.loadFromSP(tag)
            }
        _itemStates.value = initialStates
        loadInitialData()
    }

    private fun loadInitialData() {
        val itemList = getLists()
        originalOrder = itemList
        viewModelScope.launch {
            _items.value = initData(getApplication(), itemList)
        }
    }

    fun updateSwitch(enabled: Boolean) {
        _switchEnabled.value = enabled
        SPUtils.setBoolean("controlCenter_priority_enable", enabled)
    }

    fun moveItem(fromIndex: Int, toIndex: Int) {
        val currentItems = _items.value.toMutableList()
        currentItems.add(toIndex, currentItems.removeAt(fromIndex))
        _items.value = currentItems

        // 检查顺序是否改变
        val newOrder = currentItems.map { it.tag }
        _orderChanged.value = newOrder != originalOrder
    }

    fun saveOrder() {
        viewModelScope.launch {
            val newOrder = _items.value.map { it.tag }
            setLists(newOrder)
            originalOrder = newOrder
            _orderChanged.value = false
        }
    }

    private fun getLists(): List<String> {
        val priorities = listOf(
            Pair("cards", SPUtils.getFloat("cards_priority", 30f)),
            Pair("media", SPUtils.getFloat("media_priority", 31f)),
            Pair("brightness", SPUtils.getFloat("brightness_priority", 32f)),
            Pair("volume", SPUtils.getFloat("volume_priority", 33f)),
            Pair("deviceControl", SPUtils.getFloat("deviceControl_priority", 34f)),
            Pair("deviceCenter", SPUtils.getFloat("deviceCenter_priority", 35f)),
            Pair("list", SPUtils.getFloat("list_priority", 36f)),
            Pair("edit", SPUtils.getFloat("edit_priority", 37f))
        )

        return priorities.sortedBy { it.second }.map { it.first }
    }

    private fun setLists(list: List<String>) {
        list.forEachIndexed { index, s ->
            SPUtils.setFloat("${s}_priority", 30f + index)
        }
    }

    private fun initData(context: Context, itemLists: List<String>): List<Card> {
        val cardTagList = context.resources.getStringArray(R.array.control_center_item_list)
        val cardNameList = context.resources.getStringArray(R.array.control_center_item_list_name)
        val cardMap = cardTagList.zip(cardNameList).toMap()

        return itemLists.mapIndexed { index, tag ->
            val column = when(tag) {
                "cards", "deviceControl", "deviceCenter", "list", "edit" -> 4
                "media" -> 2
                "brightness", "volume" -> 1
                else -> 4
            }
            Card(index, tag, column, cardMap.getValue(tag))
        }
    }
}


@Composable
fun ControlCenterListScreen(
    navController: NavController,
    currentStartDestination: MutableState<String>
) {

    val viewModel: ControlCenterListViewModel = viewModel()

    val items by viewModel.items.collectAsState()
    val orderChanged by viewModel.orderChanged.collectAsState()
    val switchEnabled by viewModel.switchEnabled.collectAsState()

    ModuleNavPagers(
        activityTitle = stringResource(R.string.control_center_edit),
        parentRoute = currentStartDestination,
        navController = navController,
        endIcon = {

            AnimatedVisibility(
                visible = orderChanged,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()

            ) {
//                .padding(end = 12.dp)
                TopButton(
                    imageVector = ImageVector.vectorResource(R.drawable.save2),
                    contentDescription = "save",
                    tint = colorScheme.primary
                ){
                    viewModel.saveOrder()
                }


            }


        },
        endClick = {
            //view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            Helper.rootShell("killall com.android.systemui")
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
                        color = colorScheme.onBackgroundVariant,
                        fontWeight = FontWeight.Medium
                    )

                    Switch(
                        checked = switchEnabled,
                        onCheckedChange = {
                            viewModel.updateSwitch(it)
                        },
                        enabled = true
                    )

                }
                DraggableGrids(
                    modifier = Modifier
                        //.height(800.dp)
                        .heightIn(640.dp, 1090.dp)
                        .fillMaxWidth(),
                    items = items,
                    column = 4,
                    userScrollEnabled = false,
                    itemMargin = DpSize(4.dp,4.dp),
                    itemKey = { index, item -> item.id },
                    onMove = { dragingIndex, targetIndex ->
                        viewModel.moveItem(dragingIndex, targetIndex)
                    }
                ) { index,item, isDragging ->

                    when(item.tag){
                        "cards" ->{
                            CardItem(index,item,viewModel)
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
                            DeviceControlItem(index,item,viewModel)
                        }
                        "deviceCenter"->{
                            DeviceCenterItem(index,item,viewModel)
                        }
                        "list"->{
                            ListItem(item)
                        }
                        "edit"->{
                            EditItem(index,item,viewModel)
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
    dfOpt: Int = 0
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


