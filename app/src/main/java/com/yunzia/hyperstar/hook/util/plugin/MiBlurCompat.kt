package com.yunzia.hyperstar.hook.util.plugin

import android.view.View
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.callStaticMethod

class MiBlurCompat(private val classLoader: ClassLoader?) {

    private val miBlurCompat = findClass("miui.systemui.util.MiBlurCompat",classLoader)

    fun setMiViewBlurModeCompat(view: View,mode:Int){
        miBlurCompat.callStaticMethod("setMiViewBlurModeCompat",view,mode)
    }

    fun clearMiBackgroundBlendColorCompat(view: View){

        miBlurCompat.callStaticMethod("clearMiBackgroundBlendColorCompat",view)
    }

    fun setMiBackgroundBlurRadiusCompat(view: View,radius:Int){

        miBlurCompat.callStaticMethod("setMiBackgroundBlurRadiusCompat",view,radius)
    }

    fun setMiBackgroundBlurRadiusCompat(view: View,radius:Int){

        XposedHelpers.callStaticMethod(miBlurCompat,"setMiBackgroundBlurRadiusCompat",view,radius)
    }

    fun setMiBackgroundBlendColors(view: View?, colorArray:IntArray, float: Float){

        miBlurCompat.callStaticMethod( "setMiBackgroundBlendColors",view,colorArray,float)
    }

    fun chooseBackgroundBlurContainerCompat(view: View,view2: View?){

        try {
            miBlurCompat.callStaticMethod( "chooseBackgroundBlurContainerCompat",view,view2)
        }  catch (e: NoSuchMethodError) {
            View::class.java.getMethod("chooseBackgroundBlurContainer", View::class.java).invoke(view,view2)

            //XposedHelpers.callMethod(view, "chooseBackgroundBlurContainerCompat",view2)
        }
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