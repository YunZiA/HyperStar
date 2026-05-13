package com.yunzia.hyperstar.hook.core.base

import android.content.Context
import android.content.ContextWrapper
import com.yunzia.hyperstar.hook.core.StarLog.logD
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.finder.loadClass
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.loadResAboveApi30
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.hook.core.helper.replaceHookMethod
import com.yunzia.hyperstar.hook.core.provider.PluginClassLoaderProvider

abstract class BasePluginHooks : BaseHooks() {
    val plugin = PLUGIN_PACKAGE

    protected fun hookPluginContext() {
        "com.android.systemui.shared.plugins.PluginInstance\$PluginFactory"
            .loadClass()
            .afterHookMethod("createPluginContext") { _, result ->
                val pluginContext = result.value as? ContextWrapper ?: return@afterHookMethod
                if (pluginContext.packageName != plugin) {
                    logD(
                        "Detected non-target plugin package, current=${pluginContext.packageName}, target=$plugin"
                    )
                    return@afterHookMethod
                }
                initPlugin(pluginContext)
            }
    }

    fun flipCard() {
        findClass(
            "miui.systemui.util.CommonUtils",
            PluginClassLoaderProvider.classLoader
        )?.apply {
            replaceHookMethod("isFlipDevice") { true }
            replaceHookMethod("isTinyScreen", Context::class.java) { true }
        }

        findClass(
            "miui.systemui.controlcenter.panel.main.qs.CompactQSCardController",
            PluginClassLoaderProvider.classLoader
        )?.apply {
            replaceHookMethod("onCreate") { null }
        }
    }

    fun initPlugin(pluginContext: ContextWrapper) {
        val pluginClassLoader = pluginContext.classLoader
        if (PluginClassLoaderProvider.init(pluginClassLoader)) {
            loadResAboveApi30(pluginContext)
            onPluginLoaded()
        }
    }

    abstract fun onPluginLoaded()
}
