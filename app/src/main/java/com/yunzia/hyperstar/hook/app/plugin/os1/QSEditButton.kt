package com.yunzia.hyperstar.hook.app.plugin.os1

import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.replaceHookMethod
import com.yunzia.hyperstar.prefs.XSPUtils

object QSEditButton : BasePluginHook() {

    private val closeEditButtonShow = XSPUtils.getBoolean("close_edit_button_show",false)

    override fun init() {
        if (!closeEditButtonShow) return

        findClass(
            "miui.systemui.controlcenter.panel.main.qs.EditButtonController",
            pluginClassLoader
        ).apply {
            replaceHookMethod(
                "available",
                Boolean::class .java
            ) { args ->
                return@replaceHookMethod false
            }
            replaceHookMethod(
                "available",
                Boolean::class.java,
                "miui.systemui.controlcenter.panel.main.MainPanelModeController\$MainPanelMode"
            ) {
                return@replaceHookMethod false
            }
        }
    }
}