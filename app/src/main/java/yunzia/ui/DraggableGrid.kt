package yunzia.ui

import android.annotation.SuppressLint
import android.os.Parcelable
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T : Any> DraggableGrid(
    items: List<T>,
    column : Int,
    itemKey:(Int,T) -> Any,
    onMove: (Int, Int) -> Unit,
    itemMargin: DpSize = DpSize(0.dp, 0.dp),
    modifier: Modifier = Modifier,
    userScrollEnabled : Boolean = false,
    content: @Composable (Int,T, Boolean) -> Unit,
) {

    val view = LocalView.current
    // Grid状态，常规做法
    val gridState = rememberLazyGridState()
    //记录拖拽状态
    val dragDropState = rememberGridDragDropState(gridState, onMove)
    LazyVerticalGrid(
        columns = GridCells.Fixed(column),
        modifier = modifier.dragContainer(true, dragDropState, view),
        state = gridState,
        contentPadding = PaddingValues(24.dp,12.dp),
        userScrollEnabled = userScrollEnabled,
        verticalArrangement = Arrangement.spacedBy(itemMargin.height),
        horizontalArrangement = Arrangement.spacedBy(itemMargin.width),
    ) {
        itemsIndexed(
            items = items,
            key = { index, item ->
                itemKey(index,item)
            }
        ) { index, item ->
            DraggableItem(dragDropState, index) { isDragging ->
                content(index,item, isDragging)
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DraggableGrids(
    items: List<Card>,
    column : Int,
    itemKey:(Int,Card) -> Any,
    onMove: (Int, Int) -> Unit,
    itemMargin: DpSize = DpSize(0.dp, 0.dp),
    modifier: Modifier = Modifier,
    userScrollEnabled : Boolean = false,
    content: @Composable (Int,Card, Boolean) -> Unit,
) {
    val view = LocalView.current
    // Grid状态，常规做法
    val gridState = rememberLazyGridState()
    //记录拖拽状态
    val dragDropState = rememberGridDragDropState(gridState, onMove)
    LazyVerticalGrid(
        columns = GridCells.Fixed(column),
        modifier = modifier.dragContainer(true,dragDropState,view),
        state = gridState,
        contentPadding = PaddingValues(24.dp,12.dp),
        userScrollEnabled = userScrollEnabled,
        verticalArrangement = Arrangement.spacedBy(itemMargin.height),
        horizontalArrangement = Arrangement.spacedBy(itemMargin.width),
    ) {
        itemsIndexed(
            items = items,
            span = { index, item ->
                GridItemSpan(item.type)
            },
            key = { index, item ->
                itemKey(index,item)
            }
        ) { index, item ->
            DraggableItem(dragDropState, index) { isDragging ->
                content(index,item, isDragging)
            }
        }
    }
}

@Parcelize
data class Card(
    val id: Int,
    val tag: String,
    val type: Int,
    val name: String
) : Parcelable



//核心方法，事件监听
@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.dragContainer(useDrag: Boolean, dragDropState: GridDragDropState, view: View): Modifier {

    if (useDrag){
        return pointerInput(key1 = dragDropState) {
            detectDragGesturesAfterLongPress(

                onDrag = { change, offset ->
                    change.consume()
                    dragDropState.onDrag(offset = offset)
                },
                onDragStart = { offset ->
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    dragDropState.onDragStart(offset)
                },
                onDragEnd = {
                    dragDropState.onDragInterrupted()
                            },
                onDragCancel = { dragDropState.onDragInterrupted() }
            )
        }

    }else{
        return Modifier

    }
}

@ExperimentalFoundationApi
@Composable
fun LazyGridItemScope.DraggableItem(
    dragDropState: GridDragDropState,
    index: Int,
    content: @Composable (isDragging: Boolean) -> Unit,
) {
    val dragging = index == dragDropState.draggingItemIndex
    val draggingModifier = if (dragging) {
        //被拖拽时
        Modifier
            .zIndex(1f) //防止被遮挡
            .graphicsLayer {
                translationX = dragDropState.draggingItemOffset.x
                translationY = dragDropState.draggingItemOffset.y
            }
    } else if (index == dragDropState.previousIndexOfDraggedItem) {
        //松手后的"回归"动画
        Modifier
            .zIndex(1f)  //防止被遮挡
            .graphicsLayer {
                translationX = dragDropState.previousItemOffset.value.x
                translationY = dragDropState.previousItemOffset.value.y
            }
    } else {
        //idle状态
        Modifier.animateItem()
    }
    Box(modifier = Modifier.then(draggingModifier) , propagateMinConstraints = true) {
        content(dragging)
    }
}

@Composable
fun rememberGridDragDropState(
    gridState: LazyGridState,
    onMove: (Int, Int) -> Unit,
): GridDragDropState {
    val scope = rememberCoroutineScope()

    // 保存拖拽相关状态
    var savedDraggingIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    var savedPreviousIndex by rememberSaveable { mutableStateOf<Int?>(null) }

    return remember(gridState) {
        GridDragDropState(
            state = gridState,
            onMove = onMove,
            scope = scope,
            initialDraggingIndex = savedDraggingIndex,
            initialPreviousIndex = savedPreviousIndex,
            onStateChange = { draggingIndex, previousIndex ->
                savedDraggingIndex = draggingIndex
                savedPreviousIndex = previousIndex
            }
        )
    }
}

class GridDragDropState internal constructor(
    private val state: LazyGridState,
    private val scope: CoroutineScope,
    private val onMove: (Int, Int) -> Unit,
    initialDraggingIndex: Int? = null,
    initialPreviousIndex: Int? = null,
    private val onStateChange: (Int?, Int?) -> Unit
) {

    //事件通道，辅助LazyVertialGrid整体滑动
    internal val scrollChannel = Channel<Float>()
    //触摸事件偏移的距离，不是触摸位置
    private var draggingItemDraggedDelta by mutableStateOf(Offset.Zero)
    //触摸的item在布局中的偏移位置
    private var draggingItemInitialOffset by mutableStateOf(Offset.Zero)

    //当前被触摸的Item索引
    private var _draggingItemIndex = mutableStateOf(initialDraggingIndex)
    val draggingItemIndex: Int? get() = _draggingItemIndex.value

    /**
     * 这里有2个原因
     * 1.由于会出现数据交换和索引交换，因此需要重新计算draggingItemOffset位置
     * 2.Grid自身也滑动，这里也可以做到矫正
     */
    internal val draggingItemOffset: Offset
        get() = draggingItemLayoutInfo?.let { item ->
            draggingItemInitialOffset + draggingItemDraggedDelta - item.offset.toOffset()
        } ?: Offset.Zero

    //当前被触摸的Item的布局信息
    private val draggingItemLayoutInfo: LazyGridItemInfo?
        get() = state.layoutInfo.visibleItemsInfo
            .firstOrNull {
                it.index == draggingItemIndex
            }
    // touch cancel或者touch up 之后继续保存被拖拽的Item，辅助通过动画方式将其Item偏移到指定位置
    private var _previousIndexOfDraggedItem = mutableStateOf(initialPreviousIndex)
    val previousIndexOfDraggedItem: Int? get() = _previousIndexOfDraggedItem.value

    // 辅助 previousIndexOfDraggedItem 进行位置移动
    internal var previousItemOffset = Animatable(Offset.Zero, Offset.VectorConverter)
        private set

    // 更新索引的方法
    internal fun updateDraggingIndex(index: Int?) {
        _draggingItemIndex.value = index
    }

    internal fun updatePreviousIndex(index: Int?) {
        _previousIndexOfDraggedItem.value = index
    }


    internal fun onDragStart(offset: Offset) {
        state.layoutInfo.visibleItemsInfo
            .firstOrNull { item ->
                offset.x.toInt() in item.offset.x..item.offsetEnd.x &&
                        offset.y.toInt() in item.offset.y..item.offsetEnd.y
            }?.also {
                updateDraggingIndex(it.index)
                draggingItemInitialOffset = it.offset.toOffset()
            }
    }

    internal fun onDragInterrupted() {
        if (draggingItemIndex != null) {
            updatePreviousIndex(draggingItemIndex)
            val startOffset = draggingItemOffset
            scope.launch {
                previousItemOffset.snapTo(startOffset)
                previousItemOffset.animateTo(
                    Offset.Zero,
                    spring(
                        stiffness = Spring.StiffnessMediumLow,
                        visibilityThreshold = Offset.VisibilityThreshold
                    )
                )
                updatePreviousIndex(null)
            }
        }
        draggingItemDraggedDelta = Offset.Zero
        updateDraggingIndex(null)
        draggingItemInitialOffset = Offset.Zero
    }

    internal fun onDrag(offset: Offset) {
        draggingItemDraggedDelta += offset

        val draggingItem = draggingItemLayoutInfo ?: return

        val startOffset = draggingItem.offset.toOffset() + draggingItemOffset
        val endOffset = startOffset + draggingItem.size.toSize()
        val middleOffset = startOffset + (endOffset - startOffset) / 2f

        val targetItem = state.layoutInfo.visibleItemsInfo.find { item ->
            middleOffset.x.toInt() in item.offset.x..item.offsetEnd.x &&
                    middleOffset.y.toInt() in item.offset.y..item.offsetEnd.y &&
                    draggingItem.index != item.index
        }

        if (targetItem != null) {
            val scrollToIndex = if (targetItem.index == state.firstVisibleItemIndex) {
                draggingItem.index
            } else if (draggingItem.index == state.firstVisibleItemIndex) {
                targetItem.index
            } else {
                null
            }
            if (scrollToIndex != null) {
                scope.launch {
                    state.scrollToItem(scrollToIndex, state.firstVisibleItemScrollOffset)
                    onMove.invoke(draggingItem.index, targetItem.index)
                }
            } else {
                onMove.invoke(draggingItem.index, targetItem.index)
            }
            updateDraggingIndex(targetItem.index)
        } else {
            val overscroll = when {
                draggingItemDraggedDelta.y > 0 ->
                    (endOffset.y - state.layoutInfo.viewportEndOffset).coerceAtLeast(0f)
                draggingItemDraggedDelta.y < 0 ->
                    (startOffset.y - state.layoutInfo.viewportStartOffset).coerceAtMost(0f)
                else -> 0f
            }
            if (overscroll != 0f) {
                scrollChannel.trySend(overscroll)
            }
        }
    }

    private val LazyGridItemInfo.offsetEnd: IntOffset
        get() = this.offset + this.size
}

operator fun IntOffset.plus(size: IntSize): IntOffset {
    return IntOffset(x + size.width, y + size.height)
}

operator fun Offset.plus(size: Size): Offset {
    return Offset(x + size.width, y + size.height)
}

