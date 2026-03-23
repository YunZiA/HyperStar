package com.yunzia.hyperstar.hook.app.plugin

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.hook.core.helper.getObjectField
import com.yunzia.hyperstar.hook.core.helper.getObjectFieldAs
import com.yunzia.hyperstar.prefs.XSPUtils

object QSCardAutoCollapse : BasePluginHook() {

    private val clickClose = XSPUtils.getBoolean("card_tile_click_close",false)

    override fun init() {
        startMethodsHook()
    }

    private fun collapseStatusBar(context: Context) {
        try {
            val systemService = context.getSystemService("statusbar")
            systemService.javaClass.getMethod("collapsePanels", *arrayOfNulls(0)).invoke(systemService, *arrayOfNulls(0))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startMethodsHook() {
        if (!clickClose) return
        findClass(
            "miui.systemui.controlcenter.qs.tileview.QSCardItemView\$onFinishInflate\$1",
            pluginClassLoader
        ).afterHookMethod("invoke",View::class.java) { args, result ->
            val qsCardItemView = thisObject.getObjectFieldAs<LinearLayout>("this$0")
            qsCardItemView.getObjectField("clickAction") ?: return@afterHookMethod
            collapseStatusBar(qsCardItemView.context)
        }
    }
}