package com.yunzia.hyperstar.hook.app.plugin.os1

import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodReplacement.returnConstant
import de.robv.android.xposed.XposedHelpers

class QSEditButton : BaseHooker() {

    private val closeEditButtonShow = XSPUtils.getBoolean("close_edit_button_show",false)

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        if (!closeEditButtonShow){
            return
        }
        startMethodsHook()
    }

    private fun startMethodsHook() {

        XposedHelpers.findAndHookMethod("miui.systemui.controlcenter.panel.main.qs.EditButtonController", classLoader, "available", Boolean::class.java, returnConstant(false));
        XposedHelpers.findAndHookMethod("miui.systemui.controlcenter.panel.main.qs.EditButtonController", classLoader, "available", Boolean::class.java, "miui.systemui.controlcenter.panel.main.MainPanelModeController\$MainPanelMode", returnConstant(false));



    }
}