package com.yunzia.hyperstar.hook.core.base

import android.app.Application
import android.content.Context
import android.util.Log
import com.yunzia.hyperstar.hook.core.StarLog
import com.yunzia.hyperstar.hook.core.StarLog.TAG
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.loadResAboveApi30
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.hook.core.XposedCore
import com.yunzia.hyperstar.prefs.loadPref
import com.yunzia.hyperstar.utils.getHookChannel
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.*

abstract class BaseXposedModule : XposedModule() {

    val module by lazy { this }
    val currentHookChannel by lazy { getHookChannel() }

    init {
    }
    override fun onModuleLoaded(param: ModuleLoadedParam) {
        XposedCore.initXposedModule(this, param)

//        log(Log.INFO, TAG, "onModuleLoaded: " + param.processName)
//        log(Log.INFO, TAG, "framework: $frameworkName($frameworkVersionCode) API $apiVersion")
//
//        val hasProp: (Long) -> Boolean = { prop -> frameworkProperties.and(prop) != 0L }
//        log(Log.INFO, TAG, "system supported: " + hasProp(PROP_CAP_SYSTEM))
//        log(Log.INFO, TAG, "remote supported: " + hasProp(PROP_CAP_REMOTE))
//        log(Log.INFO, TAG, "api protection: " + hasProp(PROP_RT_API_PROTECTION))
    }

    override fun onPackageLoaded(param: PackageLoadedParam) {
        loadPref()
        XposedCore.initOnPackageLoaded(param)
        log(Log.INFO, TAG, "onPackageLoaded: " + param.packageName)
        log(Log.INFO, TAG, "default classloader is " + param.defaultClassLoader)
        StarLog.log("ModuleMain at " + param.applicationInfo.packageName)
        Application::class.java.apply {
            afterHookMethod("attach", Context::class.java) {args, result->
                try {
                    loadResAboveApi30((args[0] as Context))
                    StarLog.logD("Application attach")
                }catch (e: Exception){
                    StarLog.logE("attach $e")
                }
            }
        }
    }

    override fun onPackageReady(param: PackageReadyParam) {
        XposedCore.initOnPackageReady(param)
    }

    override fun onSystemServerStarting(param: SystemServerStartingParam) {
        XposedCore.onSystemServerStarting(param)
        log(Log.INFO, TAG, "onSystemServerStarting, system classloader: " + param.classLoader)
    }

    fun initHooks(vararg hooks: BaseHook?) {
        for (h in hooks) {
            h ?: continue
            if ((h.mPackageName.isEmpty() || XposedCore.hookedPackageName == h.mPackageName) && currentHookChannel in h.minVersion..h.maxVersion && !h.isInit){
                try {
                    h.init()
                    h.isInit = true
                    StarLog.log("initialize hook in currentHookChannel: ${h.className} ")
                } catch (e: Exception) {
                    StarLog.log("Failed to initialize hook: ${h.className}\n $e")
                }
            }
        }
    }
}