package com.yunzia.hyperstar.hook.app.plugin.os3

import android.content.Context
import android.os.SystemClock
import android.view.View
import android.widget.FrameLayout
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.beforeHookMethod
import com.yunzia.hyperstar.hook.core.helper.getLongField
import com.yunzia.hyperstar.hook.core.helper.getObjectField
import com.yunzia.hyperstar.prefs.XSPUtils

object QSTileAutoCollapse : BasePluginHook() {

    private val clickClose = XSPUtils.getBoolean("list_tile_click_close",false)

    fun collapseStatusBar(context: Context) {
        try {
            val systemService = context.getSystemService("statusbar")
            systemService.javaClass.getMethod("collapsePanels", *arrayOfNulls(0)).invoke(systemService, *arrayOfNulls(0))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun init() {
        if (!clickClose) return

        val QSTileItemView = findClass("miui.systemui.controlcenter.qs.tileview.QSTileItemView", pluginClassLoader)
        val MainPanelModeController = findClass("miui.systemui.controlcenter.panel.main.MainPanelController\$Mode",pluginClassLoader)

        QSTileItemView.beforeHookMethod(
            "onFinishInflate\$lambda$0",
            QSTileItemView,View::class .java
        ) { args, result ->
            val qSTileItemView = args[0] as FrameLayout
            val lastTriggeredTime = qSTileItemView.getLongField("lastTriggeredTime")!!
            val elapsedRealtime = SystemClock.elapsedRealtime()

            if (elapsedRealtime > lastTriggeredTime + 200) {
                val clickAction = qSTileItemView.getObjectField("clickAction")
                    ?: return@beforeHookMethod

                val enumConstants: Array<out Any> = MainPanelModeController?.enumConstants
                    ?: return@beforeHookMethod

                val mainPanelMode = qSTileItemView.getObjectField("mode")
                if (mainPanelMode == enumConstants[0]) {
                    collapseStatusBar(qSTileItemView.context)
                }
            }
        }
    }

}