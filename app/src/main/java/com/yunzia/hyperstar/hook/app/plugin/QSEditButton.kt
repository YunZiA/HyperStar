package com.yunzia.hyperstar.hook.app.plugin

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


    }
}