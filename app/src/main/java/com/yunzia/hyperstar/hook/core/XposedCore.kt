package com.yunzia.hyperstar.hook.core

import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper
import com.yunzia.hyperstar.hook.core.provider.ClassLoaderProvider
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModuleInterface.*

object XposedCore {

    private lateinit var _base: XposedInterface

    val base: XposedInterface
        get() = _base

    @JvmStatic
    lateinit var hookedPackageName: String
        private set

    @JvmStatic
    lateinit var modulePath: String
        private set

    @JvmStatic
    fun initXposedModule(base: XposedInterface, param: ModuleLoadedParam) {
        this._base = base
        this.modulePath = base.moduleApplicationInfo.sourceDir
    }

    fun initOnPackageLoaded(param: PackageLoadedParam) {
        ClassLoaderProvider.classLoader = param.defaultClassLoader
        hookedPackageName = param.packageName
    }

    @JvmStatic
    fun initOnPackageReady(param: PackageReadyParam) {
        ClassLoaderProvider.classLoader = param.classLoader
        hookedPackageName = param.packageName
    }

    @JvmStatic
    fun onSystemServerStarting(param: SystemServerStartingParam) {
        ClassLoaderProvider.classLoader = param.classLoader
    }

}

