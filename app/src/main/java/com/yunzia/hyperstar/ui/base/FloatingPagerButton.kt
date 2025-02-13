package com.yunzia.hyperstar.ui.base

import android.view.RoundedCorner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.base.modifier.bounceAnim
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.utils.getCornerRadiusBottom

@Composable
private fun getSystemCornerRadius(): Dp {
    val insets = LocalView.current.rootWindowInsets
    val density = LocalDensity.current.density
    val roundedCornerRadius =
        insets?.getRoundedCorner(RoundedCorner.POSITION_BOTTOM_LEFT)?.radius ?: getCornerRadiusBottom()
    val cornerDp = (roundedCornerRadius / density).dp
    //if (cornerDp <= 32.dp) return 0.dp
    return cornerDp
}

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

    val expand = remember { mutableStateOf(false) }

    val durationMillis = 350
    val easing = LinearOutSlowInEasing

    val roundedCorner by rememberUpdatedState(getSystemCornerRadius())

    val dim = animateColorAsState(
        if (expand.value){
            colorScheme.windowDimming
        }else{
            Color.Transparent
        },
        animationSpec = tween(200, easing = easing)

    )

    val layoutDirection = LayoutDirection.Rtl

    val end = animateDpAsState(
        if (expand.value){
            0.dp
        }else{
            insideMargin.calculateEndPadding(layoutDirection)
            //0.dp
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
            roundedCorner*12/10
        }else{
            buttonRadius
        },
        animationSpec = tween(durationMillis, easing = easing)
    )

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
                shape = SmoothRoundedCornerShape(radius.value,0.8f),
                color = containerColor,
                shadowElevation = if (expand.value) 0.dp else shadowElevation,
                enabled = !expand.value,
                onClick = {
                    expand.value = true
                },
            ) {

                AnimatedVisibility(
                    !expand.value
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
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis/4*3,
                            easing = easing,
                            delayMillis  = durationMillis/2
                        )
                    ) + expandIn(
                        animationSpec = tween(durationMillis, easing = easing)
                    ),
                    exit = shrinkOut(
                        animationSpec = tween(durationMillis, easing = easing)
                    ) + fadeOut(
                        animationSpec = tween(durationMillis, easing = easing)
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