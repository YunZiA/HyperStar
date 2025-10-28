package com.yunzia.hyperstar.hook.app.plugin.os1

import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.findClass
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

            if (MiPlayExtentionsKt.callStaticMethodAs("isLocalSpeaker",p0Vars)){
                val dd = p0Vars.callMethod("d")
                val roomName = dd.callMethodAs<String>("getName")
                it.result = roomName

            }

        }

    }
}