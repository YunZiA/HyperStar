package com.yunzia.hyperstar.hook.app.plugin

import com.yunzia.hyperstar.hook.base.BaseHooker

class QSMiplayView : BaseHooker(){
    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)

        startMethodsHook(classLoader)
    }

    private fun startMethodsHook(classLoader: ClassLoader?) {


    }


}