//package com.yunzia.hyperstar.ui.blend
//
//import androidx.collection.MutableObjectLongMap
//import androidx.compose.runtime.snapshots.Snapshot
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.BlurredEdgeTreatment
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.geometry.Rect
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.geometry.isSpecified
//import androidx.compose.ui.geometry.isUnspecified
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.drawscope.ContentDrawScope
//import androidx.compose.ui.graphics.layer.drawLayer
//import androidx.compose.ui.layout.LayoutCoordinates
//import androidx.compose.ui.layout.findRootCoordinates
//import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
//import androidx.compose.ui.node.DrawModifierNode
//import androidx.compose.ui.node.GlobalPositionAwareModifierNode
//import androidx.compose.ui.node.LayoutAwareModifierNode
//import androidx.compose.ui.node.ObserverModifierNode
//import androidx.compose.ui.node.TraversableNode
//import androidx.compose.ui.node.currentValueOf
//import androidx.compose.ui.node.findNearestAncestor
//import androidx.compose.ui.node.invalidateDraw
//import androidx.compose.ui.node.observeReads
//import androidx.compose.ui.node.requireDensity
//import androidx.compose.ui.node.requireGraphicsContext
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.takeOrElse
//import androidx.compose.ui.unit.toIntSize
//import androidx.compose.ui.unit.toSize
//import androidx.compose.ui.util.trace
//import dev.chrisbanes.haze.Bitmask
//import dev.chrisbanes.haze.BlurEffect
//import dev.chrisbanes.haze.DirtyFields
//import dev.chrisbanes.haze.HazeArea
//import dev.chrisbanes.haze.HazeDefaults
//import dev.chrisbanes.haze.HazeEffectScope
//import dev.chrisbanes.haze.HazeInputScale
//import dev.chrisbanes.haze.HazeLogger
//import dev.chrisbanes.haze.HazeProgressive
//import dev.chrisbanes.haze.HazeSourceNode
//import dev.chrisbanes.haze.HazeState
//import dev.chrisbanes.haze.HazeStyle
//import dev.chrisbanes.haze.HazeTint
//import dev.chrisbanes.haze.HazeTraversableNodeKeys
//import dev.chrisbanes.haze.LocalHazeStyle
//import dev.chrisbanes.haze.OnPreDrawListener
//import dev.chrisbanes.haze.ScrimBlurEffect
//import dev.chrisbanes.haze.getWindowId
//import dev.chrisbanes.haze.letIf
//import dev.chrisbanes.haze.positionForHaze
//import dev.chrisbanes.haze.resolveBlurEnabled
//import dev.chrisbanes.haze.resolveBlurRadius
//import dev.chrisbanes.haze.shouldClip
//import dev.chrisbanes.haze.shouldClipToAreaBounds
//import dev.chrisbanes.haze.shouldExpandLayer
//import dev.chrisbanes.haze.shouldUsePreDrawListener
//import dev.chrisbanes.haze.trace
//import dev.chrisbanes.haze.unsynchronizedLazy
//import dev.chrisbanes.haze.updateBlurEffectIfNeeded
//import kotlin.collections.asSequence
//import kotlin.collections.forEach
//import kotlin.collections.orEmpty
//import kotlin.getValue
//import kotlin.math.max
//import kotlin.math.min
//
//class BlendBlurShowNode(
//    var state: HazeState? = null,
//    style: HazeStyle = HazeStyle.Unspecified,
//    var block: (HazeEffectScope.() -> Unit)? = null,
//) : Modifier.Node(),
//    CompositionLocalConsumerModifierNode,
//    GlobalPositionAwareModifierNode,
//    LayoutAwareModifierNode,
//    ObserverModifierNode,
//    DrawModifierNode,
//    TraversableNode,
//    HazeEffectScope {
//
//    override val traverseKey: Any
//        get() = BlendBlurNodeKeys.Blur
//
//    override val shouldAutoInvalidate: Boolean = false
//
//    internal var dirtyTracker = Bitmask()
//
//    internal var blurEnabledSet: Boolean = false
//    override var blurEnabled: Boolean = resolveBlurEnabled()
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "blurEnabled changed. Current: $field. New: $value" }
//                field = value
//                dirtyTracker += DirtyFields.BlurEnabled
//            }
//            // Mark the set flag, to indicate that this value should take precedence
//            blurEnabledSet = true
//        }
//
//    override var inputScale: HazeInputScale = HazeInputScale.Default
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "inputScale changed. Current: $field. New: $value" }
//                field = value
//                dirtyTracker += DirtyFields.InputScale
//            }
//        }
//
//    internal var compositionLocalStyle: HazeStyle = HazeStyle.Unspecified
//        set(value) {
//            if (field != value) {
//                HazeLogger.d(TAG) { "LocalHazeStyle changed. Current: $field. New: $value" }
//                onStyleChanged(field, value)
//                field = value
//            }
//        }
//
//    override var style: HazeStyle = style
//        set(value) {
//            if (field != value) {
//                HazeLogger.d(TAG) { "style changed. Current: $field. New: $value" }
//                onStyleChanged(field, value)
//                field = value
//            }
//        }
//
//    internal var positionOnScreen: Offset = Offset.Unspecified
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "positionOnScreen changed. Current: $field. New: $value" }
//                dirtyTracker += DirtyFields.ScreenPosition
//                field = value
//            }
//        }
//
//    internal var rootBoundsOnScreen: Rect = Rect.Zero
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "rootBoundsOnScreen changed. Current: $field. New: $value" }
//                dirtyTracker += DirtyFields.ScreenPosition
//                field = value
//            }
//        }
//
//    private val areaOffsets = MutableObjectLongMap<HazeArea>()
//
//    internal var size: Size = Size.Unspecified
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "size changed. Current: $field. New: $value" }
//                dirtyTracker += DirtyFields.Size
//                field = value
//            }
//        }
//
//    internal var layerSize: Size = Size.Unspecified
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "layerSize changed. Current: $field. New: $value" }
//                dirtyTracker += DirtyFields.LayerSize
//                field = value
//            }
//        }
//
//    internal var layerOffset: Offset = Offset.Zero
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "layerOffset changed. Current: $field. New: $value" }
//                dirtyTracker += DirtyFields.LayerOffset
//                field = value
//            }
//        }
//
//    override var blurRadius: Dp = Dp.Unspecified
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "blurRadius changed. Current: $field. New: $value" }
//                dirtyTracker += DirtyFields.BlurRadius
//                field = value
//            }
//        }
//
//    override var noiseFactor: Float = -1f
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "noiseFactor changed. Current: $field. New: $value" }
//                dirtyTracker += DirtyFields.NoiseFactor
//                field = value
//            }
//        }
//
//    override var mask: Brush? = null
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "mask changed. Current: $field. New: $value" }
//                dirtyTracker += DirtyFields.Mask
//                field = value
//            }
//        }
//
//    override var backgroundColor: Color = Color.Unspecified
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "backgroundColor changed. Current: $field. New: $value" }
//                dirtyTracker += DirtyFields.BackgroundColor
//                field = value
//            }
//        }
//
//    override var tints: List<HazeTint> = emptyList()
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "tints changed. Current: $field. New: $value" }
//                dirtyTracker += DirtyFields.Tints
//                field = value
//            }
//        }
//
//    override var fallbackTint: HazeTint = HazeTint.Unspecified
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "fallbackTint changed. Current: $field. New: $value" }
//                dirtyTracker += DirtyFields.FallbackTint
//                field = value
//            }
//        }
//
//    override var alpha: Float = 1f
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "alpha changed. Current $field. New: $value" }
//                dirtyTracker += DirtyFields.Alpha
//                field = value
//            }
//        }
//
//    override var progressive: HazeProgressive? = null
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "progressive changed. Current $field. New: $value" }
//                dirtyTracker += DirtyFields.Progressive
//                field = value
//            }
//        }
//
//    internal var windowId: Any? = null
//
//    internal var areas: List<HazeArea> = emptyList()
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "backgroundAreas changed. Current $field. New: $value" }
//                dirtyTracker += DirtyFields.Areas
//
//                // Remove the pre-draw listener from the current areas
//                for (area in field) {
//                    area.preDrawListeners -= areaPreDrawListener
//                }
//                // Add the pre-draw listener to all of the new areas
//                for (area in value) {
//                    area.preDrawListeners += areaPreDrawListener
//                }
//                field = value
//            }
//        }
//
//    private val contentDrawArea by lazy { HazeArea() }
//
//    override var canDrawArea: ((HazeArea) -> Boolean)? = null
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "canDrawArea changed. Current $field. New: $value" }
//                field = value
//            }
//        }
//
//    internal var blurEffect: BlurEffect = ScrimBlurEffect(this)
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "blurEffect changed. Current $field. New: $value" }
//                // Cleanup the old value
//                field.cleanup()
//                field = value
//            }
//        }
//
//    override var blurredEdgeTreatment: BlurredEdgeTreatment = HazeDefaults.blurredEdgeTreatment
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "blurredEdgeTreatment. Current $field. New: $value" }
//                dirtyTracker += DirtyFields.BlurredEdgeTreatment
//                field = value
//            }
//        }
//
//    override var drawContentBehind: Boolean = HazeDefaults.drawContentBehind
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "drawContentBehind changed. Current $field. New: $value" }
//                dirtyTracker += DirtyFields.DrawContentBehind
//                field = value
//            }
//        }
//
//    override var clipToAreasBounds: Boolean? = null
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "clipToAreasBounds changed. Current $field. New: $value" }
//                dirtyTracker += DirtyFields.ClipToAreas
//                field = value
//            }
//        }
//
//    override var expandLayerBounds: Boolean? = null
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "expandLayer changed. Current $field. New: $value" }
//                dirtyTracker += DirtyFields.ExpandLayer
//                field = value
//            }
//        }
//
//    override var forceInvalidateOnPreDraw: Boolean = false
//        set(value) {
//            if (value != field) {
//                HazeLogger.d(TAG) { "forceInvalidateOnPreDraw changed. Current $field. New: $value" }
//                dirtyTracker += DirtyFields.ForcePreDraw
//                field = value
//            }
//        }
//    private val areaPreDrawListener by unsynchronizedLazy { OnPreDrawListener(::invalidateDraw) }
//
//    private fun onStyleChanged(old: HazeStyle?, new: HazeStyle?) {
//        if (old?.tints != new?.tints) dirtyTracker += DirtyFields.Tints
//        if (old?.fallbackTint != new?.fallbackTint) dirtyTracker += DirtyFields.Tints
//        if (old?.backgroundColor != new?.backgroundColor) dirtyTracker += DirtyFields.BackgroundColor
//        if (old?.noiseFactor != new?.noiseFactor) dirtyTracker += DirtyFields.NoiseFactor
//        if (old?.blurRadius != new?.blurRadius) dirtyTracker += DirtyFields.BlurRadius
//    }
//
//    internal fun update() {
//        onObservedReadsChanged()
//    }
//
//    override fun onAttach() {
//        update()
//    }
//
//    override fun onObservedReadsChanged() {
//        observeReads(::updateEffect)
//    }
//
//    override fun onPlaced(coordinates: LayoutCoordinates) {
//        // If the positionOnScreen has not been placed yet, we use the value on onPlaced,
//        // otherwise we ignore it. This primarily fixes screenshot tests which only run tests
//        // up to the first draw. We need onGloballyPositioned which tends to happen after
//        // the first pass
//        Snapshot.withoutReadObservation {
//            if (positionOnScreen.isUnspecified) {
//                onPositioned(coordinates, "onPlaced")
//            }
//        }
//    }
//
//    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
//        onPositioned(coordinates, "onGloballyPositioned")
//    }
//
//    private fun onPositioned(coordinates: LayoutCoordinates, source: String) {
//        if (!isAttached) {
//            // This shouldn't happen, but it does...
//            // https://github.com/chrisbanes/haze/issues/665
//            return
//        }
//
//        positionOnScreen = coordinates.positionForHaze()
//        size = coordinates.size.toSize()
//        windowId = getWindowId()
//
//        val rootLayoutCoords = coordinates.findRootCoordinates()
//        rootBoundsOnScreen = Rect(
//            offset = rootLayoutCoords.positionForHaze(),
//            size = rootLayoutCoords.size.toSize(),
//        )
//
//        HazeLogger.d(TAG) {
//            "$source: positionOnScreen=$positionOnScreen, size=$size"
//        }
//
//        updateEffect()
//    }
//
//    override fun ContentDrawScope.draw() {
//        try {
//            HazeLogger.d(TAG) { "-> start draw()" }
//
//            if (!isAttached) {
//                // This shouldn't happen, but it does...
//                // https://github.com/chrisbanes/haze/issues/665
//                return
//            }
//
//            if (size.isSpecified && layerSize.isSpecified) {
//                if (state != null) {
//                    if (areas.isNotEmpty()) {
//                        // If the state is not null and we have some areas, let's perform background blurring
//                        updateBlurEffectIfNeeded(this)
//                        with(blurEffect) { drawEffect() }
//                    }
//                    // Finally we draw the content over the background
//                    drawContentSafely()
//                } else {
//                    // Else we're doing content (foreground) blurring, so we need to use our
//                    // contentDrawArea
//                    val contentLayer = contentDrawArea.contentLayer
//                        ?.takeUnless { it.isReleased }
//                        ?: requireGraphicsContext().createGraphicsLayer().also {
//                            contentDrawArea.contentLayer = it
//                            HazeLogger.d(TAG) { "Updated contentLayer in content HazeArea" }
//                        }
//                    // Record the this node's content into the layer
//                    contentLayer.record(size.toIntSize()) {
//                        this@draw.drawContentSafely()
//                    }
//                    updateBlurEffectIfNeeded(this)
//                    if (drawContentBehind || blurEffect is ScrimBlurEffect) {
//                        // We need to draw the content for scrims
//                        drawLayer(contentLayer)
//                    }
//                    with(blurEffect) { drawEffect() }
//                }
//            } else {
//                HazeLogger.d(TAG) { "-> State not valid, so no need to draw effect." }
//                drawContentSafely()
//            }
//        } finally {
//            onPostDraw()
//            HazeLogger.d(TAG) { "-> end draw()" }
//        }
//    }
//
//    private fun updateEffect(): Unit = trace("HazeEffectNode-updateEffect") {
//        if (!isAttached) return@trace
//
//        compositionLocalStyle = currentValueOf(LocalHazeStyle)
//        windowId = getWindowId()
//
//        // Invalidate if any of the effects triggered an invalidation, or we now have zero
//        // effects but were previously showing some
//        block?.invoke(this)
//
//        val backgroundBlurring = state != null
//
//        areas.forEach { area ->
//            // Remove our pre draw listener from the current areas
//            area.preDrawListeners -= areaPreDrawListener
//        }
//
//        areas = if (backgroundBlurring) {
//            val ancestorSourceNode =
//                (findNearestAncestor(HazeTraversableNodeKeys.Source) as? HazeSourceNode)
//                    ?.takeIf { it.state == this.state }
//
//            state?.areas.orEmpty()
//                .also {
//                    HazeLogger.d(TAG) { "Background Areas observing: $it" }
//                }
//
//                .asSequence()
//                .filter { area ->
//                    val filter = canDrawArea
//                    when {
//                        filter != null -> filter(area)
//                        ancestorSourceNode != null -> area.zIndex < ancestorSourceNode.zIndex
//                        else -> true
//                    }.also { included ->
//                        HazeLogger.d(TAG) { "Background Area: $area. Included=$included" }
//                    }
//                }
//                .toMutableList()
//                .apply { sortBy(HazeArea::zIndex) }
//        } else {
//            contentDrawArea.size = size
//            contentDrawArea.positionOnScreen = positionOnScreen
//            contentDrawArea.windowId = windowId
//            listOf(contentDrawArea)
//        }
//
//        if (shouldUsePreDrawListener()) {
//            for (area in areas) {
//                area.preDrawListeners += areaPreDrawListener
//            }
//        }
//
//        updateAreaOffsets()
//
//        val blurRadiusPx = with(currentValueOf(LocalDensity)) {
//            resolveBlurRadius().takeOrElse { 0.dp }.toPx()
//        }
//
//        if (backgroundBlurring && areas.isNotEmpty() && size.isSpecified && positionOnScreen.isSpecified) {
//            val blurRadiusPx = with(requireDensity()) {
//                resolveBlurRadius().takeOrElse { 0.dp }.toPx()
//            }
//
//            // Now we clip the expanded layer bounds, to remove anything areas which
//            // don't overlap any areas, and the window bounds
//            val clippedLayerBounds = Rect(positionOnScreen, size)
//                .letIf(shouldExpandLayer()) { it.inflate(blurRadiusPx) }
//                .letIf(shouldClipToAreaBounds()) { rect ->
//                    // Calculate the dimensions which covers all areas...
//                    var left = Float.POSITIVE_INFINITY
//                    var top = Float.POSITIVE_INFINITY
//                    var right = Float.NEGATIVE_INFINITY
//                    var bottom = Float.NEGATIVE_INFINITY
//                    for (area in areas) {
//                        val bounds = area.bounds ?: continue
//                        left = min(left, bounds.left)
//                        top = min(top, bounds.top)
//                        right = max(right, bounds.right)
//                        bottom = max(bottom, bounds.bottom)
//                    }
//                    rect.intersect(left, top, right, bottom)
//                }
//                .intersect(rootBoundsOnScreen)
//
//            layerSize = Size(
//                width = clippedLayerBounds.width.coerceAtLeast(0f),
//                height = clippedLayerBounds.height.coerceAtLeast(0f),
//            )
//            layerOffset = positionOnScreen - clippedLayerBounds.topLeft
//        } else if (!backgroundBlurring && size.isSpecified && !shouldClip()) {
//            layerSize = Size(
//                width = size.width + (blurRadiusPx * 2),
//                height = size.height + (blurRadiusPx * 2),
//            )
//            layerOffset = Offset(blurRadiusPx, blurRadiusPx)
//        } else {
//            layerSize = size
//            layerOffset = Offset.Zero
//        }
//
//        invalidateIfNeeded()
//    }
//
//    private fun onPostDraw() {
//        dirtyTracker = Bitmask()
//    }
//
//    private fun invalidateIfNeeded() {
//        val invalidateRequired = dirtyTracker.any(DirtyFields.InvalidateFlags)
//        HazeLogger.d(TAG) {
//            "invalidateRequired=$invalidateRequired. " +
//                    "Dirty params=${DirtyFields.stringify(dirtyTracker)}"
//        }
//        if (invalidateRequired) {
//            invalidateDraw()
//        }
//    }
//
//    private fun updateAreaOffsets() {
//        // Calculate new offsets and detect changes for diff tracking
//        val hasAreaOffsetsChanged = when {
//            areaOffsets.size != areas.size -> true
//            else -> {
//                areas.any { area ->
//                    val newOffset = positionOnScreen - area.positionOnScreen
//                    !areaOffsets.contains(area) || areaOffsets[area] != newOffset.packedValue
//                }
//            }
//        }
//
//        if (hasAreaOffsetsChanged) {
//            HazeLogger.d(TAG) { "areaOffsets changed" }
//            dirtyTracker += DirtyFields.AreaOffsets
//
//            areaOffsets.clear()
//            areas.forEach { area ->
//                val offset = positionOnScreen - area.positionOnScreen
//                areaOffsets[area] = offset.packedValue
//            }
//        }
//    }
//
//    internal companion object {
//        const val TAG = "HazeEffect"
//    }
//}