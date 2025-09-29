package com.yunzia.hyperstar.hook.app.systemui.os3

import android.animation.ArgbEvaluator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.graphics.alpha
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.afterHookConstructor
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.utils.XSPUtils


class SystemBarBackground : Hooker() {

    private val isTransparentNavigationBarBackground = XSPUtils.getBoolean("is_transparent_navigationBar_background",false)
    private val isTransparentStatusBarBackground = XSPUtils.getBoolean("is_transparent_statusBar_background",false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        if (!isTransparentNavigationBarBackground && !isTransparentStatusBarBackground) return

        findClass(
            "com.android.systemui.shared.statusbar.phone.BarTransitions",
            classLoader
        ).afterHookConstructor(
            Int::class.java,
            View::class.java
        ) {
            val view = it.args[1] as View

            val name = view.context.resources.getResourceEntryName(view.id)

            if ((isTransparentNavigationBarBackground && name == "navigation_bar_view") ||
                (isTransparentStatusBarBackground && name == "status_bar_container")
            ) {
                this.getObjectField("mBarBackground")?.setIntField("mSemiTransparent", 0)
            }

        }




    }

    fun gradientBackground(){

        findClass(
            "com.android.systemui.shared.statusbar.phone.BarTransitions\$BarBackgroundDrawable",
            classLoader
        ).afterHookMethod(
            "draw",
            Canvas::class.java
        ) {
            val mMode = this.getIntField("mMode")
            val mColor = this.getIntField("mColor")
            starLog.logD("SystemBarBackground","mColor = $mColor")
            if (mColor.alpha == 0) return@afterHookMethod
            when (mMode){
                1,2->{
                    val canvas = it.args[0] as Canvas
                    val mPaint = this.getObjectFieldAs<Paint>("mPaint")
                    val mFrame = this.getObjectFieldAs<Rect>("mFrame")
                    val mGradient = this.getObjectFieldAs<Drawable>("mGradient")
                    val mGradientAlpha = this.getIntField("mGradientAlpha")
                    val mSemiTransparent = this.getIntField("mSemiTransparent")
                    val bounds: Rect = if (mFrame != null) mFrame else this.callMethodAs<Rect>("getBounds")
                    if (bounds.isEmpty) {
                        return@afterHookMethod
                    }
                    val gradientStart: Int = Color.TRANSPARENT // 完全透明
                    val gradientEnd: Int = mColor // 半透明颜色
                    if (mGradientAlpha > 0){
                        mGradient.alpha = mGradientAlpha
                        mGradient.draw(canvas)
                    }

                    starLog.logD("SystemBarBackground","bounds.left = ${bounds.left}\nbounds.right = ${bounds.right}")
                    // 创建从下到上的渐变（常见于状态栏遮罩）
                    // 如果你想从上到下，交换 y 坐标
//                    val gradient = LinearGradient(
//                        bounds.left.toFloat(), bounds.bottom.toFloat(),
//                        bounds.left.toFloat(), bounds.top.toFloat(),
//                        gradientStart,
//                        gradientEnd,
//                        Shader.TileMode.CLAMP
//                    )

// 根据位置数组计算对应的颜色值，形成“先块后慢”的效果
                    val colorCount = 8 // 采样点数量，越多越平滑

                    val colors = IntArray(colorCount)
                    val positions = FloatArray(colorCount)
                    val interpolator = DecelerateInterpolator()

                    for (i in 0 until colorCount) {
                        val t = i * 1f / (colorCount - 1)
                        val interpolatedT = interpolator.getInterpolation(t)
                        positions[i] = interpolatedT
                        colors[i] = ArgbEvaluator().evaluate(t, gradientStart, gradientEnd) as Int
                    }

                    val gradient = LinearGradient(
                        bounds.left.toFloat(), bounds.bottom.toFloat(),
                        bounds.left.toFloat(), bounds.top.toFloat(),
                        colors, // 使用颜色数组
                        positions, // 使用位置数组
                        Shader.TileMode.CLAMP
                    )

                    mPaint.shader = gradient
                    canvas.drawRect(bounds, mPaint)
                    //mPaint.shader = null // 清除 shader 避免影响其他绘制

                    return@afterHookMethod
                }


            }


        }
    }



}