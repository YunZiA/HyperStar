package com.yunzia.hyperstar.hook.init

//import com.yunzia.annotations.Init
import com.yunzia.hyperstar.hook.app.systemui.os2.LowDeviceBackgroundColor
import com.yunzia.hyperstar.hook.app.systemui.os3.SystemBarBackground
import com.yunzia.hyperstar.hook.app.systemui.os2.NotificationForLm
import com.yunzia.hyperstar.hook.app.systemui.os2.QSHeaderView
import com.yunzia.hyperstar.hook.core.base.BaseHooks
import com.yunzia.hyperstar.hook.core.annotation.Init

@Init(packageName = "com.android.systemui", versions = [3])
object SystemUIHookForOS3 : BaseHooks() {
    override fun init() {
        initHooks(
            SystemBarBackground,
            LowDeviceBackgroundColor,
            NotificationForLm,
            QSHeaderView,
            PluginHooksForOS3
        )
    }
}