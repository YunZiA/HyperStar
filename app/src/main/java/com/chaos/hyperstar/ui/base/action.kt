package com.chaos.hyperstar.ui.base

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import com.chaos.hyperstar.ui.base.enums.EventState
import top.yukonga.miuix.kmp.MiuixScrollBehavior
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.rememberOverscrollFlingBehavior

fun Modifier.bounceClick() = composed {
    var eventState by remember { mutableStateOf(EventState.Idle) }
    val scale by animateFloatAsState(if (eventState == EventState.Pressed) 0.70f else 1f)

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(eventState) {
            awaitPointerEventScope {
                eventState = if (eventState == EventState.Pressed) {
                    waitForUpOrCancellation()
                    EventState.Idle
                } else {
                    awaitFirstDown(false)
                    EventState.Pressed
                }
            }
        }
}

fun Modifier.scroll(

    scrollLazyColumnState: LazyListState,
    enableOverScroll: Boolean = true,
    topAppBarScrollBehavior: MiuixScrollBehavior? = null,
) = composed {
    val firstModifier = remember(enableOverScroll) {
        Modifier.overScrollVertical(onOverscroll = { topAppBarScrollBehavior?.isPinned = it })

    }
    val finalModifier = remember(topAppBarScrollBehavior) {
        topAppBarScrollBehavior?.let {
            firstModifier.nestedScroll(it.nestedScrollConnection)
        } ?: firstModifier
    }
    this.overScrollVertical(onOverscroll = { topAppBarScrollBehavior?.isPinned = it })
        .nestedScroll(
            topAppBarScrollBehavior!!.nestedScrollConnection
        )
        scrollable(
            flingBehavior = rememberOverscrollFlingBehavior { scrollLazyColumnState },
            state = scrollLazyColumnState,
            orientation =  Orientation.Vertical,
        )

}
