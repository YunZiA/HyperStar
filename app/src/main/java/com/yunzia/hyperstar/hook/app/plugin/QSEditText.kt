package com.yunzia.hyperstar.hook.app.plugin

import android.content.res.XModuleResources
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.callbacks.XC_InitPackageResources

class QSEditText: Hooker() {

    val mEditTitle = XSPUtils.getString("qs_customize_entry_button_text","null")


    override fun initResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
        modRes: XModuleResources?
    ) {
        super.initResources(resparam, modRes)
        if (mEditTitle == "null") return

        resparam?.res?.setReplacement(plugin,"string","qs_customize_entry_button_text",mEditTitle)


    }
}