package com.yunzia.hyperstar.hook.base

import com.yunzia.hyperstar.hook.app.home.RemoveNoBlurDevice
import com.yunzia.hyperstar.hook.tool.starLog
import de.robv.android.xposed.callbacks.XC_LoadPackage

class InitMiuiHomeHook : BaseHooker() {
    override fun doMethods(loadPackageParam: XC_LoadPackage.LoadPackageParam?) {
        super.doMethods(loadPackageParam)
        if (loadPackageParam?.packageName == "com.miui.home") {
            starLog.log("Loaded app: " + loadPackageParam.packageName)
            doMethodsHook(loadPackageParam.classLoader)
        }
        
    }

    override fun doMethodsHook(classLoader: ClassLoader?) {
        super.doMethodsHook(classLoader)
        doBaseMethods(RemoveNoBlurDevice())


    }

}