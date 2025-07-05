package com.yunzia.hyperstar.hook.init

import com.yunzia.annotations.Init
import com.yunzia.hyperstar.hook.app.home.RemoveNoBlurDevice
import com.yunzia.hyperstar.hook.app.screenshot.EnableClipboardWriteOnScreenshot
import com.yunzia.hyperstar.hook.base.InitHooker


@Init(packageName = "com.miui.screenshot")
class InitMiuiScreenshot : InitHooker() {

    override fun initHook() {
        initHooker(EnableClipboardWriteOnScreenshot())
    }

}