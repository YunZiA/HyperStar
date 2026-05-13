package com.yunzia.hyperstar.hook.app.plugin

import android.content.Context
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.prefs.XSPUtils

object SuperBlurWidgetManager : BasePluginHook() {

    val superBlurWidget = XSPUtils.getInt("is_super_blur_Widget",0)

    override fun init() {
        if (superBlurWidget == 0) return
        findClass(
            "miui.systemui.controlcenter.utils.ControlCenterUtils",
            pluginClassLoader
        ).afterHookMethod("getBackgroundBlurOpenedInDefaultTheme", Context::class.java) { args, result ->
            if (superBlurWidget == 1) {
                result.replace(false)
            } else if (superBlurWidget == 2) {
                result.replace(true)
            }
        }
    }
}