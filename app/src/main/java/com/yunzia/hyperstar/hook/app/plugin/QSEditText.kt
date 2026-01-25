package com.yunzia.hyperstar.hook.app.plugin

import com.yunzia.hyperstar.hook.core.BasePluginHook
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.stringReplaceByValue
import com.yunzia.hyperstar.prefs.XSPUtils

object QSEditText: BasePluginHook() {

    private val mEditTitle = XSPUtils.getString("qs_customize_entry_button_text","null")

    override fun init() {
        if (mEditTitle == "null") return
        stringReplaceByValue("qs_customize_entry_button_text", plugin, mEditTitle)
    }
}