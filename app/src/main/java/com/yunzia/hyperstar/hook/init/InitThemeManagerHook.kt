package com.yunzia.hyperstar.hook.init

import android.app.AndroidAppHelper
import com.yunzia.annotations.Init
import com.yunzia.hyperstar.hook.base.InitHooker
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.utils.XSPUtils

@Init(packageName = "com.android.thememanager")
class InitThemeManagerHook: InitHooker() {

    override fun initHook() {
        //AndroidAppHelper.currentApplication().packageManager.
        if (XSPUtils.getBoolean("is_unlock_ai_wallpaper",false)){

            findClass(
                "com.android.thememanager.basemodule.utils.wvg",
                classLoader
            ).afterHookMethod(
                "jk"
            ){
                if (it.result == false){
                    it.result = true
                }
            }
        }



    }

}