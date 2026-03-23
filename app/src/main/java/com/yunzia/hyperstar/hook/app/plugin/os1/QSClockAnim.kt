package com.yunzia.hyperstar.hook.app.plugin.os1

import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.replaceHookMethod
import com.yunzia.hyperstar.prefs.XSPUtils

object QSClockAnim : BasePluginHook() {

    val closeQsClockAnim = XSPUtils.getBoolean("close_qs_clock_anim",false)

    override fun init() {
        if (closeQsClockAnim) startMethodsHook()
    }

    private fun startMethodsHook() {

        findClass(
            "miui.systemui.controlcenter.panel.main.header.FakeStatusHeaderController",
            pluginClassLoader
        ).replaceHookMethod(
            "onCreate"
        ){
            return@replaceHookMethod null

        }


    }

}