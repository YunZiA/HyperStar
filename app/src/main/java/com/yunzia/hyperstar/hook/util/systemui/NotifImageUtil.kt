package com.yunzia.hyperstar.hook.util.systemui

import android.app.Notification
import android.content.Context
import android.graphics.drawable.Drawable
import de.robv.android.xposed.XposedHelpers

class NotifImageUtil(private val classLoader: ClassLoader?) {

    val notifImageUtil = XposedHelpers.findClass("com.android.systemui.statusbar.notification.utils.NotifImageUtil",classLoader)

    fun getCustomAppIcon(notification: Notification, context: Context) = XposedHelpers.callMethod(notifImageUtil,"getCustomAppIcon",notification,context) as Drawable

}