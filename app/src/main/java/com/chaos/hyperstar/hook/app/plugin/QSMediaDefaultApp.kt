package com.chaos.hyperstar.hook.app.plugin

import com.chaos.hyperstar.hook.base.BaseHooker
import com.chaos.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook

class QSMediaDefaultApp :BaseHooker() {

    val apps = XSPUtils.getString("media_default_app_package","")

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        if (apps != ""){
            startMethodsHook(classLoader)

        }
    }

    private fun startMethodsHook(classLoader: ClassLoader?) {
//        val c = XposedHelpers.findClass("f.q.i.c", classLoader)
//        val LastPlaying = XposedHelpers.findClass(
//            "com.android.systemui.QSControlMiPlayDetailHeader\$Companion\$getLastPlayingAppPackageName\$2",
//            classLoader
//        )

        hookAllMethods(classLoader, "com.android.systemui.QSControlMiPlayDetailHeader\$Companion\$getLastPlayingAppPackageName\$2",
            "invokeSuspend",
            object : MethodHook {
                override fun before(param: XC_MethodHook.MethodHookParam?) {

                }

                override fun after(param: XC_MethodHook.MethodHookParam?) {

                    param?.result = apps

                }
            })
    }


}