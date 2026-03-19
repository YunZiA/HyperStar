package com.yunzia.hyperstar.ui.blend

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.requireDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toIntSize

class BlendBlurLayerNode(
    var layers: BlendBlurLayers,
) : DrawModifierNode, GlobalPositionAwareModifierNode, Modifier.Node() {

    override fun ContentDrawScope.draw() {
        drawContent()
        recordLayer(layers.graphicsLayer){ layers.onDraw(this@draw) }
    }

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        if (coordinates.isAttached) {
            layers.layerCoordinates = coordinates
        }
    }

    override fun onDetach() {
        layers.layerCoordinates = null
    }

    fun DrawScope.recordLayer(
        layer: GraphicsLayer,
        size: IntSize = this.size.toIntSize(),
        block: DrawScope.() -> Unit
    ) {
        val density = node.requireDensity()
        layer.record(size) {
            val prevDensity = drawContext.density
            drawContext.density = density
            try {
                this.block()
            } finally {
                drawContext.density = prevDensity
            }
        }
    }

}
