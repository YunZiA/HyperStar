package com.yunzia.hyperstar.ui.base

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ItemAnim(
    animState: Boolean,
    waitTime: Long = 0L,
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var delayedAnimState by remember { mutableStateOf(animState) }


    LaunchedEffect(animState, waitTime) {
        if (waitTime > 0L) {
            coroutineScope.launch {
                delay(waitTime)
                delayedAnimState = animState
            }
        } else {

            delayedAnimState = animState
        }
    }


    AnimatedVisibility(
        delayedAnimState,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        content()
    }
}