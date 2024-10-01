package com.chaos.hyperstar.hook.app.plugin

import com.chaos.hyperstar.hook.base.BaseHooker
import com.chaos.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

class QSClockAnim : BaseHooker() {

    val closeQsClockAnim = XSPUtils.getBoolean("close_qs_clock_anim",false)

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)

        if (!closeQsClockAnim) return

        startMethodsHook(classLoader)
    }

    private fun startMethodsHook(classLoader: ClassLoader?) {

        val FakeStatusHeaderController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.header.FakeStatusHeaderController",classLoader)

        XposedHelpers.findAndHookMethod(FakeStatusHeaderController,"onCreate",object : XC_MethodReplacement(){
            override fun replaceHookedMethod(param: MethodHookParam?): Any? {
                return null
            }
        })


    }

}