package com.yunzia.hyperstar.hook.init

import com.yunzia.hyperstar.hook.base.Init
import com.yunzia.hyperstar.hook.base.InitHooker
import com.yunzia.hyperstar.utils.XSPUtils

@Init(packageName = "com.xiaomi.barrage")
class InitBarrageHook: InitHooker() {

    override fun initHook() {

        if (XSPUtils.getBoolean("is_disable_barrage_click",false)){

            findClass(
                "com.xiaomi.barrage.utils.BarrageWindowUtils",
                classLoader
            ).replaceHookMethod(
                "initListener"
            ){
                return@replaceHookMethod null
            }
        }


    }

}