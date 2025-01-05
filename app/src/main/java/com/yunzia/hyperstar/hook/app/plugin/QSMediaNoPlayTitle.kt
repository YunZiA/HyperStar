package com.yunzia.hyperstar.hook.app.plugin

import android.content.res.XModuleResources
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.callbacks.XC_InitPackageResources

class QSMediaNoPlayTitle : Hooker() {

    val mHeaderTitle = XSPUtils.getString("miplay_detail_header_no_song","null")


    override fun initResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
        modRes: XModuleResources?
    ) {
        super.initResources(resparam, modRes)
        if (mHeaderTitle == "null") return

        resparam?.res?.setReplacement(plugin,"string","miplay_detail_header_no_song",mHeaderTitle)


    }
}