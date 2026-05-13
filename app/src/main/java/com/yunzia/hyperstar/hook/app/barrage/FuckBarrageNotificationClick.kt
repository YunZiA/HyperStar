package com.yunzia.hyperstar.hook.app.barrage

import com.yunzia.hyperstar.hook.app.home.RemoveNoBlurDevice
import com.yunzia.hyperstar.hook.core.StarLog
import com.yunzia.hyperstar.hook.core.base.BaseHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.replaceHookMethod
import com.yunzia.hyperstar.prefs.XSPUtils

object FuckBarrageNotificationClick: BaseHook() {

    override fun init() {
        if (XSPUtils.getBoolean("is_disable_barrage_click", false)) {
            findClass(
                "com.xiaomi.barrage.utils.BarrageWindowUtils"
            ).replaceHookMethod(
                "initListener"
            ) {
                return@replaceHookMethod null
            }
        }
    }

}