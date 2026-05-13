package com.yunzia.hyperstar.hook.util.plugin

import android.content.Context
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.callStaticMethod

class ControlCenterUtils(private val classLoader: ClassLoader?) {

    private val controlCenterUtils = findClass("miui.systemui.controlcenter.utils.ControlCenterUtils",classLoader)
    fun getBackgroundBlurOpenedInDefaultTheme(context: Context) = controlCenterUtils.callStaticMethod("getBackgroundBlurOpenedInDefaultTheme",context) as Boolean

}