package com.yunzia.hyperstar.ui.component.modifier

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.component.enums.EventState
import com.yunzia.hyperstar.utils.PreferencesUtil
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape


fun Modifier.bounceAnimN(
    enabled: Boolean = true,
    finishedListener:((Float) -> Unit)? = null
) = composed {

    val eventState = remember { mutableStateOf(EventState.Idle) }
    this.bounceScale(eventState) {
            finishedListener?.invoke(it)
        }
        .bounceClick(eventState, enabled)
        .clip(RoundedCornerShape(8.dp))
}

fun Modifier.bounceAnim(
    enabled: Boolean = true,
    finishedListener:((Float) -> Unit)? = null
) = composed {
    val updatedOnClick by rememberUpdatedState(finishedListener)
    val enable = PreferencesUtil.getBoolean("bounce_anim_enable",true)
    val eventState = remember { mutableStateOf(EventState.Idle) }
    if (enabled){
        if (enable){
            this.bounceScale(eventState) {
                    finishedListener?.invoke(it)
                }
                .bounceClick(eventState, enabled)
                .clip(RoundedCornerShape(8.dp))

        }else{
            finishedListener?.invoke(1f)
            this

        }
    }else{
        this
    }


}

fun Modifier.bounceScale(
    eventState: MutableState<EventState>,
    finishedListener:((Float) -> Unit)? = null
) = composed {
    val scale by animateFloatAsState(if (eventState.value == EventState.Pressed) 0.9f else 1f,
        animationSpec = tween(100),
        label = "", finishedListener ={
            if (it == 1f){
                finishedListener?.invoke(it)

            }
        }
    )
    this.scale(scale)

}


fun Modifier.bounceCorner(
    eventState: MutableState<EventState>,
) = composed {
    val size by animateDpAsState(if (eventState.value == EventState.Pressed) 8.dp else 0.dp,
        animationSpec = tween(100),
        label = "",
    )
    this.clip(SmoothRoundedCornerShape(size,0.5f))

}

fun Modifier.bounceClick(
    buttonState: MutableState<EventState>,
    enabled: Boolean = true,
) = composed {


    this.pointerInput(Unit) {
        awaitPointerEventScope {
            while (enabled) {
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


