package com.yunzia.hyperstar.hook.app.plugin.os2

import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.base.replaceHookMethod
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
        ).replaceHookMethod(
            "available",
            Boolean::class.java
        ) {
            false
        }


    }
}