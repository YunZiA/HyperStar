package com.yunzia.hyperstar.hook.app.plugin

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.utils.XSPUtils

class QSCardAutoCollapse : Hooker() {

    private val clickClose = XSPUtils.getBoolean("card_tile_click_close",false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

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
            classLoader
        ).afterHookMethod("invoke",View::class.java){

            val qsCardItemView = this.getObjectFieldAs<LinearLayout>("this$0")

            qsCardItemView.getObjectField("clickAction") ?: return@afterHookMethod

            collapseStatusBar(qsCardItemView.context)

        }

    }

}