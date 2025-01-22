package com.yunzia.hyperstar.hook.util.plugin

import de.robv.android.xposed.XposedHelpers

class ConfigUtils(classLoader: ClassLoader?) {

    private var configUtils:Class<*> = XposedHelpers.findClass("miui.systemui.controlcenter.ConfigUtils",classLoader)


    val INSTANCE = XposedHelpers.getStaticObjectField(configUtils,"INSTANCE")


    fun textAppearanceChanged(configuration: Any?):Boolean{
        return XposedHelpers.callMethod(INSTANCE,"textAppearanceChanged",configuration) as Boolean
    }





}