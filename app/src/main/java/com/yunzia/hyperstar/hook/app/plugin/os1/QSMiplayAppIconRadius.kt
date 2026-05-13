package com.yunzia.hyperstar.hook.app.plugin.os1

import android.graphics.Color
import android.view.ViewGroup
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.dimenReplaceById
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.hookLayout
import com.yunzia.hyperstar.hook.core.helper.callMethod
import com.yunzia.hyperstar.prefs.XSPUtils
import com.yunzia.hyperstar.hook.util.android.findViewByIdName

object QSMiplayAppIconRadius: BasePluginHook() {

    private val disableAppIconRadius = XSPUtils.getBoolean("qs_detail_app_icon_radius",false)
    private val qsDetailProgressBgRadius = XSPUtils.getBoolean("qs_detail_progress_bg_radius",false)

    override fun init() {
        if (disableAppIconRadius) {
            hookLayout("qs_control_detail_header_metainfo_layout",plugin) {
                this as ViewGroup
                val icon = findViewByIdName("app_icon")
                val card = icon?.parent
                card.callMethod("setCardBackgroundColor",Color.TRANSPARENT)
                card.callMethod("setRadius",0f)
            }
            try {
                hookLayout("qs_control_detail_header_metainfo_layout_support_land", plugin){
                    this as ViewGroup
                    val icon = findViewByIdName("app_icon")
                    val card = icon?.parent
                    card.callMethod("setCardBackgroundColor",Color.TRANSPARENT)
                    card.callMethod("setRadius",0f)
                }
            }catch (e: Exception) {
                // layout may not exist on all devices
            }

        }

        if (qsDetailProgressBgRadius){
            dimenReplaceById(plugin, "miplay_seekbar_progress_bg_corner_radiu", R.dimen.miplay_seekbar_progress_bg_corner_radiu)
        }
    }
}