package com.yunzia.hyperstar.hook.app.plugin.os1

import com.yunzia.hyperstar.hook.core.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.afterHookAllMethods
import com.yunzia.hyperstar.hook.core.helper.callMethod
import com.yunzia.hyperstar.hook.core.helper.callMethodAs
import com.yunzia.hyperstar.hook.core.helper.callStaticMethodAs
import com.yunzia.hyperstar.prefs.XSPUtils

object QSMediaDeviceName : BasePluginHook() {

    override fun init() {
        if (XSPUtils.getInt("is_local_speaker",0) == 1){
            startMethodsHook()
        }
    }

    private fun startMethodsHook() {
        val MiPlayExtentionsKt  = findClass("com.android.systemui.MiPlayExtentionsKt",pluginClassLoader)
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