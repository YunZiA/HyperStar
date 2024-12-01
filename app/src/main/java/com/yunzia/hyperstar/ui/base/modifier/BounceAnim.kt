package com.yunzia.hyperstar.ui.base.modifier

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import com.yunzia.hyperstar.ui.base.enums.EventState
import com.yunzia.hyperstar.utils.PreferencesUtil
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.rememberOverscrollFlingBehavior




fun Modifier.bounceAnim(
    finishedListener:((Float) -> Unit)? = null
) = composed {
    val enable = PreferencesUtil.getBoolean("bounce_anim_enable",true)
    val eventState = remember { mutableStateOf(EventState.Idle) }
    if (enable){
        this.bounceScale(eventState){
            finishedListener?.invoke(it)
        }
            .bounceClick(eventState)
    }else{
        finishedListener?.invoke(1f)
        this
    }


}


fun Modifier.bounceAnimClickable(
    finishedListener:((Float) -> Unit)? = null
) = composed {

    val click = remember { mutableStateOf(false) }
    val enable = PreferencesUtil.getBoolean("bounce_anim_enable",true)
    val eventState = remember { mutableStateOf(EventState.Idle) }
    if (enable){
        this.bounceScale(eventState){
            if (click.value){
                finishedListener?.invoke(it)

            }
            click.value = false
        }
            .bounceClick(eventState)
            .clickable {
                click.value = true
            }
    }else{
        finishedListener?.invoke(1f)
        this
    }


}

fun Modifier.bounceScale(
    eventState: MutableState<EventState>,
    finishedListener:((Float) -> Unit)? = null
) = composed {
    val scale by animateFloatAsState(if (eventState.value == EventState.Pressed) 0.8f else 1f,
        animationSpec = tween(100),
        label = "", finishedListener ={
            if (it == 1f){
                finishedListener?.invoke(it)

            }
        }
    )
    this.scale(scale)

}

fun Modifier.bounceClick(buttonState: MutableState<EventState>) = composed {

    this.pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                when (event.type) {
                    PointerEventType.Press -> {
                        buttonState.value = EventState.Pressed
                    }
                    PointerEventType.Release -> {
                        buttonState.value = EventState.Idle
                    }
                }

            }
        }

    }
}







fun Modifier.scroll(

    scrollLazyColumnState: LazyListState,
    enableOverScroll: Boolean = true,
    topAppBarScrollBehavior: ScrollBehavior? = null,
) = composed {
    val firstModifier = remember(enableOverScroll) {
        Modifier.overScrollVertical(onOverscroll = { topAppBarScrollBehavior?.isPinned = it })

    }
    val finalModifier = remember(topAppBarScrollBehavior) {
        topAppBarScrollBehavior?.let {
            firstModifier.nestedScroll(it.nestedScrollConnection)
        } ?: firstModifier
    }
    this
        .overScrollVertical(onOverscroll = { topAppBarScrollBehavior?.isPinned = it })
        .nestedScroll(
            topAppBarScrollBehavior!!.nestedScrollConnection
        )
    scrollable(
        flingBehavior = rememberOverscrollFlingBehavior { scrollLazyColumnState },
        state = scrollLazyColumnState,
        orientation =  Orientation.Vertical,
    )

}
