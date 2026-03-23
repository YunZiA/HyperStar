package com.yunzia.hyperstar.hook.core

import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper
import com.yunzia.hyperstar.hook.core.provider.ClassLoaderProvider
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModuleInterface.*

object XposedCore {

    internal lateinit var base: XposedInterface
        private set

    @JvmStatic
    lateinit var hookedPackageName: String
        private set

    @JvmStatic
    fun initXposedModule(base: XposedInterface, param: ModuleLoadedParam) {
        this.base = base
        ResourcesHelper.initModuleResources(base)
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

