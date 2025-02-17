package com.yunzia.hyperstar.hook.util.systemui

import com.yunzia.hyperstar.hook.tool.starLog
import de.robv.android.xposed.XposedHelpers

class Dependency(private val classloader: ClassLoader?) {

    private val dependency = XposedHelpers.findClassIfExists("com.android.systemui.Dependency",classloader)

    val sDependency by lazy {
        XposedHelpers.getStaticObjectField(dependency,"sDependency")
    }

    fun getDependencyInner(any: Any): Any? {

        if (sDependency == null){
            return null
        }
        return XposedHelpers.callMethod(sDependency,"getDependencyInner",any)
    }

    fun getDependencyInnerByName(name: String): Any? {
        val obj = XposedHelpers.findClass(name, classloader)
        if (obj == null){
            starLog.logE("$name is null")
            return null
        }
        return getDependencyInner(obj)
    }



}