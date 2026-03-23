package com.yunzia.hyperstar.hook.app.plugin.os3

import com.yunzia.hyperstar.hook.base.BaseHookHelper.findMethodExt
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.getObjectField
import com.yunzia.hyperstar.hook.core.helper.replaceHook
import com.yunzia.hyperstar.prefs.XSPUtils

object QSLabelFollowExpandAnim : BasePluginHook(){

    override fun init() {
        if (!XSPUtils.getBoolean("title_follow_anim", false)) return
        findClass(
            "miui.systemui.controlcenter.panel.main.qs.QSItemViewHolder",
            pluginClassLoader
        ).apply {
//            replaceHookMethod("getItemFrame"){
//
//            }
        }.findMethodExt(
            "getIconFrame",
            { isBridge && isSynthetic }
        )?.replaceHook {
            val itemView = thisObject.getObjectField("itemView")
            return@replaceHook itemView
        }
    }

}