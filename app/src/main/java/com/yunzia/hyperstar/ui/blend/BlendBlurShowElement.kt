//package com.yunzia.hyperstar.ui.blend
//
//import androidx.compose.ui.node.ModifierNodeElement
//import androidx.compose.ui.platform.InspectorInfo
//import dev.chrisbanes.haze.HazeEffectNode
//import dev.chrisbanes.haze.HazeEffectScope
//import dev.chrisbanes.haze.HazeState
//import dev.chrisbanes.haze.HazeStyle
//
//data class BlendBlurShowElement (
//    val state: HazeState?,
//    val style: HazeStyle = HazeStyle.Unspecified,
//    val block: (HazeEffectScope.() -> Unit)? = null,
//) : ModifierNodeElement<BlendBlurShowNode>() {
//
//    override fun create(): BlendBlurShowNode = BlendBlurShowNode(state, style, block)
//
//    override fun update(node: BlendBlurShowNode) {
//        node.state = state
//        node.style = style
//        node.block = block
//        node.update()
//    }
//
//    override fun InspectorInfo.inspectableProperties() {
//        name = "BlendBlurShow"
//    }
//}