package com.yunzia.hyperstar.hook.app.plugin

import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class SuperBlurVolumeManager : BaseHooker() {

    val superBlurVolume = XSPUtils.getInt("is_super_blur_volume",0)

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        if (superBlurVolume != 0){
            startMethodsHook()

        }
    }

    private fun startMethodsHook() {
        val MiBlurCompat  = XposedHelpers.findClass("miui.systemui.util.MiBlurCompat",classLoader)
        XposedHelpers.findAndHookMethod(MiBlurCompat, "getBackgroundBlurOpened", Class.forName("android.content.Context") , object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {

            }

            override fun afterHookedMethod(param: MethodHookParam?) {

                if (superBlurVolume == 1){
                    param?.result = false

                }else if (superBlurVolume == 2){
                    param?.result = true

                }

            }

        })


    }
}