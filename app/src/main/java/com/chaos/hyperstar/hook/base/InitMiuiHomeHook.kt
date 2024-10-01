package com.chaos.hyperstar.hook.base

import com.chaos.hyperstar.hook.app.Home.Beta.BetaBigIcon
import com.chaos.hyperstar.hook.tool.starLog
import de.robv.android.xposed.callbacks.XC_LoadPackage

class InitMiuiHomeHook : BaseHooker() {
    override fun doMethods(loadPackageParam: XC_LoadPackage.LoadPackageParam?) {
        super.doMethods(loadPackageParam)
        if (loadPackageParam?.packageName == "com.miui.home") {
            starLog.log("Loaded app: " + loadPackageParam.packageName)
            doMethodsHook(loadPackageParam.classLoader)
        }
        
    }

    private fun doMethodsHook(classLoader: ClassLoader?) {
        BetaBigIcon().doMethods(classLoader)
    }


}