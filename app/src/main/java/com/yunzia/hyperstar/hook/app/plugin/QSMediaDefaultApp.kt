package com.yunzia.hyperstar.hook.app.plugin

import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook

class QSMediaDefaultApp : Hooker() {

    val apps = XSPUtils.getString("media_default_app_package","")

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        if (apps != ""){
            startMethodsHook()

        }
    }

    private fun startMethodsHook() {

        hookAllMethods(classLoader, "com.android.systemui.QSControlMiPlayDetailHeader\$Companion\$getLastPlayingAppPackageName\$2",
            "invokeSuspend",
            object : MethodHook {
                override fun before(param: XC_MethodHook.MethodHookParam) {

                }

                override fun after(param: XC_MethodHook.MethodHookParam) {

                    param.result = apps

                }
            })
    }


}