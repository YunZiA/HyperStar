package com.yunzia.hyperstar.hook.init

import com.yunzia.annotations.Init
import com.yunzia.hyperstar.hook.app.systemui.os1.LowDeviceBackgroundColor
import com.yunzia.hyperstar.hook.app.systemui.os1.NavigationBarBackground
import com.yunzia.hyperstar.hook.base.InitHooker

@Init(packageName = "com.android.systemui")
class SystemUIHookForOS1 : InitHooker() {
    private val pluginHookForOS1 = PluginHookForOS1()

    override fun initResources() {
        initResource(pluginHookForOS1)
        if (resparam!!.packageName != mPackageName) return
    }

    override fun initHook() {
        pluginHookForOS1.initHooker()
        NavigationBarBackground().initHooker()
        LowDeviceBackgroundColor().initHooker()

        doTestHook()
    }


    private fun doTestHook() {
    }
}