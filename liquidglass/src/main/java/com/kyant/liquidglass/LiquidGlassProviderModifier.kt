package com.kyant.liquidglass

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo

fun Modifier.liquidGlassProvider(
    state: LiquidGlassProviderState
): Modifier =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this then LiquidGlassProviderElement(state = state)
    } else {
        this
    }

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private class LiquidGlassProviderElement(
    val state: LiquidGlassProviderState
) : ModifierNodeElement<LiquidGlassProviderModifierNode>() {

    override fun create(): LiquidGlassProviderModifierNode {
        return LiquidGlassProviderModifierNode(state = state)
    }

    override fun update(node: LiquidGlassProviderModifierNode) {
        node.update(state = state)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "liquidGlassProvider"
        properties["state"] = state
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LiquidGlassProviderElement) return false

        if (state != other.state) return false

        return true
    }

    override fun hashCode(): Int {
        return state.hashCode()
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal class LiquidGlassProviderModifierNode(
    var state: LiquidGlassProviderState
) : DrawModifierNode, GlobalPositionAwareModifierNode, Modifier.Node() {

    override val shouldAutoInvalidate: Boolean = false

    override fun ContentDrawScope.draw() {
        drawContent()

        state.graphicsLayer.record {
            state.backgroundColor?.let { drawRect(it) }
            this@draw.drawContent()
        }
    }

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        state.rect = coordinates.boundsInRoot()
    }

    fun update(
        state: LiquidGlassProviderState
    ) {
        this.state = state
    }
}
