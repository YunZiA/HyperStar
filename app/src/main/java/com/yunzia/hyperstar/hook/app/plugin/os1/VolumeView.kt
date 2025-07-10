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

//    val volumeWidthCollapsedP = XSPUtils.getFloat("volume_width_collapsed_p",-1f)
//    val volumeWidthCollapsedL = XSPUtils.getFloat("volume_width_collapsed_l",-1f)

    val ShadowMarginTopP = XSPUtils.getFloat("volume_shadow_margin_top_collapsed_p",-1f)
    val ShadowMarginTopL = XSPUtils.getFloat("volume_shadow_margin_top_collapsed_l",-1f)

    val ShadowHeightP = XSPUtils.getFloat("volume_shadow_height_collapsed_p",-1f)
    val ShadowHeightL = XSPUtils.getFloat("volume_shadow_height_collapsed_l",-1f)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        startCollpasedColumn()
    }

    private fun startCollpasedColumn() {

        if (VolumeOffsetTopCollapsedP != -1f || VolumeOffsetTopCollapsedL != -1f) {

            findClass(
                "com.android.systemui.miui.ViewStateGroup\$Builder",
                classLoader
            ).afterHookMethod(
                "addStateWithIntDimen",
                Int::class.java,
                Int::class.java,
                Int::class.java
            ){
                val mContext = this.getObjectFieldAs<Context>("mContext")
                val res = mContext.resources
                val i3 = it.args[2] as Int
                val miuiVolumeOffsetTopCollapsed = res.getIdentifier("miui_volume_offset_top_collapsed","dimen",plugin)

                when(i3){
                    miuiVolumeOffsetTopCollapsed->{
                        val VolumeOffsetTop:Float
                        if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                            if (VolumeOffsetTopCollapsedP == -1f) return@afterHookMethod
                            VolumeOffsetTop = VolumeOffsetTopCollapsedP
                        }else{
                            if (VolumeOffsetTopCollapsedL == -1f) return@afterHookMethod
                            VolumeOffsetTop = VolumeOffsetTopCollapsedL
                        }
                        it.result = this.callMethod(
                            "addState",
                            it.args[0],
                            it.args[1],
                            dpToPx(res,VolumeOffsetTop).toInt()
                        )

                    }

                }

            }

        }



        if (ShadowMarginTopP != -1f || ShadowMarginTopL != -1f){

            findClass(
                "com.android.systemui.miui.volume.MiuiVolumeDialogMotion\$5",
                classLoader
            ).afterHookMethod(
                "onGlobalLayout"
            ){
                val this0 = this.getObjectField("this$0")
                val mContext = this0.getObjectFieldAs<Context>("mContext")
                val res = mContext.resources
                val mShadowView = this0.getObjectFieldAs<View>("mShadowView")

                if (this0.callMethodAs<Boolean>("isLandscape")!!){
//                        val mDialogView = XposedHelpers.getObjectField(this0,"mDialogView") as View
//                        var top = mDialogView.top - res.getDimensionPixelSize(res.getIdentifier("miui_volume_dialog_shadow_top_offset","dimen",plugin))
////                        if (ShadowMarginTopP == -1f) return
////                        top = mDialogView.top - dpToPx(res,ShadowMarginTopP).toInt()
//                        XposedHelpers.callMethod(this0,"setTopMargin",mShadowView,top)

                }else{
                    val ShadowMarginTop:Int
                    if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                        if (ShadowMarginTopP == -1f) return@afterHookMethod
                        ShadowMarginTop = dpToPx(res,ShadowMarginTopP).toInt()
                    }else{
                        if (ShadowMarginTopL == -1f) return@afterHookMethod
                        ShadowMarginTop = dpToPx(res,ShadowMarginTopL).toInt()
                    }

                    this0.callMethod("setTopMargin",mShadowView,ShadowMarginTop)

                }
            }
        }
        if (ShadowHeightP != -1f || ShadowHeightL != -1f ){

            findClass(
                "com.android.systemui.miui.volume.MiuiVolumeDialogMotion",
                classLoader
            ).afterHookMethod(
                "createShowAnimator"
            ){
                val mContext = this.getObjectFieldAs<Context>("mContext")
                val res = mContext.resources
                val mShadowView = this.getObjectFieldAs<View>("mShadowView")
                val mRingerModeLayout = this.getObjectFieldAs<View>("mRingerModeLayout")
                val shadowHeight:Float

                if (mRingerModeLayout.visibility == View.VISIBLE){

                    if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                        if (ShadowHeightP == -1f) return@afterHookMethod
                        shadowHeight = ShadowHeightP
                    }else{
                        if (ShadowHeightL == -1f) return@afterHookMethod
                        shadowHeight = ShadowHeightL

                    }
                    mShadowView.layoutParams.height = dpToPx(res,shadowHeight).toInt()
                }

            }

        }

        if (VolumeOffsetTopCollapsedP != -1f || VolumeOffsetTopCollapsedL != -1f || VolumeHeightCollapsedP != -1f || VolumeHeightCollapsedL != -1f){


            findClass(
                "com.android.systemui.miui.volume.DndPopupWindow",
                classLoader
            ).replaceHookMethod(
                "getPositionY",
                Int::class.java
            ){
                val i = it.args[0] as Int
                val mContext = this.getObjectField("mContext") as Context
                val res = mContext.theme.resources
                var miuiVolumeOffsetTopCollapsed = getDimensionPixelSize(res,"miui_volume_offset_top_collapsed",plugin)
                if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                    if (VolumeOffsetTopCollapsedP != -1f) {
                        miuiVolumeOffsetTopCollapsed = dpToPx(res,VolumeOffsetTopCollapsedP).toInt()
                    }
                } else {
                    if (VolumeOffsetTopCollapsedL != -1f){
                        miuiVolumeOffsetTopCollapsed = dpToPx(res,VolumeOffsetTopCollapsedL).toInt()
                    }
                }

                val miuiVolumeFooterMarginTop = getDimensionPixelSize(res,"miui_volume_footer_margin_top",plugin)
                val miuiVolumeSilenceButtonHeight = getDimensionPixelSize(res,"miui_volume_silence_button_height",plugin)
                var miuiVolumeColumnHeight = getDimensionPixelSize(res,"miui_volume_column_height",plugin)
                if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                    if (VolumeHeightCollapsedP != -1f){
                        miuiVolumeColumnHeight = dpToPx(res,VolumeHeightCollapsedP).toInt()
                    }
                } else {
                    if (VolumeHeightCollapsedL != -1f){
                        miuiVolumeColumnHeight =  dpToPx(res,VolumeHeightCollapsedL).toInt()
                    }
                }

                val result = (((miuiVolumeOffsetTopCollapsed + miuiVolumeColumnHeight)
                        + (miuiVolumeSilenceButtonHeight * 1.5))
                        + (miuiVolumeFooterMarginTop * 2)) - (i * 0.8f)
                return@replaceHookMethod result.toInt()

            }
        }

        findClass(
            "com.android.systemui.miui.volume.MiuiVolumeDialogImpl",
            classLoader
        ).afterHookMethod(
            "updateColumnsSizeH",
            View::class.java
        ) {
            val view = it.args[0] as View
            val mContext = this.getObjectFieldAs<Context>("mContext")
            val res = mContext.resources
            val mExpanded =  this.getBooleanField("mExpanded")
            if (!mExpanded) {
                val marginLayoutParams = view.layoutParams
                val height :Float
                if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                    if (VolumeHeightCollapsedP == -1f) return@afterHookMethod
                    height = VolumeHeightCollapsedP
                } else {
                    if (VolumeHeightCollapsedL == -1f) return@afterHookMethod
                    height= VolumeHeightCollapsedL
                }
                marginLayoutParams.height = dpToPx(res,height).toInt()
                view.layoutParams = marginLayoutParams
            }

        }

        findClass(
            "com.android.systemui.miui.volume.MiuiVolumeSeekBarProgressView",
            classLoader
        ).afterHookMethod(
            "updateProgressViewSize",
            Boolean::class.java,
            Boolean::class.java
        ) { this as View
            val mContext = this.getObjectField("mContext") as Context
            val res = mContext.theme.resources
            val mExpanded = it.args[0] as Boolean
            if (!mExpanded) {
                val marginLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
                //marginLayoutParams.width = dpToPx(res,80f).toInt()
                var height: Int
                if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    if (VolumeHeightCollapsedP == -1f) return@afterHookMethod
                    height = dpToPx(res, VolumeHeightCollapsedP).toInt()
                } else {
                    if (VolumeHeightCollapsedL == -1f) return@afterHookMethod
                    height = dpToPx(res, VolumeHeightCollapsedL).toInt()
                }
                if (height % 2 == 1) {
                    marginLayoutParams.height = ++height
                } else {
                    marginLayoutParams.height = height

                }
                this.layoutParams = marginLayoutParams
                this.setObjectField("mHeight", height)
            }
        }

        findClass(
            "com.android.systemui.miui.volume.RoundRectFrameLayout",
            classLoader
        ).apply {
            afterHookMethod(
                "updateProgressViewSize",
                Boolean::class.java,
                Boolean::class.java
            ) { this as View
                val mContext = this.getObjectFieldAs<Context>("mContext")
                val res = mContext.resources
                val mExpanded =  it.args[0] as Boolean
                if (!mExpanded) {
                    val marginLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
                    //marginLayoutParams.width = dpToPx(res,80f).toInt()
                    var height :Int
                    if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                        if (VolumeHeightCollapsedP == -1f) return@afterHookMethod
                        height = dpToPx(res,VolumeHeightCollapsedP).toInt()
                    } else {
                        if (VolumeHeightCollapsedL == -1f) return@afterHookMethod
                        height= dpToPx(res,VolumeHeightCollapsedL).toInt()
                    }
                    if (height %2 == 1){
                        height++
                        marginLayoutParams.height = height
                    }else{
                        marginLayoutParams.height = height

                    }
                    this.layoutParams = marginLayoutParams
                }

            }
        }

    }

}