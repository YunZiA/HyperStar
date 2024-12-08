package com.yunzia.hyperstar.hook.app.plugin

import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class QSMediaDeviceName :BaseHooker() {

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)

        if (XSPUtils.getInt("is_local_speaker",0) == 1){
            startMethodsHook()
        }

    }

    private fun startMethodsHook() {

        val MiPlayExtentionsKt  = findClass("com.android.systemui.MiPlayExtentionsKt",classLoader)
        hookAllMethods(MiPlayExtentionsKt,
            "getFullName",
            object : MethodHook{
                override fun before(param: XC_MethodHook.MethodHookParam?) {


                }

                override fun after(param: XC_MethodHook.MethodHookParam?) {
                    val p0Vars = param?.args?.get(0)

                    val isLocalSpeaker : Boolean = XposedHelpers.callStaticMethod(MiPlayExtentionsKt,"isLocalSpeaker",p0Vars) as Boolean
                    if (isLocalSpeaker){
                        val kk = XposedHelpers.callMethod(p0Vars,"k")
                        val roomName = XposedHelpers.callMethod(kk,"getName")  as String
                        param?.result = roomName

                    }



                }
            })

    }
}