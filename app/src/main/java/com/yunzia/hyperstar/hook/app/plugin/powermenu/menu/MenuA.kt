package com.yunzia.hyperstar.hook.os1.app.plugin.powermenu

import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.LinearLayout
import com.yunzia.hyperstar.hook.app.plugin.powermenu.base.MenuItem
import com.yunzia.hyperstar.hook.app.plugin.powermenu.menu.GridAdapter
import com.yunzia.hyperstar.hook.base.getDimensionPixelOffset
import de.robv.android.xposed.XposedHelpers
import yunzia.utils.DensityUtil.Companion.dpToPx

private val plugin = "miui.systemui.plugin"

fun menuA(mContext: Context, thisObj: Any?, items: List<MenuItem?>,mTalkbackLayout:FrameLayout,mSliderView: FrameLayout): View {
    val res = mContext.resources
    val num = items.size

    val items1 = if ( num > 4 ){
        items.subList(0,4)
    }else{
        items
    }


    val topMenu = GridView(mContext).apply {
        gravity = Gravity.CENTER
        numColumns = items1.size
        horizontalSpacing = dpToPx(res,10f).toInt()
        //verticalSpacing = dpToPx(res,14f).toInt()
        adapter = GridAdapter(mContext, items1) {
            Handler(Looper.getMainLooper()).postDelayed({
                XposedHelpers.callMethod(thisObj, "dismiss", 1)
            }, 100)
        }
    }
    val bottomMenu = if ( num > 4 ){
        GridView(mContext).apply {
            gravity = Gravity.CENTER
            numColumns = num-4
            horizontalSpacing = dpToPx(res,10f).toInt()
            adapter = GridAdapter(mContext, items.subList(4, num)) {
                Handler(Looper.getMainLooper()).postDelayed({
                    XposedHelpers.callMethod(thisObj, "dismiss", 1)
                }, 100)
            }
        }

    }else{
        View(mContext)
    }

    val topLP = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        gravity = Gravity.TOP + Gravity.CENTER_HORIZONTAL
    }

    val space = View(mContext)
    val spaceLP = LinearLayout.LayoutParams(0,0 ).apply {
        weight = 1f
    }

    val bottomLP = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        gravity = Gravity.BOTTOM + Gravity.CENTER_HORIZONTAL
    }


    val sliderWidth = res.getIdentifier("slider_width","dimen", plugin)
    val size = getDimensionPixelOffset(res,"slider_width",plugin)
    val width = size * 4 + dpToPx(res,10f).toInt()
    val sliderHeight = res.getIdentifier("slider_height","dimen", plugin)
    val top = getDimensionPixelOffset(res,"slider_height",plugin)
    val height = top + dpToPx(res,230f).toInt()


    val group = LinearLayout(mContext).apply {
        orientation = LinearLayout.VERTICAL
        //translationX = -100f
        addView(topMenu,topLP)
        addView(space,spaceLP)
        addView(bottomMenu,bottomLP)

    }

    val groupLP = FrameLayout.LayoutParams(width, height).apply {
        gravity = Gravity.CENTER
    }
    //val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT )

    mTalkbackLayout.addView(group, groupLP)

    val initialHeight = 0
    ValueAnimator.ofInt(initialHeight, width).apply {
        duration = 300  // 动画持续时间
        interpolator = AccelerateDecelerateInterpolator()  // 使用加速减速插值器
        addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            //menu1.scaleY = value
            val params = group.layoutParams
            params.width = value
            group.layoutParams = params
        }
        start()
    }
    ValueAnimator.ofFloat(0.4f, 1f).apply {
        duration = 250  // 动画持续时间
        interpolator = AccelerateDecelerateInterpolator()  // 使用加速减速插值器
        addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            group.alpha = value
        }
        start()
    }

    return group
}