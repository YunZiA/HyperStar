package com.chaos.hyperstar.hook.app.plugin

import com.chaos.hyperstar.hook.base.BaseHooker
import com.chaos.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodReplacement.returnConstant
import de.robv.android.xposed.XposedHelpers

class QSEditButton : BaseHooker() {
    val close_edit_button_show = XSPUtils.getBoolean("close_edit_button_show",false)
    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        if (!close_edit_button_show){
            return
        }
        startMethodsHook(classLoader)
    }

    private fun startMethodsHook(classLoader: ClassLoader?) {

        XposedHelpers.findAndHookMethod("miui.systemui.controlcenter.panel.main.qs.EditButtonController", classLoader, "available", Boolean::class.java, returnConstant(false));
        XposedHelpers.findAndHookMethod("miui.systemui.controlcenter.panel.main.qs.EditButtonController", classLoader, "available", Boolean::class.java, "miui.systemui.controlcenter.panel.main.MainPanelModeController\$MainPanelMode", returnConstant(false));



    }
}