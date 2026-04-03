package com.yunzia.hyperstar.hook.init

import com.yunzia.hyperstar.hook.app.barrage.FuckBarrageNotificationClick
import com.yunzia.hyperstar.hook.core.base.BaseHooks
import com.yunzia.hyperstar.hook.core.annotation.Init

@Init(packageName = "com.xiaomi.barrage")
object InitBarrageHook: BaseHooks() {

    override fun init() {
        initHooks(
            FuckBarrageNotificationClick
        )
    }

}