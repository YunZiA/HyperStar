package com.yunzia.hyperstar.hook.core.provider

import com.yunzia.hyperstar.hook.core.StarLog

object PluginClassLoaderProvider {

    @JvmStatic
    var classLoader: ClassLoader? = null
        private set

    @JvmStatic
    fun init(pluginClassLoader: ClassLoader?): Boolean {
        pluginClassLoader?.let {
            if (classLoader == null || classLoader != it){
                classLoader = it
                StarLog.log("Loaded pluginClassLoader: $it")
                return true
            }
        } ?: run {
            StarLog.logE("Failed to load pluginClassLoader: null returned")
            classLoader = null
        }
        return false
    }
}