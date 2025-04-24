package com.yunzia.hyperstar.hook.util.plugin

import android.view.View
import de.robv.android.xposed.XposedHelpers

class MiBlurCompat(private val classLoader: ClassLoader?) {

    private val miBlurCompat = XposedHelpers.findClass("miui.systemui.util.MiBlurCompat",classLoader)

    fun setMiViewBlurModeCompat(view: View,mode:Int){
        XposedHelpers.callStaticMethod(miBlurCompat,"setMiViewBlurModeCompat",view,mode)
    }

    fun clearMiBackgroundBlendColorCompat(view: View){

        XposedHelpers.callStaticMethod(miBlurCompat,"clearMiBackgroundBlendColorCompat",view)
    }

    fun setMiBackgroundBlurRadiusCompat(view: View,radius:Int){

        XposedHelpers.callStaticMethod(miBlurCompat,"setMiBackgroundBlurRadiusCompat",view,radius)
    }

    fun setMiBackgroundBlendColors(view: View?, colorArray:IntArray, float: Float){

        XposedHelpers.callStaticMethod(miBlurCompat, "setMiBackgroundBlendColors",view,colorArray,float)
    }

    fun chooseBackgroundBlurContainerCompat(view: View,view2: View?){

        try {
            XposedHelpers.callStaticMethod(miBlurCompat, "chooseBackgroundBlurContainerCompat",view,view2)
        }  catch (e: NoSuchMethodError) {
            View::class.java.getMethod("chooseBackgroundBlurContainer", View::class.java).invoke(view,view2)

            //XposedHelpers.callMethod(view, "chooseBackgroundBlurContainerCompat",view2)
        }
    }

}