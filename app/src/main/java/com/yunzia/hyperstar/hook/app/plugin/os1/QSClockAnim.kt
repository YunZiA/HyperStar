package com.yunzia.hyperstar.hook.app.plugin.os1

import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.utils.XSPUtils

class QSClockAnim : Hooker() {

    val closeQsClockAnim = XSPUtils.getBoolean("close_qs_clock_anim",false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        if (!closeQsClockAnim) return

        startMethodsHook()
    }

    private fun startMethodsHook() {

        findClass(
            "miui.systemui.controlcenter.panel.main.header.FakeStatusHeaderController",
            classLoader
        ).replaceHookMethod(
            "onCreate"
        ){
            return@replaceHookMethod null

        }


    }

}