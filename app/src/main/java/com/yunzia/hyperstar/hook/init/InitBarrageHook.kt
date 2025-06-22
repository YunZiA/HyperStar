package com.yunzia.hyperstar.hook.init

import com.yunzia.annotations.Init
import com.yunzia.hyperstar.hook.base.InitHooker
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.base.replaceHookMethod
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