package com.yunzia.hyperstar.hook.util.plugin

import android.view.View
import de.robv.android.xposed.XposedHelpers

class MiBlurCompat(classLoader: ClassLoader?) {

    private val miBlurCompat = XposedHelpers.findClass("miui.systemui.util.MiBlurCompat",classLoader)

    fun setMiViewBlurModeCompat(view: View,mode:Int){
        XposedHelpers.callStaticMethod(miBlurCompat,"setMiViewBlurModeCompat",view,mode)
    }

    fun clearMiBackgroundBlendColorCompat(view: View){

        XposedHelpers.callStaticMethod(miBlurCompat,"clearMiBackgroundBlendColorCompat",view)
    }

    fun setMiBackgroundBlendColors(view: View?, colorArray:IntArray, float: Float){

        XposedHelpers.callStaticMethod(miBlurCompat, "setMiBackgroundBlendColors",view,colorArray,float)
    }

}