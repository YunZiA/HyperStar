package com.yunzia.hyperstar.hook.app.plugin

import android.content.res.XModuleResources
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.callbacks.XC_InitPackageResources

class QSMediaNoPlayTitle : BaseHooker() {

    val mHeaderTitle = XSPUtils.getString("miplay_detail_header_no_song","null")


    override fun doResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
        modRes: XModuleResources?
    ) {
        super.doResources(resparam, modRes)
        if (mHeaderTitle == "null") return

        resparam?.res?.setReplacement(plugin,"string","miplay_detail_header_no_song",mHeaderTitle)


    }
}