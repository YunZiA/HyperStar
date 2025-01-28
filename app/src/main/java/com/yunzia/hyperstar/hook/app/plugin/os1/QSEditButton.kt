package com.yunzia.hyperstar.hook.app.plugin.os1

import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.utils.XSPUtils

class QSEditButton : Hooker() {

    private val closeEditButtonShow = XSPUtils.getBoolean("close_edit_button_show",false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        if (!closeEditButtonShow){
            return
        }
        startMethodsHook()
    }

    private fun startMethodsHook() {

        findClass(
            "miui.systemui.controlcenter.panel.main.qs.EditButtonController",
            classLoader
        ).apply {
            replaceHookMethod(
                "available",
                Boolean::class.java
            ){
                return@replaceHookMethod false
            }
            replaceHookMethod(
                "available",
                Boolean::class.java,
                "miui.systemui.controlcenter.panel.main.MainPanelModeController\$MainPanelMode"
            ){
                return@replaceHookMethod false
            }

        }

    }
}