package com.yunzia.hyperstar.hook.core.base

import com.yunzia.hyperstar.hook.core.provider.PluginClassLoaderProvider

abstract class BasePluginHook : BaseHook() {
    val plugin =  "miui.systemui.plugin"
    val pluginClassLoader by lazy{ PluginClassLoaderProvider.classLoader }
}