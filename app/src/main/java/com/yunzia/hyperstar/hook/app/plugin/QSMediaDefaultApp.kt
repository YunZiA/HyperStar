package com.yunzia.hyperstar.hook.app.plugin

import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.afterHookAllMethods
import com.yunzia.hyperstar.prefs.XSPUtils

object QSMediaDefaultApp : BasePluginHook() {

    val apps = XSPUtils.getString("media_default_app_package","")

    override fun init() {
        if (apps.isNullOrEmpty()) return
        findClass(
            "com.android.systemui.QSControlMiPlayDetailHeader\$Companion\$getLastPlayingAppPackageName\$2",
            pluginClassLoader
        ).afterHookAllMethods("invokeSuspend") { args, result ->
            result.replace(apps)
        }
    }
}