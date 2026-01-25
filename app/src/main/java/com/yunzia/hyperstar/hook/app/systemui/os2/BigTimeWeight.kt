package com.yunzia.hyperstar.hook.app.systemui.os2

import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.TextView
import com.yunzia.hyperstar.hook.core.BaseHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.Log
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.afterHookAllMethods
import com.yunzia.hyperstar.hook.core.helper.beforeHookMethod
import com.yunzia.hyperstar.hook.core.helper.callMethodAs
import com.yunzia.hyperstar.hook.core.helper.getObjectField
import com.yunzia.hyperstar.hook.core.helper.setObjectField

object Test : BaseHook() {


//    override fun initResources(
//        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
//        modRes: XModuleResources?
//    ) {
//        super.initResources(resparam, modRes)
//
//        resparam?.res?.hookLayout(
//            systemUI,
//            "layout",
//            "hybrid_notification",
//            object : XC_LayoutInflated(){
//                override fun handleLayoutInflated(liparam: LayoutInflatedParam) {
//                    logE("hybrid_notification hooked")
//                    val root = liparam.view as ViewGroup
//                    root.findViewByIdNameAs<TextView>(
//                        "notification_title"
//                    ).typeface = Typeface.defaultFromStyle(Typeface.BOLD)
//                }
//
//            }
//        )
//
//        resparam?.res?.hookLayout(
//            systemUI,
//            "layout",
//            "hybrid_conversation_notification",
//            object : XC_LayoutInflated(){
//                override fun handleLayoutInflated(liparam: LayoutInflatedParam) {
//                    logE("hybrid_notification hooked")
//                    val root = liparam.view as ViewGroup
//                    root.findViewByIdNameAs<TextView>(
//                        "notification_title"
//                    ).typeface = Typeface.defaultFromStyle(Typeface.BOLD)
//                }
//
//            }
//        )


//        resparam?.res?.setReplacement(
//            systemUI,
//            "dimen",
//            "notification_title_text_size",
//            modRes?.fwd(
//                R.dimen.notification_title_text_size
//            )
//        )
//    }

    override fun init() {
        
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
            "com.android.systemui.statusbar.notification.row.HybridGroupManager"
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
            "com.android.systemui.controlcenter.shade.NotificationHeaderExpandController"
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