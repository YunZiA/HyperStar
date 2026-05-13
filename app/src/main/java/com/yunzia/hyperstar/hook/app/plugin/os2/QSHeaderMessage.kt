package com.yunzia.hyperstar.hook.app.plugin.os2

import android.os.Handler
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.getObjectField
import com.yunzia.hyperstar.hook.core.helper.getObjectFieldAs
import com.yunzia.hyperstar.hook.core.helper.replaceHookMethod
import com.yunzia.hyperstar.prefs.XSPUtils

object QSHeaderMessage : BasePluginHook() {
    private val showMessage= XSPUtils.getBoolean("close_header_show_message",false)
    val showMessageMillis: Float = XSPUtils.getFloat("header_show_message_millis",1f)*1000

    override fun init() {
        if (showMessage){
            findClass(
                "miui.systemui.controlcenter.panel.main.header.MessageHeaderController",
                pluginClassLoader
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
                pluginClassLoader
            ).replaceHookMethod(
                "onComplete",
                Any::class.java
            ) {
                val messageHeaderController = thisObject.getObjectField("this\$0")
                val msg = thisObject.getObjectField("this\$1")
                val uiHandler = messageHeaderController.getObjectFieldAs<Handler>("uiHandler")
                val hideMsgCallback = msg.getObjectFieldAs<Runnable>("hideRunnable")
                uiHandler.postDelayed(hideMsgCallback,showMessageMillis.toLong())
                return@replaceHookMethod null
            }
        }
    }
}