package com.yunzia.hyperstar.hook.app.systemui.os1

import androidx.core.graphics.toColorInt
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.tool.ColorResourceReplacer
import com.yunzia.hyperstar.utils.XSPUtils


class LowDeviceBackgroundColor: Hooker() {
    private val lowDeviceBackgroundColor by lazy {  XSPUtils.getString("low_device_qc_background_color","null")}

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        if (lowDeviceBackgroundColor != null && lowDeviceBackgroundColor != "null"){

            ColorResourceReplacer.registerColorReplacement { resourceId ->

                // 如果资源 ID 是指定的颜色资源，则返回替换的颜色值
                val mResourcesImpl = this.getObjectField("mResourcesImpl")
                val id = mResourcesImpl.callMethodAs<Int>("getIdentifier","notification_control_center_solid_background_color","color","com.android.systemui")
                if (resourceId == id) {
                    // 返回替换的颜色值（以 16 进制表示）
                    lowDeviceBackgroundColor!!.toColorInt() // 替换为红色
                } else {
                    null // 不替换
                }
            }

        }

    }


}