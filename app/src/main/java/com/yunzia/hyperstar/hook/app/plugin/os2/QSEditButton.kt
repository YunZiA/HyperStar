package com.yunzia.hyperstar.hook.app.plugin.os2

import com.yunzia.hyperstar.hook.core.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.replaceHookMethod
import com.yunzia.hyperstar.prefs.XSPUtils

object QSEditButton : BasePluginHook() {

    private val closeEditButtonShow = XSPUtils.getBoolean("close_edit_button_show",false)

    override fun init() {
        
        if (!closeEditButtonShow){
            return
        }
        startMethodsHook()
    }

    private fun startMethodsHook() {

        findClass(
            "miui.systemui.controlcenter.panel.main.qs.EditButtonController",
            pluginClassLoader
        ).replaceHookMethod(
            "available",
            Boolean::class.java
        ) {
            false
        }


    }
}