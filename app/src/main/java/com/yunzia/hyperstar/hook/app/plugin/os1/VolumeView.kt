package com.yunzia.hyperstar.hook.app.plugin.os1

import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.view.ViewGroup
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.base.getDimensionPixelSize
import com.yunzia.hyperstar.hook.base.replaceHookMethod
import com.yunzia.hyperstar.utils.XSPUtils
import yunzia.utils.DensityUtil.Companion.dpToPx


class VolumeView: Hooker() {

    val isPressExpandVolume = XSPUtils.getBoolean("is_press_expand_volume",false)

    val VolumeOffsetTopCollapsedP = XSPUtils.getFloat("volume_offset_top_collapsed_p",-1f)
    val VolumeOffsetTopCollapsedL = XSPUtils.getFloat("volume_offset_top_collapsed_l",-1f)

    val VolumeHeightCollapsedP = XSPUtils.getFloat("volume_height_collapsed_p",-1f)
    val VolumeHeightCollapsedL = XSPUtils.getFloat("volume_height_collapsed_l",-1f)

    val ShadowHeightNoFooterP = XSPUtils.getFloat("volume_shadow_height_collapsed_no_footer_p",-1f)
    val ShadowHeightNoFooterL = XSPUtils.getFloat("volume_shadow_height_collapsed_no_footer_l",-1f)

    val ShadowMarginTopP = XSPUtils.getFloat("volume_shadow_margin_top_collapsed_p",-1f)
    val ShadowMarginTopL = XSPUtils.getFloat("volume_shadow_margin_top_collapsed_l",-1f)

    val ShadowHeightP = XSPUtils.getFloat("volume_shadow_height_collapsed_p",-1f)
    val ShadowHeightL = XSPUtils.getFloat("volume_shadow_height_collapsed_l",-1f)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        startCollpasedColumn()
    }

    private fun startCollpasedColumn() {

        replaceDimen(
            "miui_volume_dialog_shadow_height_no_footer",plugin
        ) {
            if (mConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (ShadowHeightNoFooterP == -1f) return@replaceDimen null
                dpToPx(displayMetrics,ShadowHeightNoFooterP)
            } else {
                if (ShadowHeightNoFooterL == -1f) return@replaceDimen null
                dpToPx(displayMetrics,ShadowHeightNoFooterL)
            }
        }
        replaceDimen(
            "miui_volume_dialog_shadow_height",plugin
        ) {
            if (mConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (ShadowHeightP == -1f) return@replaceDimen null
                dpToPx(displayMetrics,ShadowHeightP)
            } else {
                if (ShadowHeightL == -1f) return@replaceDimen null
                dpToPx(displayMetrics,ShadowHeightL)
            }
        }

        replaceDimen(
            "miui_volume_offset_top_collapsed", plugin
        ){
            if (mConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (VolumeOffsetTopCollapsedP == -1f) return@replaceDimen null
                dpToPx(displayMetrics,VolumeOffsetTopCollapsedP)
            } else {
                if (VolumeOffsetTopCollapsedL == -1f) return@replaceDimen null
                dpToPx(displayMetrics,VolumeOffsetTopCollapsedL)
            }
        }


        replaceDimen(
            "miui_volume_dialog_shadow_margin_top", plugin
        ){
            if (mConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (ShadowMarginTopP == -1f) return@replaceDimen null
                dpToPx(displayMetrics,ShadowMarginTopP)
            } else {
                if (ShadowMarginTopL == -1f) return@replaceDimen null
                dpToPx(displayMetrics,ShadowMarginTopL)
            }
        }


        replaceDimen(
            "miui_volume_column_height",plugin
        ){
            if (mConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (VolumeHeightCollapsedP == -1f) return@replaceDimen null
                dpToPx(displayMetrics,VolumeHeightCollapsedP)
            } else {
                if (VolumeHeightCollapsedL == -1f) return@replaceDimen null
                dpToPx(displayMetrics,VolumeHeightCollapsedL)
            }
        }




    }

}