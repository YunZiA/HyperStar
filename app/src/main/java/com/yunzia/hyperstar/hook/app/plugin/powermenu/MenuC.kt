package com.yunzia.hyperstar.hook.app.plugin.powermenu

import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.yunzia.hyperstar.hook.app.plugin.powermenu.PowerMenu.Item
import de.robv.android.xposed.XposedHelpers
import yunzia.utils.DensityUtil.Companion.dpToPx

private val plugin = "miui.systemui.plugin"

fun menuC(mContext: Context, thisObj: Any?, items: List<Item?>, mTalkbackLayout: FrameLayout, mSliderView: FrameLayout): View {
    val res = mContext.resources


    val menu1 = ButtonB(mContext,items[0]!!){
        Handler(Looper.getMainLooper()).postDelayed({
            XposedHelpers.callMethod(thisObj,"dismiss",1)
        }, 100)
    }

    val menu2 = ButtonB(mContext,items[1]!!){
        Handler(Looper.getMainLooper()).postDelayed({
            XposedHelpers.callMethod(thisObj,"dismiss",1)
        }, 100)
    }

    val m1 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT )
    m1.gravity = Gravity.TOP+ Gravity.CENTER_HORIZONTAL
    //m1.addRule(RelativeLayout.ALIGN_PARENT_TOP)

    val c = View(mContext)
    val m3 = LinearLayout.LayoutParams(0,0 ).apply {
        weight = 1f
    }

    val m2 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT )
    m2.gravity = Gravity.BOTTOM+ Gravity.CENTER_HORIZONTAL
    //m1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

    val sliderWidth = res.getIdentifier("slider_width","dimen",plugin)
    val size = res.getDimensionPixelOffset(sliderWidth)
    val width = size/4*3*2+size/5*4*2+size + dpToPx(res,15f).toInt()
    val sliderHeight = res.getIdentifier("slider_height","dimen",plugin)
    val top = res.getDimensionPixelOffset(sliderHeight)
    val height = top+ dpToPx(res,230f).toInt()


    val group = LinearLayout(mContext).apply {
        orientation = LinearLayout.HORIZONTAL
        //translationX = -100f
        addView(menu1,m1)
        addView(c,m3)
        addView(menu2,m2)

    }

    val layoutParams = FrameLayout.LayoutParams(width, size/4*3 )
    //val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT )
    layoutParams.gravity = Gravity.CENTER
    mTalkbackLayout.addView(group, layoutParams)


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