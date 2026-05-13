package com.yunzia.hyperstar.hook.app.plugin.powermenu.menu

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.yunzia.hyperstar.hook.core.helper.callMethod

internal const val PLUGIN_PACKAGE = "miui.systemui.plugin"

internal fun View.playExpandAnimation(targetWidth: Int) {
    ValueAnimator.ofInt(0, targetWidth).apply {
        duration = 300
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val params = layoutParams
            params.width = value
            layoutParams = params
        }
        start()
    }
    ValueAnimator.ofFloat(0.4f, 1f).apply {
        duration = 250
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener { animation ->
            alpha = animation.animatedValue as Float
        }
        start()
    }
}

internal fun View.animateScale(targetScale: Float, duration: Long = 150) {
    ObjectAnimator.ofPropertyValuesHolder(
        this,
        PropertyValuesHolder.ofFloat("scaleX", targetScale),
        PropertyValuesHolder.ofFloat("scaleY", targetScale)
    ).apply {
        this.duration = duration
        start()
    }
}

internal fun dismissAfterDelay(thisObj: Any?) {
    Handler(Looper.getMainLooper()).postDelayed({
        thisObj.callMethod("dismiss", 1)
    }, 100)
}
