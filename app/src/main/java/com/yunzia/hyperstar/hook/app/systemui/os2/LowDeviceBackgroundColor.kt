package com.yunzia.hyperstar.hook.app.systemui.os2

import androidx.core.graphics.toColorInt
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.colorReplaceByValue
import com.yunzia.hyperstar.hook.core.BaseHook
import com.yunzia.hyperstar.prefs.XSPUtils
import io.github.kyuubiran.ezxhelper.xposed.EzXposed


object LowDeviceBackgroundColor: BaseHook() {
    private val lowDeviceBackgroundColor by lazy {  XSPUtils.getString("low_device_qc_background_color","null")}
    private val lowDeviceSecondBackgroundColor by lazy {  XSPUtils.getString("low_device_not_second_background_color","null")}

    override fun init() {

        if (lowDeviceBackgroundColor != null && lowDeviceBackgroundColor != "null"){
            colorReplaceByValue("shade_solid_background_color", EzXposed.hookedPackageName,lowDeviceBackgroundColor!!.toColorInt())
            colorReplaceByValue("notification_control_center_solid_background_color", EzXposed.hookedPackageName,lowDeviceSecondBackgroundColor!!.toColorInt())

        }

    }


}