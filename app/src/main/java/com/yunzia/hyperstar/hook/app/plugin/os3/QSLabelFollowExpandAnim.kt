package com.yunzia.hyperstar.hook.app.plugin.os3

import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.base.replaceHookMethod
import com.yunzia.hyperstar.utils.XSPUtils

class QSLabelFollowExpandAnim : Hooker(){

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        if (!XSPUtils.getBoolean("title_follow_anim", false)) return
        findClass(
            "miui.systemui.controlcenter.panel.main.qs.QSItemViewHolder",
            classLoader
        ).apply {
//            replaceHookMethod("getItemFrame"){
//
//            }
        }.findMethodExt(
            "getIconFrame",
            { isBridge && isSynthetic }
        ).replace {
            val itemView = this.getObjectField("itemView")
            return@replace itemView
        }
    }

}