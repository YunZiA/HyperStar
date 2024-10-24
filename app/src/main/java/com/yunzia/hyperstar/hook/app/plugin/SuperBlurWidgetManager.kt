package com.yunzia.hyperstar.hook.app.plugin

import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class SuperBlurWidgetManager : BaseHooker() {

    val superBlurWidget = XSPUtils.getInt("is_super_blur_Widget",0)

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        if (superBlurWidget != 0){
            startMethodsHook()

        }
    }

    private fun startMethodsHook() {
        val ControlCenterUtils  = XposedHelpers.findClass("miui.systemui.controlcenter.utils.ControlCenterUtils",classLoader)
        XposedHelpers.findAndHookMethod(ControlCenterUtils, "getBackgroundBlurOpenedInDefaultTheme", Class.forName("android.content.Context") , object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {

            }

            override fun afterHookedMethod(param: MethodHookParam?) {

                if (superBlurWidget == 1){
                    param?.result = false

                }else if (superBlurWidget == 2){
                    param?.result = true

                }

            }

        })


    }
}