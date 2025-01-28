package com.yunzia.hyperstar.hook.app.plugin.os2

import android.os.Handler
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.utils.XSPUtils

class QSHeaderMessage : Hooker() {
    private val showMessage= XSPUtils.getBoolean("close_header_show_message",false)
    val showMessageMillis: Float = XSPUtils.getFloat("header_show_message_millis",1f)*1000

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        startMethodsHook()
    }

    private fun startMethodsHook() {
        if (showMessage){
            findClass(
                "miui.systemui.controlcenter.panel.main.header.MessageHeaderController",
                classLoader
            ).replaceHookMethod(
                "showMsg",
                CharSequence::class.java
            ) {
                return@replaceHookMethod null
            }

        }
        else if (showMessageMillis != 1000f){
            findClass(
                "miui.systemui.controlcenter.panel.main.header.MessageHeaderController\$Msg\$showConfig\$1",
                classLoader
            ).replaceHookMethod(
                "onComplete",
                Any::class.java
            ) {
                val messageHeaderController = this.getObjectField("this\$0")
                val msg = this.getObjectField("this\$1")
                val uiHandler = messageHeaderController.getObjectFieldAs<Handler>("uiHandler")
                val hideMsgCallback = msg.getObjectFieldAs<Runnable>("hideRunnable")

                uiHandler.postDelayed(hideMsgCallback,showMessageMillis.toLong())
                return@replaceHookMethod null

            }

        }
    }


}