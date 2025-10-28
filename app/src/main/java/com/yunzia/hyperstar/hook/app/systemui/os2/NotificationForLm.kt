package com.yunzia.hyperstar.hook.app.systemui.os2

import android.app.Notification
import android.content.pm.ApplicationInfo
import android.os.Parcelable
import android.text.TextUtils
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.base.replaceHookMethod
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.utils.XSPUtils


class NotificationForLm:Hooker() {

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        val miuiBaseNotifUtil = findClass("com.miui.systemui.notification.MiuiBaseNotifUtil",classLoader)
        val appList = XSPUtils.getString("notification_icon_type_whitelist", "com.tencent.mm") ?: return

        if (appList == "||") return

        miuiBaseNotifUtil.replaceHookMethod("getNotificationTypeForIm",Notification::class.java){
            val notification = it.args[0] as Notification
            val notifExtras = notification.extras

            if (!TextUtils.isEmpty(notifExtras.getCharSequence("hyperOs.category"))) {
                return@replaceHookMethod  0
            }


            if (notification.largeIcon != null || notification.getLargeIcon() != null) {
                val applicationInfo = notifExtras.getParcelable<Parcelable>("android.appInfo") as ApplicationInfo?
                val packageName = applicationInfo?.packageName ?:return@replaceHookMethod -1
                if (appList.contains("|$packageName|")){
                    val channelId = XSPUtils.getString(packageName, "app_all") ?: return@replaceHookMethod -1
                    if (channelId == "app_all"){
                        return@replaceHookMethod 1
                    }else{

                        starLog.logD("getNotificationTypeForIm $applicationInfo | ${notification.channelId} compare $channelId")
                        //return@replaceHookMethod  if ("message_channel_new_id" == notification.channelId || notification.channelId.startsWith("message_channel_")) 1 else -1

                        return@replaceHookMethod  if (channelId == notification.channelId || notification.channelId.startsWith(channelId)) 1 else -1
                    }


                }
                return@replaceHookMethod -1
            }
            return@replaceHookMethod -1



        }



    }

}