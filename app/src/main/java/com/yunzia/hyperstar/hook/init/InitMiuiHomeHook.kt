package com.yunzia.hyperstar.hook.init

import com.yunzia.hyperstar.hook.app.home.RemoveNoBlurDevice
import com.yunzia.hyperstar.hook.base.Init
import com.yunzia.hyperstar.hook.base.InitHooker

@Init(packageName = "com.miui.home")
class InitMiuiHomeHook : InitHooker() {

    override fun initHook() {
        initHooker(RemoveNoBlurDevice())
    }

}