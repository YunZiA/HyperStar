package com.yunzia.hyperstar.hook.init

//import com.yunzia.annotations.Init
import com.yunzia.hyperstar.hook.core.annotation.Init
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.base.BaseHooks
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.prefs.XSPUtils

@Init(packageName = "com.android.thememanager")
object InitThemeManagerHook: BaseHooks() {

    override fun init() {
        //AndroidAppHelper.currentApplication().packageManager.
        if (XSPUtils.getBoolean("is_unlock_ai_wallpaper",false)){

            findClass(
                "com.android.thememanager.basemodule.utils.wvg"
            ).afterHookMethod(
                "jk"
            ) { args, result ->
                if (result.value == false){
                    result.replace(true)
                }
            }
        }

    }

}