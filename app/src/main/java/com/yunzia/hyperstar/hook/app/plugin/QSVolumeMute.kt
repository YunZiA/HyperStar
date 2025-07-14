package com.yunzia.hyperstar.hook.app.plugin

import com.yunzia.hyperstar.hook.base.Hooker

class QSVolumeMute: Hooker() {


    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
//        findClass(
//            "miui.systemui.controlcenter.panel.main.recyclerview.ToggleSliderViewHolder",classLoader
//        ).afterHookMethod(
//            "setInMirror",
//            Boolean::class.java
//        ){
//            this.setObjectField("inMirror",false)
//        }
    }
}