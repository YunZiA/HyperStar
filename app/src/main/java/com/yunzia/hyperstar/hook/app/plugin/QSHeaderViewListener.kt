package com.yunzia.hyperstar.hook.app.plugin

import android.content.Context
import android.provider.Settings
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers


class QSHeaderViewListener : BaseHooker() {

    private val is_use_chaos_header = XSPUtils.getBoolean("is_use_chaos_header",false)

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)

        if (!is_use_chaos_header){
            return
        }
        startMethodsHook()
    }


    private fun startMethodsHook() {
        var qsListController: Any? = null
        val MainPanelModeController = findClass("miui.systemui.controlcenter.panel.main.MainPanelController\$Mode",classLoader)

        val MainPanelHeaderController  = findClass("miui.systemui.controlcenter.panel.main.header.MainPanelHeaderController",classLoader)

        XposedBridge.hookAllConstructors(MainPanelHeaderController,object :XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                val thisObj = param?.thisObject
                val context = XposedHelpers.callMethod(thisObj,"getContext") as Context
                val controlCenterHeader = XposedHelpers.getObjectField(thisObj,"controlCenterHeader")
                val controlCenterHeaderView = XposedHelpers.getObjectField(controlCenterHeader,"controlCenterHeaderView") as ViewGroup

                val editId = Settings.System.getInt(context.contentResolver,"cc_edit_Id",0)
                if (editId == 0){
                    starLog.log("ControlCenterHeaderController editId == null")
                    return
                }
                val editButton = controlCenterHeaderView.findViewById<View>(editId)
                starLog.log("ControlCenterHeaderController ${editButton}")

                editButton.setOnClickListener {
                    if(controlCenterHeaderView.alpha == 0f) return@setOnClickListener
                    it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    if (qsListController != null){
                        starLog.log("qsListControllerProvider != null")

                        val mainPanelMode: Array<out Any>? = MainPanelModeController.enumConstants
                        if (mainPanelMode == null){
                            starLog.log("enumConstants == null")
                            return@setOnClickListener
                        }

                        starLog.log(""+mainPanelMode[0])
                        XposedHelpers.callMethod(qsListController,"startQuery",mainPanelMode[2])
                    }
                }
            }
        })


        val EditButtonController_Factory = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.qs.EditButtonController_Factory",classLoader)

        XposedBridge.hookAllConstructors(EditButtonController_Factory,object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject
                val qsListControllerProvider = XposedHelpers.getObjectField(thisObj,"qsListControllerProvider")

                if (qsListControllerProvider == null){
                    starLog.log("qsListControllerProviders == null")
                    return
                }
                starLog.log("qsListControllerProviders != null")
                qsListController = XposedHelpers.callMethod(qsListControllerProvider,"get")
            }
        })

    }


}


