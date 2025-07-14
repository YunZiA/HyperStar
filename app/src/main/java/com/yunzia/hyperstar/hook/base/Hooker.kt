package com.yunzia.hyperstar.hook.base

import android.content.res.XModuleResources
import android.graphics.Color
import com.yunzia.hyperstar.hook.tool.starLog
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam

abstract class Hooker : HookerHelper() {
    var plugin: String = "miui.systemui.plugin"
    var systemUI: String = "com.android.systemui"

    var resparam: InitPackageResourcesParam? = null
    var modRes: XModuleResources? = null
    var classLoader: ClassLoader? = null
    open var mPackageName: String = ""
    val className = lazy { this.javaClass.simpleName }


    open fun initResources(resparam: InitPackageResourcesParam?, modRes: XModuleResources?) {
        this.resparam = resparam
        this.modRes = modRes
    }

    open fun initHook(classLoader: ClassLoader?) {
        this.classLoader = classLoader
        logD("$classLoader")
    }


    fun ReplaceColor(color: String?, colorValue: String?) {
        resparam!!.res.setReplacement(plugin, "color", color, Color.parseColor(colorValue))
    }

    fun ReplaceIntArray(array: String?, arrayChange: (array: IntArray)->Unit) {
        val arrayId = resparam!!.res.getIdentifier(array, "array", plugin)
        val ay = resparam!!.res.getIntArray(arrayId)
        arrayChange(ay)
        resparam!!.res.setReplacement(plugin, "array", array, ay)
    }

    fun logD(msg: String){
        starLog.logD(className.value, mPackageName, msg)

    }


}