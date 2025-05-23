package com.yunzia.hyperstar.ui.component.modifier

import android.annotation.SuppressLint
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme


fun Modifier.blur(hazeState: HazeState) = this.hazeSource(hazeState)


@SuppressLint("UnnecessaryComposedModifier")
@OptIn(ExperimentalHazeApi::class)
fun Modifier.showBlur(hazeState: HazeState) = composed {

    val containerColor: Color = colorScheme.background
    val alpha = 0.67f
    val blurRadius: Dp = 25.dp
    val noiseFactor = 0f
    val hazeStyle = HazeStyle(
        backgroundColor = containerColor,
        tint = HazeTint(containerColor.copy(alpha)),
        blurRadius = blurRadius,
        noiseFactor = noiseFactor
    )

    this.hazeEffect(
        hazeState,
        hazeStyle,
    ){
        inputScale = HazeInputScale.Fixed(0.5f)
    }

}
