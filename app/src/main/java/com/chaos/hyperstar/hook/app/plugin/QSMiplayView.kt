package com.chaos.hyperstar.hook.app.plugin

import android.content.res.XModuleResources
import com.chaos.hyperstar.hook.base.BaseHooker

class QSMiplayView : BaseHooker(){
    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)

        startMethodsHook(classLoader)
    }

    private fun startMethodsHook(classLoader: ClassLoader?) {


    }


}