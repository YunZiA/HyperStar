package com.yunzia.hyperstar.ui.base

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.base.helper.getSystemSmoothCornerRadius
import com.yunzia.hyperstar.ui.base.modifier.bounceAnim
import kotlinx.coroutines.delay
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape


@Composable
fun FloatingPagerButton(
    modifier: Modifier = Modifier,
    buttonRadius: Dp = 60.dp,
    containerColor: Color = MiuixTheme.colorScheme.primary,
    shadowElevation: Dp = 4.dp,
    minWidth: Dp = 60.dp,
    minHeight: Dp = 60.dp,
    insideMargin: PaddingValues = PaddingValues(0.dp),
    defaultWindowInsetsPadding: Boolean = true,
    buttonContent: @Composable () -> Unit,
    content: @Composable (MutableState<Boolean>) -> Unit,
) {

    val mul = LocalConfiguration.current
    val activity = LocalActivity.current


    val expand = remember { mutableStateOf(false) }

    val durationMillis = 350
    val easing = CubicBezierEasing(0.38F, 0.0F, 0.55F, 0.99F)
    //LinearOutSlowInEasing

    val roundedCorner by rememberUpdatedState(getSystemSmoothCornerRadius())

    val dim = animateColorAsState(
        if (expand.value){
            colorScheme.windowDimming
        }else{
            Color.Transparent
        },
        animationSpec = tween(200, easing = easing),
    )

    val layoutDirection = LayoutDirection.Rtl

    val complete = remember { mutableStateOf(false) }

    val end = animateDpAsState(
        if (expand.value){
            0.dp
        }else{
            insideMargin.calculateEndPadding(layoutDirection)
        },
        animationSpec = tween(durationMillis, easing = easing)
    )


    val bottom = animateDpAsState(
        if (expand.value){
            0.dp
        }else{
            insideMargin.calculateBottomPadding()
        },
        animationSpec = tween(durationMillis, easing = easing)
    )

    val radius = animateDpAsState(
        if (expand.value){
            if (activity?.isInMultiWindowMode == true){
                16.dp
            }else{
                roundedCorner
            }
        }else{
            buttonRadius
        },
        animationSpec = tween(durationMillis, easing = easing),
        finishedListener = {
        }

    )
//    if (radius.value == roundedCorner) radius.value = 0.dp
    LaunchedEffect(radius.value) {
        if (!expand.value){
            complete.value = false
            return@LaunchedEffect
        }
        if (
            radius.value == if (activity?.isInMultiWindowMode == true){
                16.dp
            }else{
                roundedCorner
            }){
            delay(200L)
            complete.value = true
        }else{
            complete.value = false
        }


    }

    Box(
        Modifier
            .fillMaxSize()
            .background(dim.value),
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = modifier
                .padding(
                    insideMargin.calculateStartPadding(layoutDirection),
                    insideMargin.calculateTopPadding(),
                    end.value,
                    bottom.value
                )
                .apply {
                    if (defaultWindowInsetsPadding && !expand.value) {
                        windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                        windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
                    }
                    bounceAnim(!expand.value)
                }

        ){

            Surface(
                modifier = Modifier.semantics { role = Role.Button },
                shape = SmoothRoundedCornerShape(if (complete.value) 0.dp else radius.value,0.8f),
                color = containerColor,
                shadowElevation = if (expand.value) 0.dp else shadowElevation,
                enabled = !expand.value,
                onClick = {
                    expand.value = true
                },
            ) {

                AnimatedVisibility(
                    !expand.value,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis/2,
                            easing = easing,
                            delayMillis  = durationMillis/2
                        )
                    ),
                    exit = fadeOut(
                        animationSpec = tween(
                            durationMillis/2,
                            easing = easing,
                        )
                    )
                ){
                    Box(
                        modifier = Modifier
                            .defaultMinSize(
                                minWidth = minWidth,
                                minHeight = minHeight,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        buttonContent()
                    }

                }
                AnimatedVisibility(
                    expand.value,
                    enter = expandIn(
                        animationSpec = tween(durationMillis, easing = easing)
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis/2,
                            easing = LinearEasing,
                            delayMillis  = durationMillis/2
                        )
                    ) + scaleIn(
                        animationSpec = tween(
                            durationMillis/2,
                            easing = LinearEasing
                        ),
                        initialScale = 0.6f,
                        transformOrigin = TransformOrigin(1f, 1f)
                    ) ,
                    exit = shrinkOut(
                        animationSpec = tween(durationMillis, easing = easing)
                    ) + fadeOut(
                        animationSpec = tween(
                            durationMillis/2,
                            easing = LinearEasing
                        )
                    ) + scaleOut(
                        animationSpec = tween(
                            durationMillis/2,
                            easing = FastOutSlowInEasing
                        ),
                        targetScale = 0.6f,
                        transformOrigin = TransformOrigin(1f, 1f)
                    )
                ) {
                    BackHandler(expand.value) {
                        expand.value = false
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colorScheme.background)

                    ){
                        content(expand)
                    }
                }

            }

        }

    }
}