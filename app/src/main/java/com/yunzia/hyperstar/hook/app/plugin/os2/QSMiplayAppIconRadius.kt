package com.yunzia.hyperstar.hook.app.plugin.os2

import android.content.res.XModuleResources
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import yunzia.utils.DensityUtil.Companion.dpToPx

class QSMiplayAppIconRadius: Hooker() {

    private val qsDetailProgressBgRadius = XSPUtils.getBoolean("qs_detail_progress_bg_radius",false)
    private val disableAppIconRadius = XSPUtils.getBoolean("qs_detail_app_icon_radius",false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        if (disableAppIconRadius) {
            replaceDimen("miplay_detail_header_app_icon_radius",plugin) {
                return@replaceDimen 0f
            }
        }
        if (qsDetailProgressBgRadius){
            replaceDimen("miplay_seekbar_progress_bg_corner_radiu",plugin) {
                return@replaceDimen dpToPx(displayMetrics, 8f)
            }
        }
    }
}