package com.yunzia.hyperstar.hook.app.home

import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.findClassWithPrefix
import com.yunzia.hyperstar.hook.base.replaceHookMethod
import com.yunzia.hyperstar.utils.XSPUtils

class RemoveNoBlurDevice : Hooker() {

    val isUnlock = XSPUtils.getBoolean("is_unlock_home_blur",false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        if (!isUnlock) return


        classLoader.findClassWithPrefix(
            "com.miui.home.launcher.common.BlurUtilities",
            "com.miui.home.common.utils.BlurUtilities"
        ).replaceHookMethod(
            "isNotSupportBlurDevice"
        ){
            return@replaceHookMethod false
        }



    }


}