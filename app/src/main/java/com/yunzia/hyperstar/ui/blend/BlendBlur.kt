package com.yunzia.hyperstar.ui.blend

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

@Stable
fun Modifier.blendBlurChoose(
    state: BlendBlurLayers,
    zIndex: Float = 0f,
    key: Any? = null,
): Modifier = this then BlendBlurLayerElement(state)


//@Stable
//fun Modifier.blendBlurShow(
//    state: BlendBlurState,
//    zIndex: Float = 0f,
//    key: Any? = null,
//): Modifier = this then BlendBlurShowElement(state, zIndex, key)