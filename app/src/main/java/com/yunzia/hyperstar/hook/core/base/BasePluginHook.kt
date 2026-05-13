package com.yunzia.hyperstar.hook.core.base

import com.yunzia.hyperstar.hook.core.provider.PluginClassLoaderProvider

abstract class BasePluginHook : BaseHook() {
    val plugin = PLUGIN_PACKAGE
    val pluginClassLoader by lazy { PluginClassLoaderProvider.classLoader }
}
