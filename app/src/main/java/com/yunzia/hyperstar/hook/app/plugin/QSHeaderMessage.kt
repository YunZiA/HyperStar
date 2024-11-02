package com.yunzia.hyperstar.hook.app.plugin

import android.os.Handler
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

class QSHeaderMessage : BaseHooker() {
    private val showMessage= XSPUtils.getBoolean("close_header_show_message",false)
    val showMessageMillis: Float = XSPUtils.getFloat("header_show_message_millis",1f)*1000

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        startMethodsHook()
    }

    private fun startMethodsHook() {


        val MessageHeaderController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.header.MessageHeaderController",classLoader)

        if (showMessage){
            XposedHelpers.findAndHookMethod(MessageHeaderController,"showMsg",CharSequence::class.java, object : XC_MethodReplacement(){
                override fun replaceHookedMethod(param: MethodHookParam?): Any? {

                    return null;

                }

            })

        }
        else if (showMessageMillis != 1000f){
            val showConfig = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.header.MessageHeaderController\$Msg\$showConfig\$1",classLoader)



            XposedHelpers.findAndHookMethod(showConfig,"onComplete",Any::class.java, object : XC_MethodReplacement(){
                override fun replaceHookedMethod(param: MethodHookParam?): Any? {
                    val thisObj = param?.thisObject
                    val messageHeaderController = XposedHelpers.getObjectField(thisObj,"this\$0")
                    val msg = XposedHelpers.getObjectField(thisObj,"this\$1")

                    val uiHandler = XposedHelpers.getObjectField(messageHeaderController,"uiHandler") as Handler
                    val hideMsgCallback = XposedHelpers.getObjectField(msg,"hideRunnable") as Runnable

                    uiHandler.postDelayed(hideMsgCallback,showMessageMillis.toLong())

                    return null;

                }

            })

        }
    }


}