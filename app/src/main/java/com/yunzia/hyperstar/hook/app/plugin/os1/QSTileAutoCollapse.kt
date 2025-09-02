package com.yunzia.hyperstar.hook.app.plugin.os1

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
            val MainPanelModeController = findClass("miui.systemui.controlcenter.panel.main.MainPanelModeController\$MainPanelMode",classLoader)

            QSTileItemView.beforeHookMethod(
                "onFinishInflate\$lambda-0",
                View::class.java
            ){
                val qSTileItemView = it.args[0] as FrameLayout
                val lastTriggeredTime = qSTileItemView.getLongField("lastTriggeredTime")
                val elapsedRealtime = SystemClock.elapsedRealtime()

                if (elapsedRealtime > lastTriggeredTime + 200) {
                    if (qSTileItemView.getObjectField("clickAction") == null){
                        starLog.logE("clickAction == null")
                        return@beforeHookMethod
                    }

                    val enumConstants: Array<out Any>? = MainPanelModeController?.getEnumConstants()
                    if (enumConstants == null){
                        starLog.logE("enumConstants == null")
                        return@beforeHookMethod
                    }
                    val mainPanelMode = qSTileItemView.getObjectField("mode")
                    if (mainPanelMode != enumConstants[2]) {
                        val mContext = qSTileItemView.context
                        collapseStatusBar(mContext)
                    }else{
                        starLog.logD("mainPanelMode == edit")

                    }
                }


            }

        }
    }

}