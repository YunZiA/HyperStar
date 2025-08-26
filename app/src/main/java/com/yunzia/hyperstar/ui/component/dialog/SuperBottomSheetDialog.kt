package com.yunzia.hyperstar.ui.component.dialog

import android.util.Half
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.G2RoundedCornerShape
import top.yukonga.miuix.kmp.utils.MiuixPopupUtils.Companion.DialogLayout
import top.yukonga.miuix.kmp.utils.getWindowSize


//object SheetDialogState {
//    val Collapsed = 0.12f  // 收起状态
//    val Half =  0.5f        // 半屏状态
//    val Full = 0.95f       // 全屏状态
//}

enum class SheetDialogState(internal val value: Float) {
    COLLAPSED(0.12f),  // 收起状态（底部露出）
    HALF(0.5f),       // 半屏状态（默认展开）
    FULL(0.95f);      // 全屏状态（最大化）


}


@Composable
fun SuperBottomSheetDialog(
    show: MutableState<Boolean>,
    sheetDialogState:SheetDialogState = SheetDialogState.HALF,
    title: String? = null,
    titleColor: Color = MiuixTheme.colorScheme.onSurface,
    summary: String? = null,
    summaryColor: Color = MiuixTheme.colorScheme.surfaceVariant,
    onFocus: () -> Unit = {},
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val getWindowSize by rememberUpdatedState(getWindowSize())
    val windowWidth by rememberUpdatedState(getWindowSize.width.dp / density.density)
    val windowHeight by rememberUpdatedState(getWindowSize.height.dp / density.density)
    val isLargeScreen by remember { derivedStateOf { (windowHeight >= 480.dp && windowWidth >= 840.dp) } }

    val animationSpec: SpringSpec<Float> = spring(dampingRatio = Spring.DampingRatioLowBouncy,stiffness = 500f)
    BackHandler(enabled = show.value) {
        onDismissRequest()
    }

    DialogLayout(
        visible = show,
        enterTransition =  slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = spring(dampingRatio = 0.95f,stiffness = 500f)
        ),
        exitTransition =  slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = spring(dampingRatio = 0.95f,stiffness = 500f)
        ),
        dimEnterTransition = fadeIn(spring(dampingRatio = 0.95f,stiffness = 500f)),
        dimExitTransition = fadeOut(spring(dampingRatio = 0.95f,stiffness = 500f)),
    ){

        val velocityThreshold = 1000f

        val screenHeight = density.run { windowHeight.toPx() }

        val initialHeight = screenHeight * sheetDialogState.value

        val collapsedHeight = screenHeight * SheetDialogState.COLLAPSED.value
        val halfHeight = screenHeight * SheetDialogState.HALF.value
        val fullHeight = screenHeight * SheetDialogState.FULL.value

        val heightAnim = remember { Animatable(initialHeight) }
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(show.value) {
            if (show.value && heightAnim.value != initialHeight){
                heightAnim.snapTo(initialHeight)
            }
        }


        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onDismissRequest.invoke()
                        }
                    )
                }
        ) {

            Surface(
                modifier = Modifier
                    .then(
                        if (isLargeScreen){
                            Modifier.widthIn(max = 420.dp)
                        } else {
                            Modifier.fillMaxWidth()
                        }
                    )
                    .offset { IntOffset(0, (screenHeight - heightAnim.value).toInt()) }
                    .height(with(density) { fullHeight.toDp() })
                    .clip(
                        G2RoundedCornerShape(
                            topStart = CornerSize(25.dp),
                            topEnd = CornerSize(25.dp),
                            bottomEnd = CornerSize(0.dp),
                            bottomStart = CornerSize(0.dp)
                        )
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                            }
                        )
                    },
                shadowElevation = 8.dp
            ) {
                Column(
                    Modifier.fillMaxSize()
                ) {
                    // 横条
                    Box(
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 12.dp, bottom = 8.dp)
                            .size(width = 40.dp, height = 5.dp)
                            .clip(RoundedCornerShape(2.5.dp))
                            .background(Color.Gray.copy(alpha = 0.5f))
                            .draggable(
                                orientation = Orientation.Vertical,
                                state = rememberDraggableState { delta ->
                                    coroutineScope.launch {
                                        val newHeight = heightAnim.value - delta
                                        heightAnim.snapTo(newHeight.coerceIn(0f, fullHeight))
                                    }
                                },
                                onDragStarted = {},
                                onDragStopped = { velocity ->
                                    coroutineScope.launch {
                                        when {
                                            heightAnim.value > halfHeight -> {
                                                if (velocity > velocityThreshold) {
                                                    heightAnim.animateTo(
                                                        targetValue = halfHeight,
                                                        animationSpec = animationSpec
                                                    )
                                                } else {
                                                    heightAnim.animateTo(
                                                        targetValue = fullHeight,
                                                        animationSpec = animationSpec
                                                    )

                                                }

                                            }

                                            heightAnim.value < halfHeight && heightAnim.value > collapsedHeight -> {
                                                if (velocity > velocityThreshold) {
                                                    heightAnim.animateTo(
                                                        targetValue = collapsedHeight,
                                                        animationSpec = animationSpec
                                                    )

                                                } else {
                                                    heightAnim.animateTo(
                                                        targetValue = halfHeight,
                                                        animationSpec = animationSpec
                                                    )

                                                }

                                            }

                                            heightAnim.value < collapsedHeight -> {
                                                if (velocity > 0f) {
                                                    show.value = false
                                                }
                                            }
                                            else -> {
                                            }
                                        }
                                    }
                                }
                            )
                    )
                    // 内容
                    Column(
                        Modifier.fillMaxSize()
                    ) {
                        content()
                    }
                }
            }
        }


    }
}