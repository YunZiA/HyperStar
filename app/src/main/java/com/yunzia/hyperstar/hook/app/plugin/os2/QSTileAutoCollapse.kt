package com.yunzia.hyperstar.hook.app.plugin.os2

import android.content.Context
import android.os.SystemClock
import android.view.View
import android.widget.FrameLayout
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.utils.XSPUtils

class QSTileAutoCollapse : Hooker() {

    private val clickClose = XSPUtils.getBoolean("list_tile_click_close",false)

    fun collapseStatusBar(context: Context) {
        try {
            val systemService = context.getSystemService("statusbar")
            systemService.javaClass.getMethod("collapsePanels", *arrayOfNulls(0)).invoke(systemService, *arrayOfNulls(0))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        if (clickClose){
            val QSTileItemView = findClass("miui.systemui.controlcenter.qs.tileview.QSTileItemView", classLoader)

            val MainPanelModeController = findClass("miui.systemui.controlcenter.panel.main.MainPanelController\$Mode",classLoader)

            QSTileItemView.afterHookMethod(
                "onFinishInflate\$lambda-0",
                QSTileItemView,
                View::class.java
            ){
                val qSTileItemView = it.args[0] as FrameLayout
                val lastTriggeredTime = qSTileItemView.getLongField("lastTriggeredTime")
                val elapsedRealtime = SystemClock.elapsedRealtime()

                if (elapsedRealtime > lastTriggeredTime + 200) {
                    val clickAction = qSTileItemView.getObjectField("clickAction")
                    if (clickAction == null) {
                        starLog.logE("clickAction == null")
                        return@afterHookMethod
                    }

                    val enumConstants: Array<out Any>? = MainPanelModeController?.enumConstants
                    if (enumConstants == null) {
                        starLog.logE("enumConstants == null")
                        return@afterHookMethod
                    }

                    val mainPanelMode = qSTileItemView.getObjectField("mode")
                    if (mainPanelMode != enumConstants[2]) {
                        collapseStatusBar(qSTileItemView.context)
                    } else {
                        starLog.logE("mainPanelMode == edit")

                    }
                }
            }

        }
    }

}