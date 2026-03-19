package com.yunzia.hyperstar.hook.core

import android.content.ContextWrapper
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.addModuleAssetPath
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.loadResAboveApi30
import com.yunzia.hyperstar.hook.core.provider.PluginClassLoaderProvider
import io.github.kyuubiran.ezxhelper.xposed.EzXposed

abstract class BasePluginHooks : BaseHooks() {
    val plugin =  "miui.systemui.plugin"
    fun initPlugin(pluginContext: ContextWrapper){
        val pluginClassLoader = pluginContext.classLoader
        if (PluginClassLoaderProvider.init(pluginClassLoader)) {
            loadResAboveApi30(pluginContext)
            onPluginLoaded()
        }
    }
    abstract fun onPluginLoaded()
}