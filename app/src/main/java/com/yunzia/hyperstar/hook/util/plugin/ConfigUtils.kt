package com.yunzia.hyperstar.hook.util.plugin

import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.callMethod
import com.yunzia.hyperstar.hook.core.helper.getStaticObjectField

class ConfigUtils(private val classLoader: ClassLoader?) {

    private var configUtils:Class<*> = findClass("miui.systemui.controlcenter.ConfigUtils",classLoader)!!


    val INSTANCE by lazy {
        configUtils.getStaticObjectField("INSTANCE")
    }


    fun textAppearanceChanged(configuration: Any?):Boolean{
        return INSTANCE.callMethod("textAppearanceChanged",configuration) as Boolean
    }





}