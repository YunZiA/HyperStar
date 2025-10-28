package com.yunzia.hyperstar.hook.app.plugin

import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.utils.XSPUtils

class QSMediaDefaultApp : Hooker() {

    val apps = XSPUtils.getString("media_default_app_package","")

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        if (apps != ""){
            startMethodsHook()
        }
    }

    private fun startMethodsHook() {

        findClass(
            "com.android.systemui.QSControlMiPlayDetailHeader\$Companion\$getLastPlayingAppPackageName\$2",
            classLoader
        ).afterHookAllMethods("invokeSuspend"){
            it.result = apps
        }

    }


}