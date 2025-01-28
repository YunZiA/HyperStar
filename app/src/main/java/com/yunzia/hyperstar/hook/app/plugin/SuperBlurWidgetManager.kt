package com.yunzia.hyperstar.hook.app.plugin

import android.content.Context
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.utils.XSPUtils

class SuperBlurWidgetManager : Hooker() {

    val superBlurWidget = XSPUtils.getInt("is_super_blur_Widget",0)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        if (superBlurWidget != 0){
            startMethodsHook()

        }
    }

    private fun startMethodsHook() {
        val controlCenterUtils  = findClass("miui.systemui.controlcenter.utils.ControlCenterUtils",classLoader)
        controlCenterUtils.afterHookMethod("getBackgroundBlurOpenedInDefaultTheme",Context::class.java){

            if (superBlurWidget == 1){
                it.result = false

            }else if (superBlurWidget == 2){
                it.result = true

            }
        }


    }
}