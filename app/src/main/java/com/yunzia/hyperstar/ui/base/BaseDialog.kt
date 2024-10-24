package com.yunzia.hyperstar.ui.base

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import top.yukonga.miuix.kmp.basic.Box


private var isDialogShowing = mutableStateOf(false)

private var dialogContext = mutableStateOf<(@Composable () -> Unit)?>(null)

/**
 * Show a dialog.
 *
 * @param content The [Composable] content of the dialog.
 */
@Composable
fun ShowDialog(
    content: (@Composable () -> Unit) = {  },
) {
    isDialogShowing.value = true
    BaseDialog(content)
    //dialogContext.value = content
}

/**
 * Dismiss the dialog.
 */
fun dismissDialog() {
    isDialogShowing.value = false
}

@Composable
fun BaseDialog(content: (@Composable () -> Unit)){
    AnimatedVisibility(
        visible = isDialogShowing.value,
        modifier = Modifier
            .zIndex(1f)
            .fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )
    }
    AnimatedVisibility(
        visible = isDialogShowing.value,
        modifier = Modifier
            .zIndex(2f)
            .fillMaxSize(),
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(
                durationMillis = 500,
                easing = CubicBezierEasing(0f, 1f, 0.36f, 1f)
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(
                durationMillis = 300,
                easing = CubicBezierEasing(1f, 0f, 0.64f, 0f)
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
//                    dialogContext.value?.invoke()
            content()
        }
    }
}


