package com.yunzia.hyperstar.hook.app.plugin.powermenu.menu

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.LinearLayout
import com.yunzia.hyperstar.hook.app.plugin.powermenu.base.MenuItem
import com.yunzia.hyperstar.hook.base.getDimensionPixelOffset
import yunzia.utils.DensityUtil.Companion.dpToPx

fun menuA(mContext: Context, thisObj: Any?, items: List<MenuItem?>, mTalkbackLayout: FrameLayout, mSliderView: FrameLayout): View {
    val res = mContext.resources
    val topItems = if (items.size > 4) items.subList(0, 4) else items

    val topMenu = GridView(mContext).apply {
        gravity = Gravity.CENTER
        numColumns = topItems.size
        horizontalSpacing = dpToPx(res, 10f).toInt()
        adapter = GridAdapter(mContext, topItems) { dismissAfterDelay(thisObj) }
    }

    val bottomMenu = if (items.size > 4) {
        GridView(mContext).apply {
            gravity = Gravity.CENTER
            numColumns = items.size - 4
            horizontalSpacing = dpToPx(res, 10f).toInt()
            adapter = GridAdapter(mContext, items.subList(4, items.size)) { dismissAfterDelay(thisObj) }
        }
    } else {
        View(mContext)
    }

    val size = getDimensionPixelOffset(res, "slider_width", PLUGIN_PACKAGE)
    val width = size * 4 + dpToPx(res, 10f).toInt()
    val height = getDimensionPixelOffset(res, "slider_height", PLUGIN_PACKAGE) + dpToPx(res, 230f).toInt()

    val group = LinearLayout(mContext).apply {
        orientation = LinearLayout.VERTICAL
        addView(topMenu, wrapContentLP(Gravity.TOP or Gravity.CENTER_HORIZONTAL))
        addView(View(mContext), LinearLayout.LayoutParams(0, 0).apply { weight = 1f })
        addView(bottomMenu, wrapContentLP(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL))
    }

    mTalkbackLayout.addView(group, FrameLayout.LayoutParams(width, height).apply {
        gravity = Gravity.CENTER
    })
    group.playExpandAnimation(width)

    return group
}

private fun wrapContentLP(gravity: Int) = LinearLayout.LayoutParams(
    LinearLayout.LayoutParams.WRAP_CONTENT,
    LinearLayout.LayoutParams.WRAP_CONTENT
).apply { this.gravity = gravity }