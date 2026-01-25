package com.yunzia.hyperstar.hook.init

//import com.yunzia.annotations.Init
import com.yunzia.hyperstar.hook.app.home.RemoveNoBlurDevice
import com.yunzia.hyperstar.hook.app.screenshot.EnableClipboardWriteOnScreenshot
import com.yunzia.hyperstar.hook.core.BaseHooks
import com.yunzia.hyperstar.hook.core.annotation.Init


@Init(packageName = "com.miui.screenshot", versions = [2, 3])
object InitMiuiScreenshot : BaseHooks() {

    override fun init() {
        initHooks(
            EnableClipboardWriteOnScreenshot
        )
    }

}