package com.yunzia.hyperstar.hook.app.plugin

import android.content.res.Configuration
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.utils.XSPUtils
import yunzia.utils.DensityUtil

class VolumeBarLayoutParams: Hooker() {

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
                DensityUtil.Companion.dpToPx(displayMetrics, ShadowHeightNoFooterP)
            } else {
                if (ShadowHeightNoFooterL == -1f) return@replaceDimen null
                DensityUtil.Companion.dpToPx(displayMetrics, ShadowHeightNoFooterL)
            }
        }
        replaceDimen(
            "miui_volume_dialog_shadow_height",plugin
        ) {
            if (mConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (ShadowHeightP == -1f) return@replaceDimen null
                DensityUtil.Companion.dpToPx(displayMetrics, ShadowHeightP)
            } else {
                if (ShadowHeightL == -1f) return@replaceDimen null
                DensityUtil.Companion.dpToPx(displayMetrics, ShadowHeightL)
            }
        }

        replaceDimen(
            "miui_volume_offset_top_collapsed", plugin
        ){
            if (mConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (VolumeOffsetTopCollapsedP == -1f) return@replaceDimen null
                DensityUtil.Companion.dpToPx(displayMetrics, VolumeOffsetTopCollapsedP)
            } else {
                if (VolumeOffsetTopCollapsedL == -1f) return@replaceDimen null
                DensityUtil.Companion.dpToPx(displayMetrics, VolumeOffsetTopCollapsedL)
            }
        }


        replaceDimen(
            "miui_volume_dialog_shadow_margin_top", plugin
        ){
            if (mConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (ShadowMarginTopP == -1f) return@replaceDimen null
                DensityUtil.Companion.dpToPx(displayMetrics, ShadowMarginTopP)
            } else {
                if (ShadowMarginTopL == -1f) return@replaceDimen null
                DensityUtil.Companion.dpToPx(displayMetrics, ShadowMarginTopL)
            }
        }


        replaceDimen(
            "miui_volume_column_height",plugin
        ){
            if (mConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (VolumeHeightCollapsedP == -1f) return@replaceDimen null
                DensityUtil.Companion.dpToPx(displayMetrics, VolumeHeightCollapsedP)
            } else {
                if (VolumeHeightCollapsedL == -1f) return@replaceDimen null
                DensityUtil.Companion.dpToPx(displayMetrics, VolumeHeightCollapsedL)
            }
        }

    }

}