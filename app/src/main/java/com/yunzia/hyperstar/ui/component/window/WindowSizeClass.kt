package com.yunzia.hyperstar.ui.component.window

class WindowSizeClass(
    /** Returns the lower bound for the width of the size class in dp. */
    val minWidthDp: Int,
    /** Returns the lower bound for the height of the size class in dp. */
    val minHeightDp: Int,
) {

    /** A convenience constructor that will truncate to ints. */
    constructor(widthDp: Float, heightDp: Float) : this(widthDp.toInt(), heightDp.toInt())

    init {
        require(minWidthDp >= 0) {
            "Expected minWidthDp to be at least 0, minWidthDp: $minWidthDp."
        }
        require(minHeightDp >= 0) {
            "Expected minHeightDp to be at least 0, minHeightDp: $minHeightDp."
        }
    }

    /**
     * Returns `true` when [minWidthDp] is greater than or equal to [widthDpBreakpoint], `false`
     * otherwise. When processing a [WindowSizeClass] note that this method is order dependent.
     * Selection should go from largest to smallest breakpoints.
     *
     * @sample androidx.window.core.samples.layout.processWindowSizeClassWidthOnly
     */
    fun isWidthAtLeastBreakpoint(widthDpBreakpoint: Int): Boolean {
        return minWidthDp >= widthDpBreakpoint
    }

    /**
     * Returns `true` when [minHeightDp] is greater than or equal to [heightDpBreakpoint], `false`
     * otherwise. When processing a [WindowSizeClass] note that this method is order dependent.
     * Selection should go from largest to smallest breakpoints.
     */
    fun isHeightAtLeastBreakpoint(heightDpBreakpoint: Int): Boolean {
        return minHeightDp >= heightDpBreakpoint
    }

    /**
     * Returns `true` when [minWidthDp] is greater than or equal to [widthDpBreakpoint] and
     * [minHeightDp] is greater than or equal to [heightDpBreakpoint], `false` otherwise. When
     * processing a [WindowSizeClass] note that this method is order dependent. Selection should go
     * from largest to smallest breakpoints.
     */
    fun isAtLeastBreakpoint(widthDpBreakpoint: Int, heightDpBreakpoint: Int): Boolean {
        return isWidthAtLeastBreakpoint(widthDpBreakpoint) &&
                isHeightAtLeastBreakpoint(heightDpBreakpoint)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as WindowSizeClass

        if (minWidthDp != other.minWidthDp) return false
        if (minHeightDp != other.minHeightDp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = minWidthDp
        result = 31 * result + minHeightDp
        return result
    }

    override fun toString(): String {
        return "WindowSizeClass(minWidthDp=$minWidthDp, minHeightDp=$minHeightDp)"
    }

    companion object {
        /** A lower bound for a size class with Medium width in dp. */
        const val WIDTH_DP_MEDIUM_LOWER_BOUND: Int = 600

        /** A lower bound for a size class with Expanded width in dp. */
        const val WIDTH_DP_EXPANDED_LOWER_BOUND: Int = 840

        /** A lower bound for a size class with Large width in dp. */
        const val WIDTH_DP_LARGE_LOWER_BOUND: Int = 1200

        /** A lower bound for a size class width Extra Large width in dp. */
        const val WIDTH_DP_EXTRA_LARGE_LOWER_BOUND: Int = 1600

        /** A lower bound for a size class with Medium height in dp. */
        const val HEIGHT_DP_MEDIUM_LOWER_BOUND: Int = 480

        /** A lower bound for a size class with Expanded height in dp. */
        const val HEIGHT_DP_EXPANDED_LOWER_BOUND: Int = 900

        private val WIDTH_DP_BREAKPOINTS_V1 =
            listOf(0, WIDTH_DP_MEDIUM_LOWER_BOUND, WIDTH_DP_EXPANDED_LOWER_BOUND)

        private val WIDTH_DP_BREAKPOINTS_V2 =
            WIDTH_DP_BREAKPOINTS_V1 +
                    listOf(WIDTH_DP_LARGE_LOWER_BOUND, WIDTH_DP_EXTRA_LARGE_LOWER_BOUND)

        private val HEIGHT_DP_BREAKPOINTS_V1 =
            listOf(0, HEIGHT_DP_MEDIUM_LOWER_BOUND, HEIGHT_DP_EXPANDED_LOWER_BOUND)

        private val HEIGHT_DP_BREAKPOINTS_V2 = HEIGHT_DP_BREAKPOINTS_V1

        private fun createBreakpointSet(
            widthBreakpoints: List<Int>,
            heightBreakpoints: List<Int>,
        ): Set<WindowSizeClass> {
            return widthBreakpoints
                .flatMap { widthBp ->
                    heightBreakpoints.map { heightBp ->
                        WindowSizeClass(minWidthDp = widthBp, minHeightDp = heightBp)
                    }
                }
                .toSet()
        }

        /**
         * The recommended breakpoints for window size classes.
         *
         * @sample androidx.window.core.samples.layout.calculateWindowSizeClass
         */
        @JvmField
        val BREAKPOINTS_V1: Set<WindowSizeClass> =
            createBreakpointSet(WIDTH_DP_BREAKPOINTS_V1, HEIGHT_DP_BREAKPOINTS_V1)

        /**
         * The recommended breakpoints for window size classes. This includes all the breakpoints
         * from [BREAKPOINTS_V1] plus new breakpoints to account for the Large and Extra Large width
         * breakpoints.
         *
         * @sample androidx.window.core.samples.layout.calculateWindowSizeClass
         */
        @JvmField
        val BREAKPOINTS_V2: Set<WindowSizeClass> =
            createBreakpointSet(WIDTH_DP_BREAKPOINTS_V2, HEIGHT_DP_BREAKPOINTS_V2)

        /**
         * Computes the recommended [WindowSizeClass] for the given width and height in DP.
         *
         * @param dpWidth width of a window in DP.
         * @param dpHeight height of a window in DP.
         * @return [WindowSizeClass] that is recommended for the given dimensions.
         * @throws IllegalArgumentException if [dpWidth] or [dpHeight] is negative.
         */
        @JvmStatic
        @Deprecated(
            "Use computeWindowSizeClass instead.",
            ReplaceWith(
                "BREAKPOINTS_V1.computeWindowSizeClass(widthDp = dpWidth, heightDp = dpHeight)",
                "androidx.window.core.layout.computeWindowSizeClass",
            ),
        )
        fun compute(dpWidth: Float, dpHeight: Float): WindowSizeClass {
            val widthDp =
                when {
                    dpWidth >= WIDTH_DP_EXPANDED_LOWER_BOUND -> WIDTH_DP_EXPANDED_LOWER_BOUND
                    dpWidth >= WIDTH_DP_MEDIUM_LOWER_BOUND -> WIDTH_DP_MEDIUM_LOWER_BOUND
                    else -> 0
                }
            val heightDp =
                when {
                    dpHeight >= HEIGHT_DP_EXPANDED_LOWER_BOUND -> HEIGHT_DP_EXPANDED_LOWER_BOUND
                    dpHeight >= HEIGHT_DP_MEDIUM_LOWER_BOUND -> HEIGHT_DP_MEDIUM_LOWER_BOUND
                    else -> 0
                }
            return WindowSizeClass(widthDp, heightDp)
        }
    }
}