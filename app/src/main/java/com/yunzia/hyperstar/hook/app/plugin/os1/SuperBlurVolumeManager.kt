package com.yunzia.hyperstar.hook.app.plugin.os1

import android.content.Context
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.utils.XSPUtils

class SuperBlurVolumeManager : Hooker() {

    val superBlurVolume = XSPUtils.getInt("is_super_blur_volume",0)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        if (superBlurVolume != 0){
            startMethodsHook()
        }
    }

    private fun startMethodsHook() {
        findClass(
            "miui.systemui.util.MiBlurCompat",
            classLoader
        ).afterHookMethod("getBackgroundBlurOpened",Context::class.java){

            if (superBlurVolume == 1){
                it.result = false

            }else if (superBlurVolume == 2){
                it.result = true

            }
        }

    }
}