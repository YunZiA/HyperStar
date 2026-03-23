package com.yunzia.hyperstar.hook.app.plugin.os2

import android.content.Context
import android.provider.Settings
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.StarLog.logD
import com.yunzia.hyperstar.hook.core.StarLog.logE
import com.yunzia.hyperstar.hook.core.helper.afterHookAllConstructors
import com.yunzia.hyperstar.hook.core.helper.callMethod
import com.yunzia.hyperstar.hook.core.helper.callMethodAs
import com.yunzia.hyperstar.hook.core.helper.getObjectField
import com.yunzia.hyperstar.hook.core.helper.getObjectFieldAs
import com.yunzia.hyperstar.prefs.XSPUtils


object QSHeaderViewListener : BasePluginHook() {

    private val is_use_chaos_header = XSPUtils.getBoolean("is_use_chaos_header",false)

    override fun init() {
        if (!is_use_chaos_header) return
        startMethodsHook()
    }


    private fun startMethodsHook() {
        var qsListController: Any? = null
        val MainPanelModeController = findClass("miui.systemui.controlcenter.panel.main.MainPanelController\$Mode",pluginClassLoader)
        val MainPanelHeaderController  = findClass("miui.systemui.controlcenter.panel.main.header.MainPanelHeaderController",pluginClassLoader)

        MainPanelHeaderController.afterHookAllConstructors { args, result ->
            val context = thisObject.callMethodAs<Context>("getContext")!!
            val controlCenterHeader = thisObject.getObjectField("controlCenterHeader")
            val controlCenterHeaderView = controlCenterHeader.getObjectFieldAs<ViewGroup>("controlCenterHeaderView")

            val editId = Settings.System.getInt(context.contentResolver,"cc_edit_Id",0)
            if (editId == 0){
                logE("ControlCenterHeaderController editId == null")
                return@afterHookAllConstructors
            }
            val editButton = controlCenterHeaderView.findViewById<View>(editId)
            editButton.setOnClickListener {
                if(controlCenterHeaderView.alpha == 0f) return@setOnClickListener
                it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                if (qsListController != null){

                    val mainPanelMode: Array<out Any>? = MainPanelModeController?.enumConstants
                    if (mainPanelMode == null){
                        logE("enumConstants == null")
                        return@setOnClickListener
                    }

                    logD(""+mainPanelMode[0])
                    qsListController.callMethod("startQuery",mainPanelMode[2])
                }
            }
        }

        findClass(
            "miui.systemui.controlcenter.panel.main.qs.EditButtonController_Factory",
            pluginClassLoader
        ).afterHookAllConstructors { args, result ->
            val qsListControllerProvider = thisObject.getObjectField("qsListControllerProvider")
            if (qsListControllerProvider == null){
                logE("qsListControllerProviders == null")
                return@afterHookAllConstructors
            }
            qsListController = qsListControllerProvider.callMethod("get")

        }

    }


}


