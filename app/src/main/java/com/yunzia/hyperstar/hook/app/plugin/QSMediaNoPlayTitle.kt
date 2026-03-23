package com.yunzia.hyperstar.hook.app.plugin

import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.stringReplaceByValue
import com.yunzia.hyperstar.prefs.XSPUtils

object QSMediaNoPlayTitle : BasePluginHook() {

    val mHeaderTitle = XSPUtils.getString("miplay_detail_header_no_song","null")


    override fun init() {
        if (mHeaderTitle == "null") return
        stringReplaceByValue("miplay_detail_header_no_song", plugin, mHeaderTitle)
    }
}