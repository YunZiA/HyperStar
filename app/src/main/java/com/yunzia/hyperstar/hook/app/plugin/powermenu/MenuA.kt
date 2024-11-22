package com.yunzia.hyperstar.hook.app.plugin.powermenu

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.LinearLayout
import yunzia.utils.DensityUtil.Companion.dpToPx
import com.yunzia.hyperstar.hook.app.plugin.powermenu.PowerMenu.Item
import de.robv.android.xposed.XposedHelpers

private val plugin = "miui.systemui.plugin"

fun menuA(mContext: Context, thisObj: Any?, items: List<Item?>,mTalkbackLayout:FrameLayout,mSliderView: FrameLayout): View {
    val res = mContext.resources
    val num = items.size

    val drawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = 120f
        setColor(Color.parseColor("#40FFFFFF"))
    }



    //drawable.setSize(100, 100) // 设置大小，单位为像素
    //val padding = dpToPx(res,10.7f).toInt()
    val items1 = if ( num > 4 ){
        items.subList(0,4)
    }else{
        items
    }


    val menu1 = GridView(mContext).apply {
        gravity = Gravity.CENTER
        numColumns = items1.size
        horizontalSpacing = dpToPx(res,10f).toInt()
        //verticalSpacing = dpToPx(res,14f).toInt()
        adapter = GridAdapter(mContext,items1){
            Handler(Looper.getMainLooper()).postDelayed({
                XposedHelpers.callMethod(thisObj,"dismiss",1)
            }, 100)
        }
    }
    val menu2 = if ( num > 4 ){
        GridView(mContext).apply {
            gravity = Gravity.CENTER
            numColumns = num-4
            horizontalSpacing = dpToPx(res,10f).toInt()
            adapter = GridAdapter(mContext,items.subList(4,num)){
                Handler(Looper.getMainLooper()).postDelayed({
                    XposedHelpers.callMethod(thisObj,"dismiss",1)
                }, 100)
            }
        }

    }else{
        View(mContext)
    }

    val m1 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT )
    m1.gravity = Gravity.TOP+Gravity.CENTER_HORIZONTAL
    //m1.addRule(RelativeLayout.ALIGN_PARENT_TOP)

    val c = View(mContext)
    val m3 = LinearLayout.LayoutParams(0,0 ).apply {
        weight = 1f
    }

    val m2 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT )
    m2.gravity = Gravity.BOTTOM+Gravity.CENTER_HORIZONTAL
    //m1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

    val sliderWidth = res.getIdentifier("slider_width","dimen",plugin)
    val size = res.getDimensionPixelOffset(sliderWidth)
    val width = size * 4 + dpToPx(res,10f).toInt()
    val sliderHeight = res.getIdentifier("slider_height","dimen",plugin)
    val top = res.getDimensionPixelOffset(sliderHeight)
    val height = top+dpToPx(res,230f).toInt()


    val group = LinearLayout(mContext).apply {
        orientation = LinearLayout.VERTICAL
        //translationX = -100f
        addView(menu1,m1)
        addView(c,m3)
        addView(menu2,m2)

    }

    val layoutParams = FrameLayout.LayoutParams(width, height )
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