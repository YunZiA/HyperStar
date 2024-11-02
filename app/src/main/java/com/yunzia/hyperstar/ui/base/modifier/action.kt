package com.yunzia.hyperstar.ui.base.modifier

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import com.yunzia.hyperstar.ui.base.enums.EventState
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.rememberOverscrollFlingBehavior

fun Modifier.bounceClick(
    onClick: () -> Unit
) = composed {
    val coroutineScope = rememberCoroutineScope()
    var click by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    //val indication =  createRipple()
    var eventState by remember { mutableStateOf(EventState.Idle) }
    val scale by animateFloatAsState(if (eventState == EventState.Pressed ) 0.7f else 1f,animationSpec = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow
    ), finishedListener = {
//        if (it == 1f && click){
//            onClick(click)
//            click = false
//        }  //else eventState = EventState.Idle
    }
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        //.indication(interactionSource, indication)
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    click = true
                    coroutineScope.launch {
                        onClick()
                    }
                },
                onPress = { offset ->
                    val press = PressInteraction.Press(offset)
                    coroutineScope.launch {
                        interactionSource.emit(press)
                        eventState = EventState.Pressed
                        val isCanceled = tryAwaitRelease()
                        Log.d("ggc", "bounceClick: ${isCanceled}")
                        awaitRelease()
                        //tryAwaitRelease()
                        interactionSource.emit(PressInteraction.Release(press))
                        eventState = EventState.Idle
                    }


                }
            )
        }

}

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.bounceClicks(
    onClick: () -> Unit
) = composed {
    val coroutineScope = rememberCoroutineScope()
    var click by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    //val indication =  createRipple()
    var eventState by remember { mutableStateOf(EventState.Idle) }
    val scale by animateFloatAsState(if (eventState == EventState.Pressed ) 0.95f else 1f,animationSpec = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessHigh
    ), finishedListener = {
//        if (it == 0.7f && !click){
//            eventState = EventState.Idle
//        }
//
//        if (it >= 0.95f && click ){
//            click = false
//        }  //else eventState = EventState.Idle
        if (eventState == EventState.Pressed && click){
            eventState = EventState.Idle
            click = false

        }
    }
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        //.indication(interactionSource, indication)
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    click = true
                    //eventState = EventState.Pressed
                    onClick()
                },
                onPress = { offset ->
                    val press = PressInteraction.Press(offset)
                    coroutineScope.launch {
                        interactionSource.emit(press)
                        eventState = EventState.Pressed
                        val isCanceled = tryAwaitRelease()
                        interactionSource.emit(PressInteraction.Release(press))
                        if (isCanceled){
                            Log.d("ggc", "bounceClick: ${isCanceled}")
                            eventState = EventState.Idle
                        }

                    }


                }
            )
        }

}

fun Modifier.bounceClick() = composed {
    var press by remember { mutableStateOf(false) }
    var eventState by remember { mutableStateOf(EventState.Idle) }
    val scale by animateFloatAsState(if (eventState == EventState.Pressed) 0.70f else 1f,animationSpec = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow
    ), finishedListener = {

        if (press && it != 1f && eventState == EventState.Idle) eventState = EventState.Idle
    }
    )
    val coroutineScope = rememberCoroutineScope()
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(eventState) {
            awaitPointerEventScope {
                if (eventState == EventState.Pressed){
                    waitForUpOrCancellation()
                    eventState = EventState.Idle

                }else {
                    awaitFirstDown(false)
                    eventState = EventState.Pressed


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
