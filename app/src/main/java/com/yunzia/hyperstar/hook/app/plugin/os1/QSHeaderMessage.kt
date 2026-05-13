package com.yunzia.hyperstar.hook.app.plugin.os1

import android.os.Handler
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.callMethod
import com.yunzia.hyperstar.hook.core.helper.getObjectField
import com.yunzia.hyperstar.hook.core.helper.replaceHookMethod
import com.yunzia.hyperstar.prefs.XSPUtils

object QSHeaderMessage : BasePluginHook() {
    private val showMessage= XSPUtils.getBoolean("close_header_show_message",false)
    val showMessageMillis: Float = XSPUtils.getFloat("header_show_message_millis",1f)*1000

    override fun init() {
        findClass(
            "miui.systemui.controlcenter.panel.main.header.MainPanelHeaderController",
            pluginClassLoader
        ).apply {
            if (showMessage){
                replaceHookMethod(
                    "showMessage",
                    CharSequence::class.java
                ) {
                    return@replaceHookMethod null
                }

            } else if (showMessageMillis != 1000f){
                replaceHookMethod(
                    "handleShowMessage",
                    CharSequence::class.java
                ) {
                    val message = it[0]
                    val headerMsgController = getObjectField("headerMsgController")
                    headerMsgController.callMethod("changeMsg",message)
                    callMethod("changeTarget",headerMsgController,true)
                    val uiHandler = getObjectField("uiHandler") as Handler
                    val hideMsgCallback = getObjectField("hideMsgCallback") as Runnable
                    uiHandler.postDelayed(hideMsgCallback,showMessageMillis.toLong())
                    return@replaceHookMethod null
                }
            }
        }
    }
}