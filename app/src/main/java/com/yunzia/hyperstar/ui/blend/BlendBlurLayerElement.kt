package com.yunzia.hyperstar.ui.blend

import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo

data class BlendBlurLayerElement(
    val layers: BlendBlurLayers,
) : ModifierNodeElement<BlendBlurLayerNode>() {

    override fun create(): BlendBlurLayerNode = BlendBlurLayerNode(layers = layers)

    override fun update(node: BlendBlurLayerNode) {
        node.layers = layers
        node.invalidateDraw()
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "BlendBlurLayers"
        properties["layers"] = layers
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BlendBlurLayerNode) return false

        if (layers != other.layers) return false

        return true
    }

    override fun hashCode(): Int {
        return layers.hashCode()
    }
}