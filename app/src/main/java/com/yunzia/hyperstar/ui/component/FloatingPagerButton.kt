package com.yunzia.hyperstar.ui.component

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.component.helper.getSystemSmoothCornerRadius
import com.yunzia.hyperstar.ui.component.modifier.bounceAnim
import kotlinx.coroutines.delay
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
fun FloatingPagerButton(
    modifier: Modifier = Modifier,
    buttonRadius: Dp = 60.dp,
    containerColor: Color = MiuixTheme.colorScheme.primary,
    shadowElevation: Dp = 4.dp,
    minSize: Dp = 60.dp,
    insideMargin: PaddingValues = PaddingValues(0.dp),
    defaultWindowInsetsPadding: Boolean = true,
    buttonContent: @Composable () -> Unit,
    content: @Composable (MutableState<Boolean>) -> Unit,
) {
    val activity = LocalActivity.current
    val expand = remember { mutableStateOf(false) }
    val complete = remember { mutableStateOf(false) }

    val durationMillis = 400
    val easing = CubicBezierEasing(0.38F, 0.0F, 0.55F, 0.99F)
    val roundedCorner = rememberUpdatedState (getSystemSmoothCornerRadius())


    // 动画状态
    val dim by animateColorAsState(
        targetValue = if (expand.value) MiuixTheme.colorScheme.windowDimming else Color.Transparent,
        animationSpec = tween(durationMillis, easing = LinearOutSlowInEasing)
    )

    val alpha by animateFloatAsState(
        targetValue = if (expand.value) 1f else 0f,
        animationSpec = tween(durationMillis, easing = LinearOutSlowInEasing)
    )


    val layoutDirection = LocalLayoutDirection.current

    val endPadding by animateDpAsState(
        targetValue = if (expand.value) 0.dp else insideMargin.calculateEndPadding(layoutDirection),
        animationSpec = tween(durationMillis+50, 50, easing = LinearOutSlowInEasing)
    )

    val bottomPadding by animateDpAsState(
        targetValue = if (expand.value) 0.dp else insideMargin.calculateBottomPadding(),
        animationSpec = tween(durationMillis+50, 50, easing = LinearOutSlowInEasing)
    )

    val radius by animateDpAsState(
        targetValue = if (expand.value) {
            if (activity?.isInMultiWindowMode == true) 16.dp else roundedCorner.value
        } else buttonRadius,
        animationSpec = tween(durationMillis, easing = LinearOutSlowInEasing),
        finishedListener = {}
    )

    LaunchedEffect(radius.value) {
        if (!expand.value){
            complete.value = false
            return@LaunchedEffect
        }
        if (radius == if (activity?.isInMultiWindowMode == true){
                16.dp
            }else{
                roundedCorner.value
            }){
            delay(200L)
            complete.value = true
        }else{
            complete.value = false
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(dim),
        contentAlignment = Alignment.BottomEnd
    ) {


        val height by animateDpAsState(
            targetValue = if (expand.value) this.maxHeight else minSize,
            animationSpec = tween(durationMillis, easing = LinearOutSlowInEasing)
        )

        val width by animateDpAsState(
            targetValue = if (expand.value) this.maxWidth else minSize,
            animationSpec = tween(durationMillis, easing = LinearOutSlowInEasing)
        )

        Box(
            modifier = modifier
                .padding(
                    insideMargin.calculateStartPadding(layoutDirection),
                    insideMargin.calculateTopPadding(),
                    endPadding,
                    bottomPadding
                )
                .apply {
                    if (defaultWindowInsetsPadding && !expand.value) {
                        windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                        windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
                    }
                    bounceAnim(!expand.value)
                },
            contentAlignment = Alignment.BottomEnd,
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        alpha = if (alpha >= 0.2f) alpha else 0f,
                        scaleX = width/this@BoxWithConstraints.maxWidth,
                        scaleY = height/this@BoxWithConstraints.maxHeight,
                        shape = SmoothRoundedCornerShape(if (complete.value) 0.dp else radius, 1f),
                        clip = true,
                        transformOrigin = TransformOrigin(1f, 1f)
                    )
            ){
                BackHandler(enabled = expand.value) {
                    expand.value = false
                }
                Box(
                    modifier = Modifier
                        .background(MiuixTheme.colorScheme.background)
                ) {
                    content(expand)
                }
            }
            if (alpha <= 0.8f){

                Surface(
                    modifier = Modifier.semantics { role = Role.Button },
                    shape = SmoothRoundedCornerShape(if (complete.value) 0.dp else radius, 1f),
                    color = containerColor,
                    shadowElevation = if (expand.value) 0.dp else shadowElevation,
                    enabled = !expand.value,
                    onClick = {}
                ) {

                    BackHandler(enabled = expand.value) {
                        expand.value = false
                    }
                    Box(
                        modifier = Modifier
                            .clickable { expand.value = true }
                            .size(width,height)
                            .defaultMinSize(minWidth = minSize, minHeight = minSize),
                        contentAlignment = Alignment.Center
                    ) {
                        buttonContent()
                    }
                }
            }




        }
    }
}