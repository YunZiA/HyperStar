package yunzia.ui

import android.annotation.SuppressLint
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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

open class Card(
    val id : Int,
    val tag: String,
    val type : Int,
    val name : String,
)



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
    val state = remember(gridState) {
        GridDragDropState(
            state = gridState,
            onMove = onMove,
            scope = scope
        )
    }
    LaunchedEffect(state) {
        while (true) {
            val diff = state.scrollChannel.receive()
            gridState.scrollBy(diff)
        }
    }
    return state
}

class GridDragDropState internal constructor(
    private val state: LazyGridState,
    private val scope: CoroutineScope,
    private val onMove: (Int, Int) -> Unit,
) {

    //事件通道，辅助LazyVertialGrid整体滑动
    internal val scrollChannel = Channel<Float>()
    //触摸事件偏移的距离，不是触摸位置
    private var draggingItemDraggedDelta by mutableStateOf(Offset.Zero)
    //触摸的item在布局中的偏移位置
    private var draggingItemInitialOffset by mutableStateOf(Offset.Zero)

    //当前被触摸的Item索引
    var draggingItemIndex by mutableStateOf<Int?>(null)
        private set

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
    internal var previousIndexOfDraggedItem by mutableStateOf<Int?>(null)
        private set
    // 辅助 previousIndexOfDraggedItem 进行位置移动
    internal var previousItemOffset = Animatable(Offset.Zero, Offset.VectorConverter)
        private set

    internal fun onDragStart(offset: Offset) {
        state.layoutInfo.visibleItemsInfo
            .firstOrNull { item ->
                /**
                 * 查找当前触摸的Item
                 */
                offset.x.toInt() in item.offset.x..item.offsetEnd.x &&
                        offset.y.toInt() in item.offset.y..item.offsetEnd.y
            }?.also {
                draggingItemIndex = it.index  //当前被触摸Item索引
                draggingItemInitialOffset = it.offset.toOffset()  //当前Item的在布局中的偏移位置

            }
    }

    internal fun onDragInterrupted() {
        if (draggingItemIndex != null) {
            //touch up 或者 touch cancel后保存位置，辅助之前被拖拽的Item通过动画到指定的位置
            previousIndexOfDraggedItem = draggingItemIndex
            val startOffset = draggingItemOffset //目标位置
            scope.launch {
                //启动协程，进行偏移
                previousItemOffset.snapTo(startOffset)
                previousItemOffset.animateTo(
                    Offset.Zero,
                    spring(
                        stiffness = Spring.StiffnessMediumLow,
                        visibilityThreshold = Offset.VisibilityThreshold
                    )
                )
                //snapTo 和 animateTo是suspend函数，因此到这里是执行完成
                previousIndexOfDraggedItem = null
            }
        }
        draggingItemDraggedDelta = Offset.Zero
        draggingItemIndex = null
        draggingItemInitialOffset = Offset.Zero
    }

    internal fun onDrag(offset: Offset) {
        draggingItemDraggedDelta += offset

        //是否检测到Item被拖拽，空白区域的拖拽无效
        val draggingItem = draggingItemLayoutInfo ?: return

        //开始位置，类似传统View的left和top
        val startOffset = draggingItem.offset.toOffset() + draggingItemOffset
        //结束位置，类似传统View的right和bottom
        val endOffset = startOffset + draggingItem.size.toSize()
        //centerX和centerY
        val middleOffset = startOffset + (endOffset - startOffset) / 2f  //运算符重载，计算出中心点

        /**
         * 查找相交的Item，这和RecyclerView的ItemTouchHelper有些区别，后者会先过滤相交
         * 的Item，然后按中心点距离排序，距离越小越优先，排序之后进行打分，偏离的距离越远越优先，
         * 因此，理论上ItemTouchHelper稳定性要高一些，而Compose的灵敏度更高
         */
        val targetItem = state.layoutInfo.visibleItemsInfo.find { item ->
            middleOffset.x.toInt() in item.offset.x..item.offsetEnd.x &&
                    middleOffset.y.toInt() in item.offset.y..item.offsetEnd.y &&
                    draggingItem.index != item.index
        }
        if (targetItem != null) {
            val scrollToIndex = if (targetItem.index == state.firstVisibleItemIndex) {
                draggingItem.index  //如果交换的Item是第一个位置展示，那么需要尝试滑动Grid
            } else if (draggingItem.index == state.firstVisibleItemIndex) {
                targetItem.index  //如果被拖拽的Item是第一个位置展示，那么尝试滑动Grid
            } else {
                null
            }
            if (scrollToIndex != null) {
                scope.launch {
                    // this is needed to neutralize automatic keeping the first item first.
                    state.scrollToItem(scrollToIndex, state.firstVisibleItemScrollOffset)
                    //回调到ViewModel层面，进行数据交换
                    onMove.invoke(draggingItem.index, targetItem.index)
                }
            } else {
                //回调到ViewModel层面，进行数据交换
                onMove.invoke(draggingItem.index, targetItem.index)
            }
            /**
             * 这里不太好理解，这行代码的意思是被拖拽的Item索引已经变了
             * 因此需要重新更新布局信息，而draggingItemIndex是mutableStateOf委托的，设置后会触发状态更新
             */
            draggingItemIndex = targetItem.index
        } else {
            /**
             *  尝试滑动布局
             */
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

