package com.yunzia.hyperstar.ui.base.modifier

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme


fun Modifier.blur(hazeState: HazeState) = this.hazeSource(hazeState)


fun Modifier.showBlur(hazeState: HazeState, alphas: State<Float> = mutableFloatStateOf(1f)) = composed {

    val containerColor: Color = colorScheme.background
    val alpha = 0.67f
    val blurRadius: Dp = 25.dp
    val noiseFactor = 0f
    val hazeStyle = HazeStyle(
            backgroundColor =  containerColor.copy(alphas.value),
            tint = HazeTint(containerColor.copy(alpha*alphas.value)),
            blurRadius = blurRadius*alphas.value,
            noiseFactor = noiseFactor
        )


    this.hazeEffect(
        hazeState,
        hazeStyle

    )

}
