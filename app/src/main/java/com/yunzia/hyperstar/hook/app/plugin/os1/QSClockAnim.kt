package com.yunzia.hyperstar.hook.app.plugin.os1

import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

class QSClockAnim : Hooker() {

    val closeQsClockAnim = XSPUtils.getBoolean("close_qs_clock_anim",false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        if (!closeQsClockAnim) return

        startMethodsHook()
    }

    private fun startMethodsHook() {

        val FakeStatusHeaderController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.header.FakeStatusHeaderController",classLoader)

        XposedHelpers.findAndHookMethod(FakeStatusHeaderController,"onCreate",object : XC_MethodReplacement(){
            override fun replaceHookedMethod(param: MethodHookParam?): Any? {
                return null
            }
        })


    }

}