package com.yunzia.hyperstar.hook.init

import com.yunzia.hyperstar.hook.app.home.RemoveNoBlurDevice
import com.yunzia.hyperstar.hook.core.BaseHooks
import com.yunzia.hyperstar.hook.core.annotation.Init

@Init(packageName = "com.miui.home", versions = [2, 3])
object InitMiuiHomeHook : BaseHooks() {

    override fun init() {
        initHooks(
            RemoveNoBlurDevice
        )
    }

}