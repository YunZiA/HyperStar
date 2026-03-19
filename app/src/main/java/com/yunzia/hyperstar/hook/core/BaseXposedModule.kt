package com.yunzia.hyperstar.hook.core

import android.app.Application
import android.content.Context
import com.yunzia.hyperstar.hook.core.Log.logE
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.addModuleAssetPath
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.loadResAboveApi30
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.prefs.loadPref
import com.yunzia.hyperstar.utils.getHookChannel
import io.github.kyuubiran.ezxhelper.xposed.EzXposed
import io.github.kyuubiran.ezxhelper.xposed.EzXposed.initAppContext
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

abstract class BaseXposedModule(base: XposedInterface, param: XposedModuleInterface.ModuleLoadedParam) : XposedModule(base, param) {

    val module by lazy { this }
    val currentHookChannel by lazy { getHookChannel() }

    init {
        EzXposed.initXposedModule(base)
        ResourcesHelper.initModuleResources(base)
    }
    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        super.onPackageLoaded(param)
        log("ModuleMain at " + param.applicationInfo.packageName)
        EzXposed.initOnPackageLoaded(param)

        Application::class.java.apply {
            afterHookMethod("attach", Context::class.java){
                try {
                    loadResAboveApi30((it.args[0] as Context))
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
            if ((h.mPackageName.isEmpty() || EzXposed.hookedPackageName == h.mPackageName) && currentHookChannel in h.minVersion..h.maxVersion && !h.isInit){
                try {
                    h.init()
                    h.isInit = true
                    log("initialize hook in currentHookChannel: ${h.className} ")
                } catch (e: Exception) {
                    log("Failed to initialize hook: ${h.className}", e)
                }
            }
        }
    }
}