package com.kyant.liquidglass

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawModifierNode
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateLayer
import androidx.compose.ui.node.requireGraphicsContext
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.isSpecified

fun Modifier.liquidGlass(
    state: LiquidGlassProviderState,
    style: () -> LiquidGlassStyle
): Modifier =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this then LiquidGlassElement(
            state = state,
            style = style
        )
    } else {
        this
    }

fun Modifier.liquidGlass(
    state: LiquidGlassProviderState,
    style: LiquidGlassStyle
): Modifier =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this then LiquidGlassElement(
            state = state,
            style = { style }
        )
    } else {
        this
    }

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private class LiquidGlassElement(
    val state: LiquidGlassProviderState,
    val style: () -> LiquidGlassStyle
) : ModifierNodeElement<LiquidGlassModifierNode>() {

    override fun create(): LiquidGlassModifierNode {
        return LiquidGlassModifierNode(
            state = state,
            style = style
        )
    }

    override fun update(node: LiquidGlassModifierNode) {
        node.update(
            state = state,
            style = style
        )
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "liquidGlass"
        properties["state"] = state
        properties["style"] = style
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LiquidGlassElement) return false

        if (state != other.state) return false
        if (style != other.style) return false

        return true
    }

    override fun hashCode(): Int {
        var result = state.hashCode()
        result = 31 * result + style.hashCode()
        return result
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal class LiquidGlassModifierNode(
    var state: LiquidGlassProviderState,
    var style: () -> LiquidGlassStyle
) : LayoutModifierNode, GlobalPositionAwareModifierNode, DelegatingNode() {

    override val shouldAutoInvalidate: Boolean = false

    private val shadersCache = LiquidGlassShadersCache()
    private var rect: Rect? by mutableStateOf(null)

    private var graphicsLayer: GraphicsLayer? = null
    private var borderGraphicsLayer: GraphicsLayer? = null

    private val drawWithCacheModifierNode =
        delegate(
            CacheDrawModifierNode {
                val style = style()

                val contentBlurRadiusPx = style.material.blurRadius.toPx()
                val contentRenderEffect =
                    if (contentBlurRadiusPx > 0f) {
                        RenderEffect.createBlurEffect(
                            contentBlurRadiusPx,
                            contentBlurRadiusPx,
                            Shader.TileMode.CLAMP
                        )
                    } else {
                        RenderEffect.createOffsetEffect(0f, 0f)
                    }

                val cornerRadiusPx = style.shape.topStart.toPx(size, this)

                val hasBleed = style.bleed.opacity > 0f
                val refractionRenderEffect =
                    RenderEffect.createChainEffect(
                        RenderEffect.createRuntimeShaderEffect(
                            shadersCache.getRefractionShader(hasBleed).apply {
                                setFloatUniform("size", size.width, size.height)
                                setFloatUniform("cornerRadius", cornerRadiusPx)

                                setFloatUniform(
                                    "refractionHeight",
                                    style.innerRefraction.height.toPx(this@CacheDrawModifierNode, size)
                                )
                                setFloatUniform(
                                    "refractionAmount",
                                    style.innerRefraction.amount.toPx(this@CacheDrawModifierNode, size)
                                )
                                setFloatUniform(
                                    "eccentricFactor",
                                    style.innerRefraction.eccentricFactor
                                )

                                if (hasBleed) {
                                    setFloatUniform(
                                        "bleedOpacity",
                                        style.bleed.opacity
                                    )
                                }
                            },
                            "image"
                        ),
                        contentRenderEffect
                    )

                val refractionWithBleedRenderEffect =
                    if (hasBleed) {
                        val bleedRenderEffect =
                            RenderEffect.createChainEffect(
                                RenderEffect.createRuntimeShaderEffect(
                                    shadersCache.getBleedShader().apply {
                                        setFloatUniform("size", size.width, size.height)
                                        setFloatUniform("cornerRadius", cornerRadiusPx)

                                        setFloatUniform(
                                            "eccentricFactor",
                                            style.innerRefraction.eccentricFactor
                                        )
                                        setFloatUniform(
                                            "bleedAmount",
                                            style.bleed.amount.toPx(this@CacheDrawModifierNode, size)
                                        )
                                    },
                                    "image"
                                ),
                                contentRenderEffect
                            )

                        val bleedBlurRadiusPx = style.bleed.blurRadius.toPx()
                        val blurredBleedRenderEffect =
                            if (bleedBlurRadiusPx > 0f) {
                                RenderEffect.createChainEffect(
                                    bleedRenderEffect,
                                    RenderEffect.createBlurEffect(
                                        bleedBlurRadiusPx,
                                        bleedBlurRadiusPx,
                                        Shader.TileMode.CLAMP
                                    )
                                )
                            } else {
                                bleedRenderEffect
                            }

                        RenderEffect.createBlendModeEffect(
                            blurredBleedRenderEffect,
                            refractionRenderEffect,
                            android.graphics.BlendMode.SRC_OVER
                        )
                    } else {
                        refractionRenderEffect
                    }

                val materialRenderEffect =
                    if (style.material != GlassMaterial.Default) {
                        RenderEffect.createRuntimeShaderEffect(
                            shadersCache.getMaterialShader().apply {
                                setFloatUniform(
                                    "contrast",
                                    style.material.contrast
                                )
                                setFloatUniform(
                                    "whitePoint",
                                    style.material.whitePoint
                                )
                                setFloatUniform(
                                    "chromaMultiplier",
                                    style.material.chromaMultiplier
                                )
                            },
                            "image"
                        )
                    } else {
                        null
                    }

                val renderEffect =
                    if (materialRenderEffect != null) {
                        RenderEffect.createChainEffect(
                            materialRenderEffect,
                            refractionWithBleedRenderEffect
                        )
                    } else {
                        refractionWithBleedRenderEffect
                    }

                graphicsLayer?.renderEffect = renderEffect.asComposeRenderEffect()

                val borderWidth = style.border.width
                val borderColor = style.border.color
                if (borderWidth.isSpecified && borderColor.isSpecified) {
                    borderGraphicsLayer?.let { layer ->
                        val borderOutline = style.shape.createOutline(size, layoutDirection, this)
                        val borderRenderEffect = style.border.createRenderEffect(this, size, cornerRadiusPx)

                        layer.renderEffect = borderRenderEffect?.asComposeRenderEffect()
                        layer.blendMode = style.border.blendMode
                        layer.record {
                            drawOutline(
                                outline = borderOutline,
                                brush = SolidColor(borderColor),
                                style = Stroke(borderWidth.toPx())
                            )
                        }
                    }
                }

                onDrawBehind {
                    val rect = rect ?: return@onDrawBehind
                    graphicsLayer?.let { layer ->
                        layer.record {
                            translate(-rect.left, -rect.top) {
                                drawLayer(state.graphicsLayer)
                            }
                        }
                        drawLayer(layer)
                    }

                    if (style.material.tint.isSpecified) {
                        drawRect(
                            color = style.material.tint,
                            blendMode = style.material.blendMode
                        )
                    }

                    borderGraphicsLayer?.let { layer ->
                        drawLayer(layer)
                    }
                }
            }
        )

    private val layerBlock: GraphicsLayerScope.() -> Unit = {
        compositingStrategy = CompositingStrategy.Offscreen
        clip = true
        shape = style().shape
    }

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measurable.measure(constraints)

        return layout(placeable.width, placeable.height) {
            placeable.placeWithLayer(0, 0, layerBlock = layerBlock)
        }
    }

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        rect = state.rect?.let {
            coordinates.boundsInRoot().translate(-it.topLeft)
        }
    }

    override fun onAttach() {
        val graphicsContext = requireGraphicsContext()
        graphicsLayer =
            graphicsContext.createGraphicsLayer().apply {
                compositingStrategy = androidx.compose.ui.graphics.layer.CompositingStrategy.Offscreen
            }
        borderGraphicsLayer =
            graphicsContext.createGraphicsLayer().apply {
                compositingStrategy = androidx.compose.ui.graphics.layer.CompositingStrategy.Offscreen
            }
    }

    override fun onDetach() {
        val graphicsContext = requireGraphicsContext()
        graphicsLayer?.let { layer ->
            graphicsContext.releaseGraphicsLayer(layer)
            graphicsLayer = null
        }
        borderGraphicsLayer?.let { layer ->
            graphicsContext.releaseGraphicsLayer(layer)
            borderGraphicsLayer = null
        }
    }

    fun update(
        state: LiquidGlassProviderState,
        style: () -> LiquidGlassStyle
    ) {
        if (this.state != state ||
            this.style != style
        ) {
            this.state = state
            this.style = style
            drawWithCacheModifierNode.invalidateDrawCache()
            invalidateLayer()
        }
    }
}
