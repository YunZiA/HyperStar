package com.yunzia.hyperstar.ui.component.modifier

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.blur.BlendColorEntry
import top.yukonga.miuix.kmp.blur.BlurBlendMode
import top.yukonga.miuix.kmp.blur.BlurColors
import top.yukonga.miuix.kmp.blur.BlurDefaults
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.blur.rememberLayerBackdrop
import top.yukonga.miuix.kmp.blur.textureBlur
import top.yukonga.miuix.kmp.blur.textureEffect
import top.yukonga.miuix.kmp.shapes.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

val Localbackdrop = compositionLocalOf< LayerBackdrop> { error("Localbackdrop not provided") }

@Composable
fun rememberLayerBackdrop(
    containerColor: Color = colorScheme.surface
): LayerBackdrop = rememberLayerBackdrop(
    onDraw = {
        drawRect(containerColor)
        drawContent()
    }
)


fun Modifier.blur(backdrop: LayerBackdrop) = this.layerBackdrop(backdrop)

@Composable
fun Modifier.showBlur(backdrop: LayerBackdrop) =

    this.textureEffect(
        backdrop = backdrop,
        shape = RectangleShape,
        blurRadius = 200f,
//        contentBlendMode = BlendMode.DstOver,
//        noiseCoefficient = 0f
        colors = BlurColors(
            blendColors = listOf(
                BlendColorEntry(colorScheme.surface, BlurBlendMode.ColorBurn)
            )
        ),
    )

fun Modifier.cardTextureBlur(
) = this
