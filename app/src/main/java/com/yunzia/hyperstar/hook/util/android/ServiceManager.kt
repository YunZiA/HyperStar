package com.yunzia.hyperstar.hook.util.android

import android.content.Context
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.provider.PluginClassLoaderProvider

class ServiceManager {
}

//fun reboot(context: Context,reason: String? = null) {
//
//    findClass("com.android.internal.app.ShutdownThread", PluginClassLoaderProvider.classLoader)?.getMethod(
//        "reboot",
//        Context::class.java,
//        String::class.java,
//        Boolean::class.javaPrimitiveType
//    )?.invoke(
//        null,
//        context,
//        reason,
//        false
//    )
//}