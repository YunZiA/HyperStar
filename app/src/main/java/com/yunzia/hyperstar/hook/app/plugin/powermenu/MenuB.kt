package com.yunzia.hyperstar.hook.app.plugin.powermenu

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import yunzia.utils.DensityUtil.Companion.dpToPx
import com.yunzia.hyperstar.hook.app.plugin.powermenu.PowerMenu.Item
import de.robv.android.xposed.XposedHelpers


private val plugin = "miui.systemui.plugin"

fun menuB(mContext: Context, thisObj: Any?, items: List<Item?>, mTalkbackLayout: FrameLayout, mSliderView: FrameLayout): View {
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
    val width = size/4*3*2+size/5*4*2+size + dpToPx(res,20f).toInt()
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

fun ButtonB(
    mContext: Context,
    item: Item,
    itemClick: () -> Unit
):View{
    val res = mContext.resources
    val sliderWidth = res.getIdentifier("slider_width","dimen",plugin)
    val sliderWidths = res.getDimensionPixelOffset(sliderWidth)
    val size = sliderWidths/4*3

    val imageView = ImageView(mContext).apply {
        scaleType =  ImageView.ScaleType.CENTER_INSIDE
        setImageDrawable(item.image)
        //setPadding(50,50,50,50)
        layoutParams = LinearLayout.LayoutParams(size, size)
        //background = drawable

    }
    val string = TextView(mContext).apply {
        text = item.text
        //setPadding(-size/2,0,0,0)
        textSize = 12f
        gravity = Gravity.CENTER
        setTextColor(Color.WHITE)
        textAlignment = TextView.TEXT_ALIGNMENT_VIEW_START
        layoutParams = LinearLayout.LayoutParams(sliderWidths/5*4, size).apply {
            gravity = Gravity.CENTER
            marginStart = -size/8
            marginEnd = size/8
        }
    }

    val grop = LinearLayout(mContext).apply {
        background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 120f
            setColor(Color.parseColor("#40FFFFFF"))
        }
        addView(imageView)
        addView(string)
        pressAnimClick()
        setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

            item.click?.invoke(imageView,mContext)
            itemClick()
        }

    }

    return grop
}

fun  View.pressAnimClick() {

    val mHandler = Handler(Looper.getMainLooper());
    var isLongPressing = false;
    val mLongPressRunnable = {
        isLongPressing = true
    }



    setOnTouchListener { v,  event ->

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mHandler.postDelayed(mLongPressRunnable, 500);
                // 开始按压动画
                val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                    this,
                    PropertyValuesHolder.ofFloat("scaleX", 0.8f),
                    PropertyValuesHolder.ofFloat("scaleY", 0.8f)
                ).apply {
                    duration = 150
                }
                scaleDown.start()

            }
            MotionEvent.ACTION_UP -> {
                mHandler.removeCallbacks(mLongPressRunnable);

                if (!isLongPressing) {
                    // 如果不是长按，则认为是单击
                    v.performClick()
                } else {
                    // 重置长按标志
                    isLongPressing = false;
                }
                val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
                    this,
                    PropertyValuesHolder.ofFloat("scaleX", 1.0f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.0f)
                ).apply {
                    duration = 150
                }
                scaleUp.start()

                //

            }
            MotionEvent.ACTION_CANCEL -> {
                // 结束按压动画
                val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
                    this,
                    PropertyValuesHolder.ofFloat("scaleX", 1.0f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.0f)
                ).apply {
                    duration = 150
                }
                scaleUp.start()

            }

        }
        true
    }

}
