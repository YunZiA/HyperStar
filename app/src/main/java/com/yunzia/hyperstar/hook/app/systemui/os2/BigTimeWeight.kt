package com.yunzia.hyperstar.hook.app.systemui.os2

import android.content.res.XModuleResources
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.TextView
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.util.starLog
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LayoutInflated

class Test : Hooker() {


    override fun initResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
        modRes: XModuleResources?
    ) {
        super.initResources(resparam, modRes)

        resparam?.res?.hookLayout(
            systemUI,
            "layout",
            "hybrid_notification",
            object : XC_LayoutInflated(){
                override fun handleLayoutInflated(liparam: LayoutInflatedParam) {
                    starLog.logE("hybrid_notification hooked")
                    val root = liparam.view as ViewGroup
                    root.findViewByIdNameAs<TextView>(
                        "notification_title"
                    ).typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                }

            }
        )

        resparam?.res?.hookLayout(
            systemUI,
            "layout",
            "hybrid_conversation_notification",
            object : XC_LayoutInflated(){
                override fun handleLayoutInflated(liparam: LayoutInflatedParam) {
                    starLog.logE("hybrid_notification hooked")
                    val root = liparam.view as ViewGroup
                    root.findViewByIdNameAs<TextView>(
                        "notification_title"
                    ).typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                }

            }
        )


//        resparam?.res?.setReplacement(
//            systemUI,
//            "dimen",
//            "notification_title_text_size",
//            modRes?.fwd(
//                R.dimen.notification_title_text_size
//            )
//        )
    }

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
//        findClass(
//            "com.android.systemui.statusbar.notification.row.HybridNotificationView",
//            classLoader
//        ).apply {
//            afterHookAllMethods(
//                "bind"
//            ){
//                val mTitleView  = this.getObjectFieldAs<TextView>("mTitleView")
//                mTitleView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
//
//            }
//            afterHookMethod("onFinishInflate"){
//                val mTitleView  = this.getObjectFieldAs<TextView>("mTitleView")
//                mTitleView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
//            }
//        }

        findClass(
            "com.android.systemui.statusbar.notification.row.HybridGroupManager",
            classLoader
        ).apply {
            afterHookAllMethods("bindFromNotification"){

                val hybridNotificationView  = it.result
                hybridNotificationView.callMethodAs<TextView>("getTitleView").typeface = Typeface.defaultFromStyle(Typeface.BOLD)

            }
        }





//        findClass("com.android.systemui.statusbar.notification.row.NotificationContentInflaterInjector",classLoader).apply {
//            afterHookAllMethods("handleTitle"){
//                val builderRemoteViews = it.args[0] as RemoteViews
//                builderRemoteViews.
//
//            }
//        }


        //notificationTextWeight()

        //bigTimeWeight()


    }

    private fun notificationTextWeight() {

    }

    private fun bigTimeWeight(){

        findClass(
            "com.android.systemui.controlcenter.shade.NotificationHeaderExpandController",
            classLoader
        ).beforeHookMethod(
            "updateWeight",
            Float::class.java
        ) {
            this.setObjectField(
                "miproNormal",
                this.getObjectField("miproMedium")!!
            )

        }
    }


}