package com.yunzia.hyperstar.hook.init

import android.graphics.Color
import android.util.Log
import android.view.View
import com.yunzia.annotations.Init
import com.yunzia.hyperstar.hook.app.systemui.os2.AddCatPaw
import com.yunzia.hyperstar.hook.app.systemui.os2.LowDeviceBackgroundColor
import com.yunzia.hyperstar.hook.app.systemui.os3.SystemBarBackground
import com.yunzia.hyperstar.hook.app.systemui.os2.NotificationForLm
import com.yunzia.hyperstar.hook.app.systemui.os2.QSHeaderView
import com.yunzia.hyperstar.hook.app.systemui.os2.Test
import com.yunzia.hyperstar.hook.base.InitHooker
import com.yunzia.hyperstar.hook.base.afterHookAllConstructors
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.base.replaceHookMethod
import java.lang.Boolean
import java.lang.Float
import kotlin.Exception
import kotlin.Int

@Init(packageName = "com.android.systemui")
class SystemUIHookForOS3 : InitHooker() {
    private val pluginHookForOS3 = PluginHookForOS3()
    private val qsHeaderView = QSHeaderView()
    private val addCatPaw = AddCatPaw()
    private val test = Test()

    override fun initResources() {
        if (resparam!!.packageName != mPackageName) {
            initResource(pluginHookForOS3)
            return
        }
        initResource(qsHeaderView)

        // modRes.fwd(R.color.black)

        //initResource(test);
        //initResource(addCatPaw);
    }

    override fun initHook() {
        SystemBarBackground().initHooker()
        LowDeviceBackgroundColor().initHooker()
        NotificationForLm().initHooker()

        qsHeaderView.initHooker()
        pluginHookForOS3.initHooker()

        //initHooker(test);

        //initHooker(addCatPaw);

    }


    private fun doTestHook() {
        //    "isFlipDevice"
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