package com.yunzia.hyperstar.hook.base

import com.yunzia.hyperstar.hook.app.Home.Beta.BetaBigIcon
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
        doBaseMethods(BetaBigIcon())
        //BetaBigIcon().doMethods(classLoader)

    }
//    @Override
//    fun doMethodsHook( classLoader:ClassLoader) {
//        BetaBigIcon().doMethods(classLoader)
//    }


}