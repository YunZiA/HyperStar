package com.yunzia.hyperstar.hook.init

//import com.yunzia.annotations.Init
import com.yunzia.hyperstar.hook.app.systemui.os1.LowDeviceBackgroundColor
import com.yunzia.hyperstar.hook.app.systemui.os1.SystemBarBackground
import com.yunzia.hyperstar.hook.core.base.BaseHooks
import com.yunzia.hyperstar.hook.core.annotation.Init

@Init(packageName = "com.android.systemui", versions = [1])
object SystemUIHookForOS1 : BaseHooks() {

    override fun init() {
        initHooks(
            PluginHookForOS1,
            SystemBarBackground,
            LowDeviceBackgroundColor
        )
    }
}