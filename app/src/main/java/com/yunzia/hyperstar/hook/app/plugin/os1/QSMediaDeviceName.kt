package com.yunzia.hyperstar.hook.app.plugin.os1

import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class QSMediaDeviceName : Hooker() {

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        if (XSPUtils.getInt("is_local_speaker",0) == 1){
            startMethodsHook()
        }

    }

    private fun startMethodsHook() {

        val MiPlayExtentionsKt  = XposedHelpers.findClass("com.android.systemui.MiPlayExtentionsKt",classLoader)
        hookAllMethods(classLoader,"com.android.systemui.MiPlayExtentionsKt",
            "getFullName",
            object : MethodHook{
                override fun before(param: XC_MethodHook.MethodHookParam?) {


                }

                override fun after(param: XC_MethodHook.MethodHookParam?) {
                    val p0Vars = param?.args?.get(0)

                    val isLocalSpeaker : Boolean = XposedHelpers.callStaticMethod(MiPlayExtentionsKt,"isLocalSpeaker",p0Vars) as Boolean
                    if (isLocalSpeaker){
                        val dd = XposedHelpers.callMethod(p0Vars,"d")
                        val roomName = XposedHelpers.callMethod(dd,"getName")  as String
                        param?.result = roomName

                    }



                }
            })

    }
}