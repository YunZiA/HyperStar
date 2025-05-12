package com.yunzia.hyperstar.hook.app.systemui.os2

import androidx.core.graphics.toColorInt
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.utils.XSPUtils

class LowDeviceBackgroundColor: Hooker() {
    private val lowDeviceBackgroundColor by lazy {  XSPUtils.getString("low_device_qc_background_color","null")}

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        if (lowDeviceBackgroundColor != null && lowDeviceBackgroundColor != "null"){
            findClass(
                "com.android.systemui.statusbar.policy.BlurUtilsExt",
                classLoader
            ).apply {
                afterHookMethod(
                    "updateResources\$16"
                ) {
                    val backgroundColor = lowDeviceBackgroundColor!!.toColorInt()
                    val i3: Int = (backgroundColor shr 24) and 255
                    this.setIntField("solidColorAlpha",i3)
                    this.setIntField("solidColorRGB",backgroundColor and ((i3 shl 24).inv()))

                }
            }

        }

    }


}