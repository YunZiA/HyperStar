package com.yunzia.hyperstar.hook.app.plugin

import android.content.Context
import com.yunzia.hyperstar.hook.core.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.prefs.XSPUtils

object SuperBlurWidgetManager : BasePluginHook() {

    val superBlurWidget = XSPUtils.getInt("is_super_blur_Widget",0)

    override fun init() {
        
        if (superBlurWidget != 0){
            startMethodsHook()

        }
    }

    private fun startMethodsHook() {
        val controlCenterUtils  = findClass("miui.systemui.controlcenter.utils.ControlCenterUtils",pluginClassLoader)
        controlCenterUtils.afterHookMethod("getBackgroundBlurOpenedInDefaultTheme",Context::class.java){

            if (superBlurWidget == 1){
                it.result = false

            }else if (superBlurWidget == 2){
                it.result = true

            }
        }


    }
}