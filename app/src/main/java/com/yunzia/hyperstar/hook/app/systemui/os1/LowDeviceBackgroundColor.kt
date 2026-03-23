package com.yunzia.hyperstar.hook.app.systemui.os1

import androidx.core.graphics.toColorInt
import com.yunzia.hyperstar.hook.core.XposedCore
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.colorReplaceByValue
import com.yunzia.hyperstar.hook.core.base.BaseHook
import com.yunzia.hyperstar.prefs.XSPUtils


object LowDeviceBackgroundColor: BaseHook() {
    private val lowDeviceBackgroundColor by lazy {  XSPUtils.getString("low_device_qc_background_color","null")}

    override fun init() {
        if (lowDeviceBackgroundColor != null && lowDeviceBackgroundColor != "null"){
            colorReplaceByValue("notification_control_center_solid_background_color", XposedCore.hookedPackageName,lowDeviceBackgroundColor!!.toColorInt())
        }

    }


}