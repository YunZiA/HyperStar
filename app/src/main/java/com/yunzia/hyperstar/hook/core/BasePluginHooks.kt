package com.yunzia.hyperstar.hook.core

import android.content.ContextWrapper
import com.yunzia.hyperstar.hook.core.provider.PluginClassLoaderProvider
import io.github.kyuubiran.ezxhelper.xposed.EzXposed

abstract class BasePluginHooks : BaseHooks() {
    val plugin =  "miui.systemui.plugin"
    fun initPlugin(pluginContext: ContextWrapper){
        val pluginClassLoader = pluginContext.classLoader
        if (PluginClassLoaderProvider.init(pluginClassLoader)) {
            onPluginLoaded()
            EzXposed.addModuleAssetPath(pluginContext)
        }
    }
    abstract fun onPluginLoaded()
}