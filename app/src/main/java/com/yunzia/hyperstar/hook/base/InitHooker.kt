package com.yunzia.hyperstar.hook.base

import android.content.res.XModuleResources
import com.yunzia.annotations.Init
import com.yunzia.hyperstar.hook.tool.starLog
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

abstract class InitHooker: HookerHelper() {

    var plugin: String = "miui.systemui.plugin"
    var resparam: InitPackageResourcesParam? = null
    var lpparam: LoadPackageParam? = null
    var modRes: XModuleResources? = null
    var classLoader: ClassLoader? = null
    var secClassLoader: ClassLoader? = null
    val init: Init? = this::class.java.getAnnotation(Init::class.java)

    val mPackageName by lazy { init!!.packageName }



    open fun initResources() {}

    open fun initHook() {}

    open fun initHook(lpparam: LoadPackageParam) {

        this.lpparam = lpparam

        if (lpparam.packageName == init!!.packageName) {
            starLog.log("Loaded app: " + lpparam.packageName)
            this.classLoader = lpparam.classLoader
            initHook()
        }
    }

    fun initHook(classLoader: ClassLoader?) {
        this.classLoader = classLoader
        initHook()
    }



    fun initResources(resparam: InitPackageResourcesParam?, modRes: XModuleResources?) {
        this.resparam = resparam
        this.modRes = modRes
        initResources()
    }


    fun initResource(hooker: Hooker) {
        hooker.initResources(resparam, modRes)
    }

    fun initResource(hooker: InitHooker) {
        hooker.initResources(resparam, modRes)
    }

    fun initHooker(hooker: Hooker) {
        try {
            hooker.initHook(classLoader)
        } catch (e: Exception) {
            starLog.log(e.message)
        }
    }

    fun initHooker(initHooker: InitHooker) {
        try {
            initHooker.initHook(classLoader)
        } catch (e: Exception) {
            starLog.log(e.message)
        }
    }

    open fun initSecHook(secClassLoader: ClassLoader?) {
        this.secClassLoader = secClassLoader
    }

    fun initSecHooker(hooker: Hooker) {
        try {
            hooker.initHook(secClassLoader)
        } catch (e: Exception) {
            starLog.log(e.message)
        }
    }
}