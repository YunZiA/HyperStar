package com.yunzia.hyperstar.hook.app.plugin.os2

import android.content.Context
import android.provider.Settings
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.afterHookAllConstructors
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.utils.XSPUtils


class QSHeaderViewListener : Hooker() {

    private val is_use_chaos_header = XSPUtils.getBoolean("is_use_chaos_header",false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        if (!is_use_chaos_header) return
        startMethodsHook()
    }


    private fun startMethodsHook() {
        var qsListController: Any? = null
        val MainPanelModeController = findClass("miui.systemui.controlcenter.panel.main.MainPanelController\$Mode",classLoader)

        val MainPanelHeaderController  = findClass("miui.systemui.controlcenter.panel.main.header.MainPanelHeaderController",classLoader)

        MainPanelHeaderController.afterHookAllConstructors {
            this
            val context = this.callMethodAs<Context>("getContext")!!
            val controlCenterHeader = this.getObjectField("controlCenterHeader")
            val controlCenterHeaderView = controlCenterHeader.getObjectFieldAs<ViewGroup>("controlCenterHeaderView")

            val editId = Settings.System.getInt(context.contentResolver,"cc_edit_Id",0)
            if (editId == 0){
                starLog.logE("ControlCenterHeaderController editId == null")
                return@afterHookAllConstructors
            }
            val editButton = controlCenterHeaderView.findViewById<View>(editId)
            editButton.setOnClickListener {
                if(controlCenterHeaderView.alpha == 0f) return@setOnClickListener
                it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                if (qsListController != null){

                    val mainPanelMode: Array<out Any>? = MainPanelModeController?.enumConstants
                    if (mainPanelMode == null){
                        starLog.logE("enumConstants == null")
                        return@setOnClickListener
                    }

                    starLog.logD(""+mainPanelMode[0])
                    qsListController.callMethod("startQuery",mainPanelMode[2])
                }
            }
        }

        findClass(
            "miui.systemui.controlcenter.panel.main.qs.EditButtonController_Factory",
            classLoader
        ).afterHookAllConstructors {
            val qsListControllerProvider = this.getObjectField("qsListControllerProvider")

            if (qsListControllerProvider == null){
                starLog.logE("qsListControllerProviders == null")
                return@afterHookAllConstructors
            }
            qsListController = qsListControllerProvider.callMethod("get")

        }

    }


}


