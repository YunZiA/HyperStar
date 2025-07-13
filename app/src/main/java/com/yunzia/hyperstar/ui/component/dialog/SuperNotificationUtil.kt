package com.yunzia.hyperstar.ui.component.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.squareup.kotlinpoet.FLOAT_ARRAY
import top.yukonga.miuix.kmp.anim.DecelerateEasing
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.utils.getWindowSize
import kotlin.collections.component1
import kotlin.collections.component2

/**
 * A util class for show popup and dialog.
 */
object SuperNotificationUtil {

    private val notificationStates = mutableStateMapOf< Notification,NotificationState >()
    private var nextZIndex = 1f

    data class Notification(
        val content: @Composable () -> Unit
    )

    data class NotificationState(
        val zIndex: Float,
        val state: MutableState<Boolean>
    )

    fun notification(
        content: @Composable () -> Unit,
    ): Notification {

        return Notification(content)
    }

    fun Notification.show(){
        val show = mutableStateOf(true)
        if (notificationStates[this] == null){
            nextZIndex++
        }
        notificationStates[this] = NotificationState(
            nextZIndex,
            show
        )
    }
    fun Notification.dismiss(){
        notificationStates[this]?.state?.value = false
    }


    @Composable
    fun SuperNotificationHost() {
        val density = LocalDensity.current
        val getWindowSize by rememberUpdatedState(getWindowSize())
        val windowWidth by rememberUpdatedState(getWindowSize.width.dp / density.density)
        val windowHeight by rememberUpdatedState(getWindowSize.height.dp / density.density)
        val largeScreen by remember { derivedStateOf { (windowHeight >= 480.dp && windowWidth >= 840.dp) } }

        notificationStates.entries.forEach { (notification, notificationState) ->
            key(notificationState) {
                var internalVisible by remember { mutableStateOf(false) }

                LaunchedEffect(notificationState.state.value) {
                    internalVisible = notificationState.state.value
                }

                // Content layer for the dialog
                AnimatedVisibility(
                    visible = internalVisible,
                    modifier = Modifier.zIndex(notificationState.zIndex).fillMaxSize(),
                    enter = slideInVertically(
                        initialOffsetY = { fullHeight -> 0 },
                        animationSpec = tween(200, easing = DecelerateEasing(1.5f))
                    ),
                    exit =  slideOutVertically(
                        targetOffsetY = { fullHeight -> 0 },
                        animationSpec = tween(200, easing = DecelerateEasing(1.5f))
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        notification.content()
                    }
                    DisposableEffect(notificationState.state.value) {
                        onDispose {
                            if (!notificationState.state.value) {
                                notificationStates.remove(notification)
                            }
                        }
                    }
                }
            }
        }
    }

}