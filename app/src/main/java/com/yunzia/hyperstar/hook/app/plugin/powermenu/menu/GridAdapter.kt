package com.yunzia.hyperstar.hook.app.plugin.powermenu.menu

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Outline
import android.graphics.drawable.GradientDrawable
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.graphics.toColorInt
import com.yunzia.hyperstar.hook.app.plugin.powermenu.base.MenuItem
import yunzia.utils.DensityUtil

private val COLOR_SELECTED = "#277af7".toColorInt()
private val COLOR_DEFAULT = "#59FFFFFF".toColorInt()

class GridAdapter(private val context: Context, private val items: List<MenuItem?>, private val itemClick: () -> Unit) : BaseAdapter() {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): MenuItem = items[position]!!

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: createItem(context, items[position]!!)
        val item = getItem(position)
        item.state

        view.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            item.click?.invoke(it, it.context)
            updateBackground(it)
        }
        return view
    }

    private fun updateBackground(view: View) {
        val bg = view.background as? GradientDrawable ?: return
        bg.setColor(if (view.isSelected) COLOR_SELECTED else COLOR_DEFAULT)
        view.invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createItem(context: Context, item: MenuItem): View {
        val res = context.resources
        val sliderWidth = res.getIdentifier("slider_width", "dimen", PLUGIN_PACKAGE)
        val size = res.getDimensionPixelOffset(sliderWidth) - DensityUtil.dpToPx(res, 5f).toInt()

        return ImageView(context).apply {
            layoutParams = FrameLayout.LayoutParams(size, size)
            clipToOutline = true
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View?, outline: Outline?) {
                    val s = view!!.width.coerceAtMost(view.height)
                    outline?.setOval(0, 0, s, s)
                }
            }
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setPadding(50, 50, 50, 50)
            if (item.isEmpty == false) {
                setImageDrawable(item.image)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(if (item.state) COLOR_SELECTED else COLOR_DEFAULT)
                }
                isSelected = item.state
            }
            setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> { animateScale(0.8f); true }
                    MotionEvent.ACTION_UP -> { animateScale(1.0f); v.performClick(); true }
                    MotionEvent.ACTION_CANCEL -> { animateScale(1.0f); true }
                    else -> false
                }
            }
        }
    }
}