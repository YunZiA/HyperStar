package com.yunzia.hyperstar.hook.init

//import com.yunzia.annotations.Init
import com.yunzia.hyperstar.hook.app.home.RemoveNoBlurDevice
import com.yunzia.hyperstar.hook.app.mms.AutoCopyVerificationCode
import com.yunzia.hyperstar.hook.core.BaseHooks
import com.yunzia.hyperstar.hook.core.annotation.Init

@Init(packageName = "com.android.mms")
object InitMMSHook: BaseHooks() {

    override fun init() {
        initHooks(
            AutoCopyVerificationCode
        )
    }

}