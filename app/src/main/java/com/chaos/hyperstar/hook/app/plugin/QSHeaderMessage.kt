package com.chaos.hyperstar.hook.app.plugin

import android.content.res.XModuleResources
import android.os.Handler
import com.chaos.hyperstar.hook.base.BaseHooker
import com.chaos.hyperstar.utils.XSPUtils
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


        val MainPanelHeaderController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.header.MainPanelHeaderController",classLoader)

        if (showMessage){
            XposedHelpers.findAndHookMethod(MainPanelHeaderController,"showMessage",CharSequence::class.java, object : XC_MethodReplacement(){
                override fun replaceHookedMethod(param: MethodHookParam?): Any? {

                    return null;

                }

            })

        }
        else if (showMessageMillis != 1000f){
            XposedHelpers.findAndHookMethod(MainPanelHeaderController,"handleShowMessage",CharSequence::class.java, object : XC_MethodReplacement(){
                override fun replaceHookedMethod(param: MethodHookParam?): Any? {
                    //this.headerMsgController.changeMsg
                    //this.uiHandler.postDelayed
                    val thisObj = param?.thisObject
                    val message = param?.args?.get(0)
                    val headerMsgController = XposedHelpers.getObjectField(thisObj,"headerMsgController")
                    XposedHelpers.callMethod(headerMsgController,"changeMsg",message)
                    XposedHelpers.callMethod(thisObj,"changeTarget",headerMsgController,true)
                    val uiHandler = XposedHelpers.getObjectField(thisObj,"uiHandler") as Handler
                    val hideMsgCallback = XposedHelpers.getObjectField(thisObj,"hideMsgCallback") as Runnable
                    uiHandler.postDelayed(hideMsgCallback,showMessageMillis.toLong())

                    return null;

                }

            })

        }
    }


}