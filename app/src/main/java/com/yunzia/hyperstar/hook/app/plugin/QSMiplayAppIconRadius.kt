package com.yunzia.hyperstar.hook.app.plugin

import android.content.res.XModuleResources
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.callbacks.XC_InitPackageResources

class QSMiplayAppIconRadius: BaseHooker() {

    private val qsDetailProgressBgRadius = XSPUtils.getBoolean("qs_detail_progress_bg_radius",false)
    private val disableAppIconRadius = XSPUtils.getBoolean("qs_detail_app_icon_radius",false)

    override fun doResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
        modRes: XModuleResources?
    ) {

        if (disableAppIconRadius) {

            resparam?.res?.setReplacement(plugin,"dimen","miplay_detail_header_app_icon_radius",modRes?.fwd(R.dimen.no))
        }

        if (qsDetailProgressBgRadius){

            resparam?.res?.setReplacement(plugin,"dimen","miplay_seekbar_progress_bg_corner_radiu", modRes?.fwd(R.dimen.miplay_seekbar_progress_bg_corner_radiu))
        }
    }
}