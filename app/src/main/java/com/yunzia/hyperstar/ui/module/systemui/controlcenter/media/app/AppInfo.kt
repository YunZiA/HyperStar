package com.yunzia.hyperstar.ui.module.systemui.controlcenter.media.app

import android.content.Intent
import android.graphics.drawable.Drawable

class AppInfo(
    var uid: Int = 0,
    var label: String = "", //应用名称
    var packageName: String = "", //应用包名
    var icon: Drawable? = null, //应用icon
    var launch : Intent? = null
)