package com.yunzia.hyperstar.hook.app.plugin.os1

import android.os.Handler
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.base.replaceHookMethod
import com.yunzia.hyperstar.utils.XSPUtils

class QSHeaderMessage : Hooker() {
    private val showMessage= XSPUtils.getBoolean("close_header_show_message",false)
    val showMessageMillis: Float = XSPUtils.getFloat("header_show_message_millis",1f)*1000

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        startMethodsHook()
    }

    private fun startMethodsHook() {

        findClass(
            "miui.systemui.controlcenter.panel.main.header.MainPanelHeaderController",
            classLoader
        ).apply {
            if (showMessage){
                replaceHookMethod(
                    "showMessage",
                    CharSequence::class.java
                ){

                    return@replaceHookMethod null
                }

            } else if (showMessageMillis != 1000f){
                replaceHookMethod(
                    "handleShowMessage",
                    CharSequence::class.java
                ){
                    val message = it.args[0]
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