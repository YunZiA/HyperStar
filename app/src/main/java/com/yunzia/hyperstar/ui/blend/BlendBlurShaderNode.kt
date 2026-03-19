//package com.yunzia.hyperstar.ui.blend
//
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableFloatStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.neverEqualPolicy
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.drawWithCache
//import androidx.compose.ui.geometry.toRect
//import androidx.compose.ui.graphics.GraphicsLayerScope
//import androidx.compose.ui.graphics.Paint
//import androidx.compose.ui.graphics.ShaderBrush
//import androidx.compose.ui.graphics.Shape
//import androidx.compose.ui.graphics.drawscope.ContentDrawScope
//import androidx.compose.ui.graphics.drawscope.DrawScope
//import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
//import androidx.compose.ui.graphics.layer.GraphicsLayer
//import androidx.compose.ui.graphics.layer.drawLayer
//import androidx.compose.ui.layout.LayoutCoordinates
//import androidx.compose.ui.layout.Measurable
//import androidx.compose.ui.layout.MeasureResult
//import androidx.compose.ui.layout.MeasureScope
//import androidx.compose.ui.node.DrawModifierNode
//import androidx.compose.ui.node.GlobalPositionAwareModifierNode
//import androidx.compose.ui.node.LayoutModifierNode
//import androidx.compose.ui.node.ObserverModifierNode
//import androidx.compose.ui.node.observeReads
//import androidx.compose.ui.node.requireDensity
//import androidx.compose.ui.node.requireGraphicsContext
//import androidx.compose.ui.text.AnnotatedString
//import androidx.compose.ui.text.TextMeasurer
//import androidx.compose.ui.text.drawText
//import androidx.compose.ui.unit.Constraints
//import androidx.compose.ui.unit.IntOffset
//import androidx.compose.ui.unit.IntSize
//import androidx.compose.ui.unit.toIntSize
//import com.kyant.backdrop.internal.isRenderEffectSupported
//import dev.chrisbanes.haze.HazeEffectScope
//import java.nio.file.Files.size
//
//class BlendBlurShaderNode(
//    val layers: BlendBlurLayers,
//    var applyBlendBlurToBrush: (ShaderBrush) -> Unit = {},
//) : LayoutModifierNode, DrawModifierNode, GlobalPositionAwareModifierNode, ObserverModifierNode, Modifier.Node() {
//
//    private val effectScope = BackdropEffectScopeImpl()
//
//    private var graphicsLayer: GraphicsLayer? = null
//
//    private val layoutLayerBlock: GraphicsLayerScope.() -> Unit = {
//        clip = true
//        shape = shapeProvider.shape
//        compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen
//    }
//
//    private var layoutCoordinates: LayoutCoordinates? by mutableStateOf(null, neverEqualPolicy())
//
//    private var padding by mutableFloatStateOf(0f)
//
//    private val recordBackdropBlock: (DrawScope.() -> Unit) = {
//        val canvas = drawContext.canvas
//        val padding = padding
//
//        if (padding != 0f) {
//            canvas.translate(padding, padding)
//        }
//        onDrawBackdrop {
//            with(backdrop) {
//                drawBackdrop(
//                    density = effectScope,
//                    coordinates = layoutCoordinates,
//                    layerBlock = layerBlock
//                )
//            }
//        }
//        if (padding != 0f) {
//            canvas.translate(-padding, -padding)
//        }
//    }
//
//    private val drawBackdropLayer: DrawScope.() -> Unit = {
//        val layer = graphicsLayer
//        if (layer != null) {
//            val padding = padding
//
//            recordLayer(
//                layer,
//                size = IntSize(
//                    size.width.toInt() + padding.toInt() * 2,
//                    size.height.toInt() + padding.toInt() * 2
//                ),
//                block = recordBackdropBlock
//            )
//
//            layer.topLeft =
//                if (padding != 0f) IntOffset(-padding.toInt(), -padding.toInt())
//                else IntOffset.Zero
//            drawLayer(layer)
//        }
//    }
//
//    override fun MeasureScope.measure(
//        measurable: Measurable,
//        constraints: Constraints
//    ): MeasureResult {
//        val placeable = measurable.measure(constraints)
//        return layout(placeable.width, placeable.height) {
//            placeable.placeWithLayer(IntOffset.Zero, layerBlock = layoutLayerBlock)
//        }
//    }
//
//    override fun ContentDrawScope.draw() {
//        if (effectScope.update(this)) {
//            updateEffects()
//        }
//
//
//        layers.graphicsLayer.let {
//
//            recordLayer(it) {
//                drawBackdropLayer()
//            }
//
//            val blurredBitmap = it.toImageBitmap()
//            applyBlendBlurToBrush.invoke(ShaderBrush())
//        }
//    }
//
//    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
//        if (coordinates.isAttached) {
//            if (layers.isCoordinatesDependent) {
//                layoutCoordinates = coordinates
//            } else {
//                if (layoutCoordinates != null) {
//                    layoutCoordinates = null
//                }
//            }
//            layers.layerCoordinates = coordinates
//        }
//    }
//
//    override fun onObservedReadsChanged() {
//        invalidateDrawCache()
//    }
//
//    fun invalidateDrawCache() {
//        observeEffects()
//    }
//
//    private fun observeEffects() {
//        observeReads { updateEffects() }
//    }
//
//    private fun updateEffects() {
//        if (!isRenderEffectSupported()) return
//
//        effectScope.apply(effects)
//        graphicsLayer?.renderEffect = effectScope.renderEffect
//        padding = effectScope.padding
//    }
//
//    override fun onAttach() {
//        val graphicsContext = requireGraphicsContext()
//        graphicsLayer = graphicsContext.createGraphicsLayer()
//
//        observeEffects()
//    }
//
//    override fun onDetach() {
//        val graphicsContext = requireGraphicsContext()
//        graphicsLayer?.let { layer ->
//            graphicsContext.releaseGraphicsLayer(layer)
//            graphicsLayer = null
//        }
//
//        effectScope.reset()
//        layoutCoordinates = null
//        layers.layerCoordinates = null
//    }
//
//
//    fun DrawScope.recordLayer(
//        layer: GraphicsLayer,
//        size: IntSize = this.size.toIntSize(),
//        block: DrawScope.() -> Unit
//    ) {
//        val density = node.requireDensity()
//        layer.record(size) {
//            val prevDensity = drawContext.density
//            drawContext.density = density
//            try {
//                this.block()
//            } finally {
//                drawContext.density = prevDensity
//            }
//        }
//    }
//}
//
//
//fun Modifier.backdropBlurText(
//    textMeasurer: TextMeasurer,
//    text: AnnotatedString,
//    blurRadius: Float = 20f
//): Modifier = drawWithCache {
//
//    val blurEffect = android.graphics.RenderEffect.createBlurEffect(
//        blurRadius,
//        blurRadius,
//        android.graphics.Shader.TileMode.CLAMP
//    )
//
//    val textLayout = textMeasurer.measure(text)
//
//    onDrawWithContent {
//
//        // 1. 创建 offscreen layer + blur
//        drawIntoCanvas { canvas ->
//
//            val paint = Paint().apply {
//                asFrameworkPaint().setRenderEffect(blurEffect)
//            }
//
//            canvas.saveLayer(size.toRect(), paint)
//
//            // 2. 绘制背景
//            drawContent()
//
//            // 3. 用 text 做 mask
//            drawText(
//                textLayout
//            )
//
//            canvas.restore()
//        }
//    }
//}