package com.yunzia.hyperstar.hook.app.plugin.os1

import android.content.Context
import com.yunzia.hyperstar.hook.core.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.prefs.XSPUtils

object SuperBlurVolumeManager : BasePluginHook() {

    val superBlurVolume = XSPUtils.getInt("is_super_blur_volume",0)

    override fun init() {
        if (superBlurVolume != 0){
            startMethodsHook()
        }
    }

    private fun startMethodsHook() {
        findClass(
            "miui.systemui.util.MiBlurCompat",
            pluginClassLoader
        ).afterHookMethod("getBackgroundBlurOpened",Context::class.java){
            if (superBlurVolume == 1){
                it.result = false
            }else if (superBlurVolume == 2){
                it.result = true
            }
        }
    }
}