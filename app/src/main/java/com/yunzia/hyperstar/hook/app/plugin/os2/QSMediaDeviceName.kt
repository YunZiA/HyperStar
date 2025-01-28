package com.yunzia.hyperstar.hook.app.plugin.os2

import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.utils.XSPUtils

class QSMediaDeviceName : Hooker() {

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        if (XSPUtils.getInt("is_local_speaker",0) == 1){
            startMethodsHook()
        }

    }

    private fun startMethodsHook() {

        val MiPlayExtentionsKt  = findClass("com.android.systemui.MiPlayExtentionsKt",classLoader)
        MiPlayExtentionsKt.afterHookAllMethods("getFullName"){
            val p0Vars = it.args[0]

            val isLocalSpeaker = MiPlayExtentionsKt.callStaticMethodAs<Boolean>("isLocalSpeaker",p0Vars)
            if (isLocalSpeaker){
                val kk = p0Vars.callMethod("k")
                it.result = kk.callMethodAs<String>("getName")

            }

        }

    }
}