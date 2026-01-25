package com.yunzia.hyperstar.hook.init

import android.graphics.Color
import android.util.Log
import android.view.View
//import com.yunzia.annotations.Init
import com.yunzia.hyperstar.hook.app.systemui.os2.AddCatPaw
import com.yunzia.hyperstar.hook.app.systemui.os2.LowDeviceBackgroundColor
import com.yunzia.hyperstar.hook.app.systemui.os2.SystemBarBackground
import com.yunzia.hyperstar.hook.app.systemui.os2.NotificationForLm
import com.yunzia.hyperstar.hook.app.systemui.os2.QSHeaderView
import com.yunzia.hyperstar.hook.app.systemui.os2.Test
import com.yunzia.hyperstar.hook.core.BaseHook
import com.yunzia.hyperstar.hook.core.BaseHooks
import com.yunzia.hyperstar.hook.core.annotation.Init
import java.lang.Boolean
import java.lang.Float
import kotlin.Exception
import kotlin.Int

@Init(packageName = "com.android.systemui", versions = [2])
object SystemUIHookForOS2 : BaseHooks() {


    override fun init() {
        initHooks(
            SystemBarBackground,
            LowDeviceBackgroundColor,
            NotificationForLm,
            QSHeaderView,
            PluginHooksForOS2
        )
    }

    fun applyViewShadowForMediaAlbum(f: Float, f2: Float, i: Int, view: View?) {
        try {
            val cls = Class.forName("android.view.View")
            val cls2: Class<*> = Float.TYPE
            cls.getMethod("setMiShadow", Integer.TYPE, cls2, cls2, cls2, cls2, Boolean.TYPE)
                .invoke(view, Color.argb(i, 0, 0, 0), 0.0f, f, f2, 1.0f, Boolean.FALSE)
        } catch (unused: Exception) {
            Log.d(
                "NotificationUtil",
                "applyViewShadowForMediaAlbum setMiShadow Method not found!"
            )
        }
    }

}