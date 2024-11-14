package com.yunzia.hyperstar.hook.app.plugin

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.hook.tool.starLog
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

class VolumeView: BaseHooker() {

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)

        val MiuiVolumeDialogView = XposedHelpers.findClass("com.android.systemui.miui.volume.MiuiVolumeDialogView",classLoader)
        XposedHelpers.findAndHookConstructor(MiuiVolumeDialogView,Context::class.java,AttributeSet::class.java,Int::class.java,object : XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                starLog.log("MiuiVolumeDialogView findAndHookConstructor")
                val thisObj = param?.thisObject
                XposedHelpers.setObjectField(thisObj,"expandListener",null)

            }
        })

        XposedHelpers.findAndHookMethod(MiuiVolumeDialogView,"onFinishInflate",object :XC_MethodHook(){
            override fun beforeHookedMethod(param: MethodHookParam?) {
                super.beforeHookedMethod(param)
                starLog.log("MiuiVolumeDialogView findAndHookConstructor before")
                val thisObj = param?.thisObject
                XposedHelpers.setObjectField(thisObj,"expandListener",null)
                //this.expandListener

            }
            override fun afterHookedMethod(param: MethodHookParam?) {
                starLog.log("MiuiVolumeDialogView onFinishInflate after")
                val thisObj = param?.thisObject
                val mExpandButton = XposedHelpers.getObjectField(thisObj,"mExpandButton") as View
                mExpandButton.setOnClickListener(null)
                mExpandButton.alpha = 0f
                mExpandButton.isClickable = false
                mExpandButton.visibility = View.GONE
                if (mExpandButton.isClickable){
                    starLog.log("MiuiVolumeDialogView mExpandButton isClickable = true")

                }

            }
        })

//        XposedHelpers.findAndHookMethod(MiuiVolumeDialogView,"onFinishInflate",object :XC_MethodReplacement(){
//
//
//            override fun replaceHookedMethod(param: MethodHookParam?): Any? {
//                return null
//            }
//        })


        XposedHelpers.findAndHookMethod(MiuiVolumeDialogView,"notifyAccessibilityChanged",Boolean::class.java,object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                starLog.log("MiuiVolumeDialogView notifyAccessibilityChanged")
                val thisObj = param?.thisObject
                val mExpandButton = XposedHelpers.getObjectField(thisObj,"mExpandButton") as View
                mExpandButton.setOnClickListener(null)
                mExpandButton.isClickable = false
                mExpandButton.visibility = View.GONE

            }
        })




    }

}