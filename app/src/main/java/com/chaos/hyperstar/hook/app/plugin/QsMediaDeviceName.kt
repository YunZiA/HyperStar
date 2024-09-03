package com.chaos.hyperstar.hook.app.plugin

import android.graphics.Bitmap
import com.chaos.hyperstar.hook.base.BaseHooker
import com.chaos.hyperstar.hook.tool.starLog
import com.chaos.hyperstar.utils.SPUtils
import com.chaos.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class QsMediaDeviceName :BaseHooker() {

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)

        if (XSPUtils.getInt("is_local_speaker",0) == 1){
            startMethodsHook(classLoader)
        }

    }

    private fun startMethodsHook(classLoader: ClassLoader?) {
        val p0Var  = XposedHelpers.findClass("c.f.d.a.j.p0",classLoader)
        val DeviceInfo  = XposedHelpers.findClass("com.miui.miplay.audio.data.DeviceInfo",classLoader)
        val MiPlayExtentionsKt  = XposedHelpers.findClass("com.android.systemui.MiPlayExtentionsKt",classLoader)
        hookAllMethods(classLoader,"com.android.systemui.MiPlayExtentionsKt",
            "getFullName",
            object : MethodHook{
                override fun before(param: XC_MethodHook.MethodHookParam?) {


                }

                override fun after(param: XC_MethodHook.MethodHookParam?) {

                    val p0Vars = param?.args?.get(0)
                    val parameterTypes = arrayOf<Class<*>>(
                        p0Var
                    )
                    val isLocalSpeaker : Boolean = XposedHelpers.callStaticMethod(MiPlayExtentionsKt,"isLocalSpeaker",parameterTypes,p0Vars) as Boolean
                    if (isLocalSpeaker){
                        val d = p0Var.getDeclaredMethod("d")
                        val name = d.invoke(p0Vars)
                        val getName = DeviceInfo.getDeclaredMethod("getName")
                        val roomName : String = getName.invoke(name) as String
                        param?.result = roomName

                        //starLog.log("name||"+roomName)
                    }



                }
            })

    }
}