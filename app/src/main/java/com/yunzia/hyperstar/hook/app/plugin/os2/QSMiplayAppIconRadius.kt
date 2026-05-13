package com.yunzia.hyperstar.hook.app.plugin.os2

import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.dimenReplaceByValue
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.prefs.XSPUtils
import yunzia.utils.DensityUtil.Companion.dpToPx

object QSMiplayAppIconRadius: BasePluginHook() {

    private val qsDetailProgressBgRadius = XSPUtils.getBoolean("qs_detail_progress_bg_radius",false)
    private val disableAppIconRadius = XSPUtils.getBoolean("qs_detail_app_icon_radius",false)

    override fun init() {

        if (disableAppIconRadius) {
            dimenReplaceByValue("miplay_detail_header_app_icon_radius",plugin) {
                return@dimenReplaceByValue 0f
            }
        }
        if (qsDetailProgressBgRadius){
            dimenReplaceByValue("miplay_seekbar_progress_bg_corner_radiu",plugin) {
                return@dimenReplaceByValue dpToPx(displayMetrics, 8f)
            }
        }
    }
}