package com.yunzia.hyperstar.hook.core.provider

import com.yunzia.hyperstar.hook.core.Log

object PluginClassLoaderProvider {

    var classLoader: ClassLoader? = null
        private set

    fun init(pluginClassLoader: ClassLoader?): Boolean {
        pluginClassLoader?.let {
            if ( classLoader == null || classLoader != it){
                classLoader = it
                Log.log("Loaded pluginClassLoader: $it")
                return true
            }
        } ?: run {
            Log.logE("Failed to load pluginClassLoader: null returned")
            classLoader = null
        }
        return false
    }
}