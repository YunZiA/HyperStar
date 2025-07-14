package com.yunzia.hyperstar.hook.init

import com.yunzia.annotations.Init
import com.yunzia.hyperstar.hook.app.mms.AutoCopyVerificationCode
import com.yunzia.hyperstar.hook.base.InitHooker
import de.robv.android.xposed.callbacks.XC_LoadPackage

@Init(packageName = "com.android.mms")
class InitMMSHook: InitHooker() {

    override fun initHook() {

        AutoCopyVerificationCode().initHooker()

    }

}