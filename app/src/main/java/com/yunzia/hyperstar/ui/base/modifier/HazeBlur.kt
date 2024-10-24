package com.yunzia.hyperstar.ui.base.modifier

import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme


fun Modifier.blur(hazeState: HazeState) = this.haze(hazeState)

fun Modifier.showBlur(hazeState: HazeState) = composed {


    val containerColor: Color = colorScheme.background
    val alpha = 0.65f
    val blurRadius: Dp = 20.dp
    val noiseFactor = 0f
    val hazeStyle = remember(containerColor, alpha, blurRadius, noiseFactor) {
        HazeStyle(
            backgroundColor = containerColor,
            tint = HazeTint(containerColor.copy(alpha)),
            blurRadius = blurRadius,
            noiseFactor = noiseFactor
        )
    }

    this.hazeChild(
        hazeState,
        hazeStyle
    )

}