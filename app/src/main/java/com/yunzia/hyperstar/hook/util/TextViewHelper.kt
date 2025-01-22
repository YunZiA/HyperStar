package com.yunzia.hyperstar.hook.util

import android.text.TextUtils
import android.widget.TextView

fun TextView.startMarqueeOfFading(fadingEdgeLength:Int){
    ellipsize = TextUtils.TruncateAt.MARQUEE
    isFocusable = true
    isSelected = true
    isSingleLine = true
    marqueeRepeatLimit = 3
    isHorizontalFadingEdgeEnabled = true
    setFadingEdgeLength(fadingEdgeLength)
    forceHasOverlappingRendering(false)
}