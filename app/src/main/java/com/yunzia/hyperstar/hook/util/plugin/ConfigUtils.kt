package com.yunzia.hyperstar.hook.util.plugin

import de.robv.android.xposed.XposedHelpers

class ConfigUtils(private val classLoader: ClassLoader?) {

    private var configUtils:Class<*> = XposedHelpers.findClass("miui.systemui.controlcenter.ConfigUtils",classLoader)


    val INSTANCE by lazy {
        XposedHelpers.getStaticObjectField(configUtils,"INSTANCE")
    }


    fun textAppearanceChanged(configuration: Any?):Boolean{
        return XposedHelpers.callMethod(INSTANCE,"textAppearanceChanged",configuration) as Boolean
    }





}