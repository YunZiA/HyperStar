package com.yunzia.hyperstar.hook.app.plugin.os2

import android.content.Context
import android.provider.Settings
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.util.starLog
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers


class QSHeaderViewListener : Hooker() {

    private val is_use_chaos_header = XSPUtils.getBoolean("is_use_chaos_header",false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

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
                    starLog.logE("ControlCenterHeaderController editId == null")
                    return
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
                    starLog.logE("qsListControllerProviders == null")
                    return
                }
                qsListController = XposedHelpers.callMethod(qsListControllerProvider,"get")
            }
        })

    }


}


