package com.yunzia.hyperstar.hook.init

//import com.yunzia.annotations.Init
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.replaceHookMethod
import com.yunzia.hyperstar.hook.core.base.BaseHooks
import com.yunzia.hyperstar.hook.core.annotation.Init
import com.yunzia.hyperstar.prefs.XSPUtils

@Init(packageName = "com.xiaomi.barrage")
object InitBarrageHook: BaseHooks() {

    override fun init() {

        if (XSPUtils.getBoolean("is_disable_barrage_click",false)){

            findClass(
                "com.xiaomi.barrage.utils.BarrageWindowUtils"
            ).replaceHookMethod(
                "initListener"
            ){
                return@replaceHookMethod null
            }
        }


    }

}