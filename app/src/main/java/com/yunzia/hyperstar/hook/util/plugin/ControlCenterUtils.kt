package com.yunzia.hyperstar.hook.util.plugin

import android.content.Context
import de.robv.android.xposed.XposedHelpers

class ControlCenterUtils(classLoader: ClassLoader?) {


    private val controlCenterUtils = XposedHelpers.findClass("miui.systemui.controlcenter.utils.ControlCenterUtils",classLoader)
    fun getBackgroundBlurOpenedInDefaultTheme(context: Context) = XposedHelpers.callStaticMethod(controlCenterUtils,"getBackgroundBlurOpenedInDefaultTheme",context) as Boolean

}