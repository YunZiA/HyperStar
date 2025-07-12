package com.kyant.liquidglass

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Immutable

@Immutable
data class LiquidGlassStyle(
    val shape: CornerBasedShape,
    val innerRefraction: InnerRefraction = InnerRefraction.Default,
    val material: GlassMaterial = GlassMaterial.Default,
    val border: GlassBorder = GlassBorder.Default,
    val bleed: Bleed = Bleed.None
)
