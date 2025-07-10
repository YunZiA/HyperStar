package com.yunzia.hyperstar.hook.app.plugin.os2

import android.content.Context
import android.content.res.Configuration
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.base.getDimensionPixelSize
import com.yunzia.hyperstar.hook.base.replaceHookMethod
import com.yunzia.hyperstar.hook.util.plugin.Util
import com.yunzia.hyperstar.utils.XSPUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    val ShadowHeightNoFooterP = XSPUtils.getFloat("volume_shadow_height_collapsed_no_footer_p",-1f)
    val ShadowHeightNoFooterL = XSPUtils.getFloat("volume_shadow_height_collapsed_no_footer_l",-1f)

    val ShadowHeightP = XSPUtils.getFloat("volume_shadow_height_collapsed_p",-1f)
    val ShadowHeightL = XSPUtils.getFloat("volume_shadow_height_collapsed_l",-1f)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        startCollpasedColumn()
        startCollpasedColumnPress()

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

    private fun startCollpasedColumnPress() {

        if (!isPressExpandVolume) return

        var longClick = false
        var longPressJob: Job? = null

        fun View.startScaleAnimation() {
            longClick = true
            this.animate()
                .scaleX(0.92f)
                .scaleY(0.92f)
                .setDuration(300)
                .start()
        }

        fun View.stopScaleAnimation() {
            // 还原到原始大小
            longClick = false
            this.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(300)
                .start()
        }

        findClass(
            "com.android.systemui.miui.volume.MiuiVolumeDialogView",
            classLoader
        ).apply {
            afterHookMethod(
                "onFinishInflate"
            ) {
                this.getObjectFieldAs<View>("mExpandButton").apply {
                    setOnClickListener(null)
                    alpha = 0f
                    isClickable = false
                    visibility = View.GONE
                }

            }
            afterHookMethod(
                "notifyAccessibilityChanged",
                Boolean::class.java
            ) {
                this.getObjectFieldAs<View>("mExpandButton").apply {
                    setOnClickListener(null)
                    isClickable = false
                    visibility = View.GONE
                }

            }
        }

        findClass(
            "com.android.systemui.miui.volume.MiuiVolumeDialogMotion", classLoader
        ).apply {
            beforeHookMethod("lambda\$processExpandTouch\$1") {
                this.setObjectField("mIsExpandButton",true)
            }

        }

        findClass(
            "com.android.systemui.miui.volume.MiuiVolumeSeekBar",
            classLoader
        ).apply {
            afterHookMethod(
                "onTouchEvent",
                MotionEvent::class.java
            ) {
                val mSeekBarOnclickListener = this.getObjectField("mSeekBarOnclickListener")
                val mSeekBarAnimListener = this.getObjectField("mSeekBarAnimListener")
                val volumePanelViewController = mSeekBarAnimListener.getObjectField("this\$0")
                val mVolumeView = volumePanelViewController.getObjectFieldAs<View>("mVolumeView")


                this.setLongField("mCurrentMS",0L)
                if (mSeekBarOnclickListener != null) {
                    val motionEvent = it.args?.get(0) as MotionEvent


                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {

                            if ( !volumePanelViewController.getBooleanField("mExpanded") ){

                                // 启动长按检测协程
                                longPressJob = CoroutineScope(Dispatchers.Main).launch {
                                    mVolumeView.startScaleAnimation() // 执行缩放动画
                                    delay(300)
                                    val mMoveY = this@afterHookMethod.getFloatField("mMoveY")

                                    if (longClick && mMoveY < 10f){
                                        mVolumeView.apply {
                                            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                            scaleY = 1f
                                            scaleX = 1f
                                        }

                                        mSeekBarOnclickListener.callMethod( "onClick")

                                    }

                                }
                            }
                        }
                        MotionEvent.ACTION_UP -> {
                            mVolumeView.stopScaleAnimation()
                            longPressJob?.cancel()

                        }
                        MotionEvent.ACTION_CANCEL -> {
                            mVolumeView.stopScaleAnimation()
                            longPressJob?.cancel()
                        }
                    }

                }
            }
        }


    }



}