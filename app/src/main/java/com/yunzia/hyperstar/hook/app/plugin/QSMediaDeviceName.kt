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
        val cc : Class<*>
        val p0Var  = XposedHelpers.findClassIfExists("v0.i",classLoader)
        val v = XposedHelpers.findClassIfExists("c.f.d.a.j.v",classLoader)
        if (p0Var != null) {
            cc = p0Var
        } else if (v != null) {
            cc = v
        }else{
            starLog.log("getFullName not getName!!")
            return
        }
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
                        cc
                    )
                    val isLocalSpeaker : Boolean = XposedHelpers.callStaticMethod(MiPlayExtentionsKt,"isLocalSpeaker",parameterTypes,p0Vars) as Boolean
                    if (isLocalSpeaker){
                        val k = cc.getDeclaredMethod("k")
                        val name = k.invoke(p0Vars)
                        val getName = DeviceInfo.getDeclaredMethod("getName")
                        val roomName : String = getName.invoke(name) as String
                        param?.result = roomName

                        //starLog.log("name||"+roomName)
                    }



                }
            })

    }
}