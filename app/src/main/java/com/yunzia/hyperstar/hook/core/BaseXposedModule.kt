package com.yunzia.hyperstar.hook.core

import android.app.Application
import com.yunzia.hyperstar.hook.core.Log.logE
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.prefs.loadPref
import com.yunzia.hyperstar.utils.getHookChannel
import io.github.kyuubiran.ezxhelper.xposed.EzXposed
import io.github.kyuubiran.ezxhelper.xposed.EzXposed.addModuleAssetPath
import io.github.kyuubiran.ezxhelper.xposed.EzXposed.initAppContext
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

abstract class BaseXposedModule(base: XposedInterface, param: XposedModuleInterface.ModuleLoadedParam) : XposedModule(base, param) {

    val module by lazy { this }
    val currentHookChannel by lazy { getHookChannel() }

    init {
        EzXposed.initXposedModule(base)
        EzXposed.initModuleResources()
    }
    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        super.onPackageLoaded(param)
        log("ModuleMain at " + param.applicationInfo.packageName)
        EzXposed.initOnPackageLoaded(param)
        Application::class.java.apply {
            afterHookMethod("onCreate"){
                this as Application
                try {
                    addModuleAssetPath(this.applicationContext)
                }catch (e: Exception){
                    logE("attach $e")
                }
            }
        }
        loadPref()
    }

    override fun onSystemServerLoaded(param: XposedModuleInterface.SystemServerLoadedParam) {
        super.onSystemServerLoaded(param)

    }

    fun initHooks(vararg hooks: BaseHook?) {
        for (h in hooks) {
            h ?: continue
            log("${h.className} minVersion ${h.minVersion}")
            if ((h.mPackageName.isEmpty() || EzXposed.hookedPackageName == h.mPackageName) && currentHookChannel in h.minVersion..h.maxVersion && !h.isInit){
                try {
                    h.init()
                    h.isInit = true
                } catch (e: Exception) {
                    log("Failed to initialize hook: ${h.className}", e)
                }
            }
        }
    }
}