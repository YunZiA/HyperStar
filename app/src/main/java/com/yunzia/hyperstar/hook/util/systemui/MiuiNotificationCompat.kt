package com.yunzia.hyperstar.hook.util.systemui

import android.app.Notification
import de.robv.android.xposed.XposedHelpers

class MiuiNotificationCompat(private val classLoader: ClassLoader?) {
    private val miuiNotificationCompat = XposedHelpers.findClass("com.miui.systemui.notification.MiuiNotificationCompat",classLoader)

    fun getTargetPkg(notification: Notification) = XposedHelpers.callStaticMethod(miuiNotificationCompat,"getTargetPkg",notification) as? CharSequence


}