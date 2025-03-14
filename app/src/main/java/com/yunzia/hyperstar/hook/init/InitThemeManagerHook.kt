package com.yunzia.hyperstar.hook.init

import com.yunzia.hyperstar.hook.base.Init
import com.yunzia.hyperstar.hook.base.InitHooker
import com.yunzia.hyperstar.utils.XSPUtils

@Init(packageName = "com.android.thememanager")
class InitThemeManagerHook: InitHooker() {

    override fun initHook() {

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