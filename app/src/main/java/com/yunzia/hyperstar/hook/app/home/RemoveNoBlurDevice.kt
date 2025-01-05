package com.yunzia.hyperstar.hook.app.home

import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodReplacement.returnConstant
import de.robv.android.xposed.XposedHelpers

class RemoveNoBlurDevice : Hooker() {


    val isUnlock = XSPUtils.getBoolean("is_unlock_home_blur",false)


    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        if (!isUnlock) return

        val BlurUtilities = XposedHelpers.findClass("com.miui.home.launcher.common.BlurUtilities",classLoader)


        XposedHelpers.findAndHookMethod(BlurUtilities,"isNotSupportBlurDevice", returnConstant(false))


    }


}