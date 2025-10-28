package com.yunzia.hyperstar.ui.component.card

enum class PivotPosition(val isTop: Boolean, val isLeft: Boolean) {
    TOP_LEFT(true, true),
    TOP_RIGHT(true, false),
    BOTTOM_LEFT(false, true),
    BOTTOM_RIGHT(false, false)
}