package com.yunzia.hyperstar.hook.app.systemui.os2

import androidx.core.graphics.toColorInt
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.utils.XSPUtils


class LowDeviceBackgroundColor: Hooker() {
    private val lowDeviceBackgroundColor by lazy {  XSPUtils.getString("low_device_qc_background_color","null")}

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        if (lowDeviceBackgroundColor != null && lowDeviceBackgroundColor != "null"){
            replaceColor("shade_solid_background_color",systemUI,lowDeviceBackgroundColor!!.toColorInt())
        }

    }


}