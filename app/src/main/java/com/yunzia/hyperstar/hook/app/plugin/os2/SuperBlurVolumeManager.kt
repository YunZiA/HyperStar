package com.yunzia.hyperstar.hook.app.plugin.os2

import android.content.Context
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.prefs.XSPUtils

object SuperBlurVolumeManager : BasePluginHook() {

    val superBlurVolume = XSPUtils.getInt("is_super_blur_volume",0)

    override fun init() {
        if (superBlurVolume == 0) return

        findClass(
            "miui.systemui.util.MiBlurCompat",
            pluginClassLoader
        ).afterHookMethod("getBackgroundBlurOpenedInDefaultTheme",Context::class.java) { args, result ->

            if (superBlurVolume == 1) {
                result.replace(false)
            }else if (superBlurVolume == 2){
                result.replace(true)
            }
        }
    }
}