package com.yunzia.hyperstar.hook.util.systemui

import android.app.Notification
import android.content.Context
import android.graphics.drawable.Drawable
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.callMethod

class NotifImageUtil(private val classLoader: ClassLoader?) {

    val notifImageUtil = findClass("com.android.systemui.statusbar.notification.utils.NotifImageUtil",classLoader)

    fun getCustomAppIcon(notification: Notification, context: Context) = notifImageUtil.callMethod("getCustomAppIcon",notification,context) as Drawable

}