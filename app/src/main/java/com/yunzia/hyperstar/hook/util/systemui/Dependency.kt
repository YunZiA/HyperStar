package com.yunzia.hyperstar.hook.util.systemui

import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.StarLog.logE
import com.yunzia.hyperstar.hook.core.helper.callMethod
import com.yunzia.hyperstar.hook.core.helper.getStaticObjectField

class Dependency(private val classloader: ClassLoader?) {

    private val dependency = findClass("com.android.systemui.Dependency",classloader)

    val sDependency by lazy {
        dependency.getStaticObjectField("sDependency")
    }

    fun getDependencyInner(any: Any): Any? {

        if (sDependency == null){
            return null
        }
        return sDependency.callMethod("getDependencyInner",any)
    }

    fun getDependencyInnerByName(name: String): Any? {
        val obj = findClass(name, classloader)
        if (obj == null){
            logE("$name is null")
            return null
        }
        return getDependencyInner(obj)
    }



}