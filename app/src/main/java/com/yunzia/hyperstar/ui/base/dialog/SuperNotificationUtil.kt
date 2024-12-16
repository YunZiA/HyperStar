package com.yunzia.hyperstar.ui.base.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import top.yukonga.miuix.kmp.anim.DecelerateEasing
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.getWindowSize

/**
 * A util class for show popup and dialog.
 */
class SuperNotificationUtil {

    companion object {
        private var isNotificationShowing = mutableStateOf(false)
        private var notificationContext = mutableStateOf<(@Composable () -> Unit)?>(null)

        /**
         * Show a dialog.
         *
         * @param content The [Composable] content of the dialog.
         */
        @Composable
        fun ShowNotification(
            content: (@Composable () -> Unit)? = null,
        ) {
            if (isNotificationShowing.value) return
            isNotificationShowing.value = true
            notificationContext.value = content
        }

        /**
         * Dismiss the dialog.
         *
         * @param show The show state of the dialog.
         */
        fun dismissNotification(
            show: MutableState<Boolean>,
        ) {
            isNotificationShowing.value = false
            show.value = false
        }


        /**
         * A host for show popup and dialog. Already added to the [Scaffold] by default.
         */
        @Composable
        fun SuperNotificationHost() {
            val density = LocalDensity.current
            val getWindowSize by rememberUpdatedState(getWindowSize())
            val windowWidth by rememberUpdatedState(getWindowSize.width.dp / density.density)
            val windowHeight by rememberUpdatedState(getWindowSize.height.dp / density.density)
            val largeScreen by rememberUpdatedState { derivedStateOf { (windowHeight >= 480.dp && windowWidth >= 840.dp) } }
            var dimEnterDuration by remember { mutableIntStateOf(0) }
            var dimExitDuration by remember { mutableIntStateOf(0) }
            if (isNotificationShowing.value) {
                dimEnterDuration = 300
                dimExitDuration = 250
            }
            AnimatedVisibility(
                visible = isNotificationShowing.value,
                modifier = Modifier
                    .zIndex(2f)
                    .fillMaxSize(),
                enter = if (largeScreen.invoke().value) {
                    fadeIn(
                        animationSpec = spring(0.9f, 900f)
                    ) + scaleIn(
                        initialScale = 0.8f,
                        animationSpec = spring(0.73f, 900f)
                    )
                } else {
                    slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight },
                        animationSpec = spring(0.92f, 400f)
                    )
                },
                exit = if (largeScreen.invoke().value) {
                    fadeOut(
                        animationSpec = tween(200, easing = DecelerateEasing(1.5f))
                    ) + scaleOut(
                        targetScale = 0.8f,
                        animationSpec = tween(200, easing = DecelerateEasing(1.5f))
                    )
                } else {
                    slideOutVertically(
                        targetOffsetY = { fullHeight -> fullHeight },
                        animationSpec = tween(200, easing = DecelerateEasing(1.5f))
                    )
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    notificationContext.value?.invoke()
                }

            }

        }
    }
}