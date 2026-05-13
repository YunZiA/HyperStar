package com.yunzia.hyperstar.hook.util.plugin

import android.content.Context
import android.view.View
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.callStaticMethod
import com.yunzia.hyperstar.hook.core.helper.getStaticBooleanField


class Util(classLoader: ClassLoader?) {

    private val util = findClass("com.android.systemui.miui.volume.Util",classLoader)

    val DEBUG = util.getStaticBooleanField("DEBUG")

    fun setMiViewBlurAndBlendColor(
        view: View,
        z: Boolean,
        context: Context,
        i: Int,
        iArr: IntArray,
        z2: Boolean

    ){  util.callStaticMethod("setMiViewBlurAndBlendColor", view, z, context, i, iArr, z2) }

    fun setVisOrGone(
        view: View,
        visOrGone: Boolean
    ){
        util.callStaticMethod("setVisOrGone",view,visOrGone)
    }


}