package com.yunzia.hyperstar.hook.app.Home.Beta

import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class BetaBigIcon : BaseHooker() {

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        if (!XSPUtils.getBoolean("is_use_beta_home_cc",false)) return
        startMethodsHook(classLoader)
    }

    private fun startMethodsHook(classLoader: ClassLoader?) {

        val RomUtils = XposedHelpers.findClass("miuix.core.util.RomUtils",classLoader)

        XposedHelpers.findAndHookMethod(RomUtils,"getHyperOsVersionNoCache",object : XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                param?.result = 2
            }
        })


    }

}