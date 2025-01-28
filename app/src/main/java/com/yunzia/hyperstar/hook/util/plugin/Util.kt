package com.yunzia.hyperstar.hook.util.plugin

import android.content.Context
import android.view.View
import de.robv.android.xposed.XposedHelpers


class Util(classLoader: ClassLoader?) {

    private val util = XposedHelpers.findClass("com.android.systemui.miui.volume.Util",classLoader)

    val DEBUG = XposedHelpers.getStaticBooleanField(util,"DEBUG")

    fun setMiViewBlurAndBlendColor(
        view: View,
        z: Boolean,
        context: Context,
        i: Int,
        iArr: IntArray,
        z2: Boolean

    ){  XposedHelpers.callStaticMethod(util,"setMiViewBlurAndBlendColor", view, z, context, i, iArr, z2) }

    fun setVisOrGone(
        view: View,
        visOrGone: Boolean
    ){
        XposedHelpers.callStaticMethod(util,"setVisOrGone",view,visOrGone)
    }


}