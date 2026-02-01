package com.yunzia.hyperstar.hook.app.plugin.os1

import android.content.Context
import android.os.SystemClock
import android.view.View
import android.widget.FrameLayout
import com.yunzia.hyperstar.hook.core.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.Log.logD
import com.yunzia.hyperstar.hook.core.Log.logE
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
        if (clickClose){
            val QSTileItemView = findClass("miui.systemui.controlcenter.qs.tileview.QSTileItemView", pluginClassLoader)
            val MainPanelModeController = findClass("miui.systemui.controlcenter.panel.main.MainPanelModeController\$MainPanelMode",pluginClassLoader)

            QSTileItemView.beforeHookMethod(
                "onFinishInflate\$lambda-0",
                View::class.java
            ){
                val qSTileItemView = it.args[0] as FrameLayout
                val lastTriggeredTime = qSTileItemView.getLongField("lastTriggeredTime") ?: return@beforeHookMethod
                val elapsedRealtime = SystemClock.elapsedRealtime()

                if (elapsedRealtime > lastTriggeredTime + 200) {
                    if (qSTileItemView.getObjectField("clickAction") == null){
                        logE("clickAction == null")
                        return@beforeHookMethod
                    }
                    val enumConstants: Array<out Any>? = MainPanelModeController?.getEnumConstants()
                    if (enumConstants == null){
                        logE("enumConstants == null")
                        return@beforeHookMethod
                    }
                    val mainPanelMode = qSTileItemView.getObjectField("mode")
                    if (mainPanelMode != enumConstants[2]) {
                        val mContext = qSTileItemView.context
                        collapseStatusBar(mContext)
                    }else{
                        logD("mainPanelMode == edit")
                    }
                }
            }
        }
    }
}