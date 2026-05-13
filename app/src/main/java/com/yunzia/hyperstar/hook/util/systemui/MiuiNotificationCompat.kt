package com.yunzia.hyperstar.hook.util.systemui

import android.app.Notification
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.callStaticMethod

class MiuiNotificationCompat(private val classLoader: ClassLoader?) {
    private val miuiNotificationCompat = findClass("com.miui.systemui.notification.MiuiNotificationCompat",classLoader)

    fun getTargetPkg(notification: Notification) = miuiNotificationCompat.callStaticMethod("getTargetPkg",notification) as? CharSequence


}