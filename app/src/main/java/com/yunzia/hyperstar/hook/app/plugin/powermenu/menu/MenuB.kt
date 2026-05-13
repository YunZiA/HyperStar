package com.yunzia.hyperstar.hook.app.plugin.powermenu.menu

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.yunzia.hyperstar.hook.app.plugin.powermenu.base.MenuItem
import yunzia.utils.DensityUtil.Companion.dpToPx
import androidx.core.graphics.toColorInt

fun menuB(mContext: Context, thisObj: Any?, items: List<MenuItem?>, mTalkbackLayout: FrameLayout, mSliderView: FrameLayout): View {
    val res = mContext.resources
    val sliderWidth = res.getIdentifier("slider_width", "dimen", PLUGIN_PACKAGE)
    val size = res.getDimensionPixelOffset(sliderWidth)
    val width = size / 4 * 3 * 2 + size / 5 * 4 * 2 + size + dpToPx(res, 20f).toInt()

    val group = LinearLayout(mContext).apply {
        orientation = LinearLayout.HORIZONTAL
        addView(ButtonB(mContext, items[0]!!) { dismissAfterDelay(thisObj) }, wrapContentLP(Gravity.TOP or Gravity.CENTER_HORIZONTAL))
        addView(View(mContext), LinearLayout.LayoutParams(0, 0).apply { weight = 1f })
        addView(ButtonB(mContext, items[1]!!) { dismissAfterDelay(thisObj) }, wrapContentLP(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL))
    }

    mTalkbackLayout.addView(group, FrameLayout.LayoutParams(width, size / 4 * 3).apply {
        gravity = Gravity.CENTER
    })
    group.playExpandAnimation(width)

    return group
}

private fun wrapContentLP(gravity: Int) = LinearLayout.LayoutParams(
    LinearLayout.LayoutParams.WRAP_CONTENT,
    LinearLayout.LayoutParams.WRAP_CONTENT
).apply { this.gravity = gravity }

fun ButtonB(mContext: Context, item: MenuItem, itemClick: () -> Unit): View {
    val res = mContext.resources
    val sliderWidth = res.getIdentifier("slider_width", "dimen", PLUGIN_PACKAGE)
    val sliderWidths = res.getDimensionPixelOffset(sliderWidth)
    val size = sliderWidths / 4 * 3

    val imageView = ImageView(mContext).apply {
        scaleType = ImageView.ScaleType.CENTER_INSIDE
        setImageDrawable(item.image)
        layoutParams = LinearLayout.LayoutParams(size, size)
    }

    val label = TextView(mContext).apply {
        text = item.text
        textSize = 12f
        gravity = Gravity.CENTER
        setTextColor(Color.WHITE)
        textAlignment = TextView.TEXT_ALIGNMENT_VIEW_START
        layoutParams = LinearLayout.LayoutParams(sliderWidths / 5 * 4, size).apply {
            gravity = Gravity.CENTER
            marginStart = -size / 8
            marginEnd = size / 8
        }
    }

    return LinearLayout(mContext).apply {
        background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 120f
            setColor("#40FFFFFF".toColorInt())
        }
        addView(imageView)
        addView(label)
        pressAnimClick()
        setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            item.click?.invoke(imageView, mContext)
            itemClick()
        }
    }
}

fun View.pressAnimClick() {
    val handler = Handler(Looper.getMainLooper())
    var isLongPressing = false
    val longPressRunnable = Runnable { isLongPressing = true }

    setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handler.postDelayed(longPressRunnable, 500)
                animateScale(0.8f)
            }
            MotionEvent.ACTION_UP -> {
                handler.removeCallbacks(longPressRunnable)
                if (!isLongPressing) v.performClick() else isLongPressing = false
                animateScale(1.0f)
            }
            MotionEvent.ACTION_CANCEL -> animateScale(1.0f)
        }
        true
    }
}
