package com.yunzia.hyperstar.hook.app.plugin

import com.yunzia.hyperstar.hook.base.Hooker

class QSMiplayView : Hooker(){
    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        startMethodsHook(classLoader)
    }

    private fun startMethodsHook(classLoader: ClassLoader?) {


    }


}