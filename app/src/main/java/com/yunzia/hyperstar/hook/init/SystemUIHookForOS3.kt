package com.yunzia.hyperstar.hook.init

import android.graphics.Color
import android.util.Log
import android.view.View
//import com.yunzia.annotations.Init
import com.yunzia.hyperstar.hook.app.systemui.os2.AddCatPaw
import com.yunzia.hyperstar.hook.app.systemui.os2.LowDeviceBackgroundColor
import com.yunzia.hyperstar.hook.app.systemui.os3.SystemBarBackground
import com.yunzia.hyperstar.hook.app.systemui.os2.NotificationForLm
import com.yunzia.hyperstar.hook.app.systemui.os2.QSHeaderView
import com.yunzia.hyperstar.hook.app.systemui.os2.Test
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.replaceHookMethod
import com.yunzia.hyperstar.hook.core.BaseHook
import com.yunzia.hyperstar.hook.core.BaseHooks
import com.yunzia.hyperstar.hook.core.annotation.Init
import java.lang.Boolean
import java.lang.Float
import kotlin.Exception
import kotlin.Int

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