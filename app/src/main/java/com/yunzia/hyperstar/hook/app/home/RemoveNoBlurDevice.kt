package com.yunzia.hyperstar.hook.app.home

import com.yunzia.hyperstar.hook.core.BaseHook
import com.yunzia.hyperstar.hook.core.finder.findClassWithPrefix
import com.yunzia.hyperstar.hook.core.helper.replaceHookMethod
import com.yunzia.hyperstar.prefs.XSPUtils
import io.github.kyuubiran.ezxhelper.core.ClassLoaderProvider

object RemoveNoBlurDevice : BaseHook() {

    val isUnlock = XSPUtils.getBoolean("is_unlock_home_blur",false)

    override fun init() {

        if (!isUnlock) return

        findClassWithPrefix(
            "com.miui.home.launcher.common.BlurUtilities",
            "com.miui.home.common.utils.BlurUtilities"
        ).replaceHookMethod(
            "isNotSupportBlurDevice"
        ){
            return@replaceHookMethod false
        }


    }


}