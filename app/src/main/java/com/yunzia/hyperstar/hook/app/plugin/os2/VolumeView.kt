package com.yunzia.hyperstar.hook.app.plugin.os2

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.util.plugin.Util
import com.yunzia.hyperstar.hook.util.starLog
import com.yunzia.hyperstar.utils.XSPUtils
import yunzia.utils.DensityUtil.Companion.dpToPx


class VolumeView: Hooker() {

    val isPressExpandVolume = XSPUtils.getBoolean("is_press_expand_volume",false)

    val isHideStandardView = XSPUtils.getBoolean("is_hide_StandardView",false)

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
        startCollpasedColumnPress()
        startCollpasedFootButton()

    }

    private fun startCollpasedColumn() {

        findClass(
            "com.android.systemui.miui.ViewStateGroup\$Builder",
            classLoader
        ).afterHookMethod(
            "addStateWithIntDimen",
            Int::class.java,
            Int::class.java,
            Int::class.java
        ) {

            val i3 = it.args[2] as Int
            val mContext = this.getObjectFieldAs<Context>("mContext")
            val res = mContext.resources
            val miuiVolumeOffsetTopCollapsed = res.getIdentifier("miui_volume_offset_top_collapsed","dimen",plugin)
            val miuiVolumeDialogShadowMarginTop = res.getIdentifier("miui_volume_dialog_shadow_margin_top","dimen",plugin)

            when(i3) {
                miuiVolumeOffsetTopCollapsed -> {
                    val VolumeOffsetTop = if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        if (VolumeOffsetTopCollapsedP == -1f) return@afterHookMethod
                        VolumeOffsetTopCollapsedP
                    } else {
                        if (VolumeOffsetTopCollapsedL == -1f) return@afterHookMethod
                        VolumeOffsetTopCollapsedL
                    }
                    it.result = this.callMethod(
                        "addState",
                        it.args[0],
                        it.args[1],
                        dpToPx(res, VolumeOffsetTop).toInt()
                    )

                }

                miuiVolumeDialogShadowMarginTop -> {
                    val ShadowMarginTop = if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        if (ShadowMarginTopP == -1f) return@afterHookMethod
                        ShadowMarginTopP
                    } else {
                        if (ShadowMarginTopL == -1f) return@afterHookMethod
                        ShadowMarginTopL
                    }
                    it.result = this.callMethod(
                        "addState",
                        it.args[0],
                        it.args[1],
                        dpToPx(res, ShadowMarginTop).toInt()
                    )

                }
            }
        }

        findClass(
            "com.android.systemui.miui.volume.MiuiVolumeDialogMotion",
            classLoader
        ).afterHookMethod(
            "updateShadowState"
        ){
            val mContext = this.getObjectField("mContext") as Context
            val res = mContext.resources
            val mShadowView = this.getObjectFieldAs<View>("mShadowView")
            val mVolumeView =  this.getObjectFieldAs<View>("mVolumeView")
            val mSuperVolume = this.getObjectFieldAs<View>("mSuperVolume")
            val mRingerModeLayout = this.getObjectFieldAs<View>("mRingerModeLayout")
            val layoutParams = mShadowView.layoutParams as ViewGroup.MarginLayoutParams
            val shadowHeight:Float

            if (!(this.callMethodAs<Boolean>("isLandscape")!!)){
                if (ShadowMarginTopP == -1f) return@afterHookMethod
                var top= dpToPx(res,ShadowMarginTopP).toInt()
                if (mSuperVolume.visibility == View.VISIBLE) {
                    top -= getDimensionPixelSize(res,"miui_shadow_super_volume_height",plugin)
                }
                layoutParams.topMargin = top
                mShadowView.layoutParams = layoutParams

            }
            if (mRingerModeLayout.visibility == View.VISIBLE){

                if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                    if (ShadowHeightP == -1f) return@afterHookMethod
                    shadowHeight = ShadowHeightP
                }else{
                    if (ShadowHeightL == -1f) return@afterHookMethod
                    shadowHeight = ShadowHeightL

                }
            }else return@afterHookMethod

            mShadowView.layoutParams.height = dpToPx(res,shadowHeight).toInt()

        }

        findClass(
            "com.android.systemui.miui.volume.DndPopupWindow",
            classLoader
        ).replaceHookMethod(
            "getPositionY",
            Int::class.java
        ){
            val i = it.args[0] as Int
            val mContext = this.getObjectFieldAs<Context>("mContext")
            val res = mContext.resources

            var miuiVolumeOffsetTopCollapsed = getDimensionPixelSize(res,"miui_volume_offset_top_collapsed",plugin)

            if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                if (VolumeHeightCollapsedP != -1f) {
                    miuiVolumeOffsetTopCollapsed = dpToPx(res,VolumeHeightCollapsedP).toInt()
                }
            } else {
                if (VolumeHeightCollapsedL != -1f){
                    miuiVolumeOffsetTopCollapsed= dpToPx(res,VolumeHeightCollapsedL).toInt()
                }
            }
            val miuiVolumeFooterMarginTop = getDimensionPixelSize(res,"miui_volume_footer_margin_top",plugin)
            val miuiVolumeSilenceButtonHeight = getDimensionPixelSize(res,"miui_volume_silence_button_height",plugin)
            var miuiVolumeColumnHeight = getDimensionPixelSize(res,"miui_volume_column_height",plugin)
            if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                if (VolumeHeightCollapsedP == -1f){
                    miuiVolumeColumnHeight = dpToPx(res,VolumeHeightCollapsedP).toInt()
                }
            } else {
                if (VolumeHeightCollapsedL != -1f){
                    miuiVolumeColumnHeight =  dpToPx(res,VolumeHeightCollapsedL).toInt()
                }
            }

            val result = (((miuiVolumeOffsetTopCollapsed + miuiVolumeColumnHeight)
                    + (miuiVolumeSilenceButtonHeight  * 1.5))
                    + (miuiVolumeFooterMarginTop * 2)) - (i * 0.8f)
            return@replaceHookMethod result.toInt()

        }

        findClass(
            "com.android.systemui.miui.volume.VolumePanelViewController",
            classLoader
        ).afterHookMethod(
            "updateColumnsSizeH",
            View::class.java
        ){
            val view = it.args[0] as View
            val mContext = this.getObjectFieldAs<Context>("mContext")
            val res = mContext.resources
            if (!this.getBooleanField("mExpanded")) {
                val marginLayoutParams = view.layoutParams
                //marginLayoutParams.width = dpToPx(res,80f).toInt()
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
            val res = mContext.resources
            val mExpanded =  it.args[0] as Boolean
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
                marginLayoutParams.height = if (height % 2 == 1) {
                    ++height
                } else {
                    height
                }
                this.setObjectField( "mHeight", height)
                this.layoutParams = marginLayoutParams
            }

        }

        findClass(
            "com.android.systemui.miui.volume.RoundRectFrameLayout",
            classLoader
        ).afterHookMethod(
            "updateProgressViewSize",
            Boolean::class.java,
            Boolean::class.java
        ) { this as View
            val mContext = this.getObjectField("mContext") as Context
            val res = mContext.resources
            val mExpanded =  it.args[0] as Boolean
            if (!mExpanded) {
                val marginLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
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

    private fun startCollpasedColumnPress() {

        if (!isPressExpandVolume) return

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

        var longClick = false
        findClass(
            "com.android.systemui.miui.volume.MiuiVolumeDialogMotion", classLoader
        ).apply {
            afterHookAllConstructors {
                this.getObjectFieldAs<View>("mExpandButton").setOnTouchListener(null)
                val mVolumeView = this.getObjectFieldAs<View>("mVolumeView")


            }
            replaceHookMethod(
                "lambda\$processExpandTouch\$1"
            ) {
                if ( getBooleanField("mExpanded") || !longClick  ) return@replaceHookMethod null
                val mVolumeView = getObjectFieldAs<View>("mVolumeView")

                starLog.logD("processExpandTouch")
//                val mVolumeExpandCollapsedAnimator = getObjectField("mVolumeExpandCollapsedAnimator")
//                val mCallback = getObjectField("mCallback")
//                mVolumeExpandCollapsedAnimator.callMethod("calculateFromViewValues",true)
//                mCallback.callMethod("onExpandClicked")
                with(AnimatorSet()) {
                    playTogether(
                        ObjectAnimator.ofFloat(mVolumeView, "scaleX", 0.95f),
                        ObjectAnimator.ofFloat(mVolumeView, "scaleY", 0.95f)
                    )
                    duration = 100L
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            mVolumeView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                            val mVolumeExpandCollapsedAnimator = this@replaceHookMethod.getObjectField("mVolumeExpandCollapsedAnimator")
                            val mCallback = this@replaceHookMethod.getObjectField("mCallback")
                            mVolumeExpandCollapsedAnimator.callMethod("calculateFromViewValues",true)
                            mCallback.callMethod("onExpandClicked")

                            mVolumeView.scaleX = 1f
                            mVolumeView.scaleY = 1f
                        }
                    })
                    start()
                }

            }

        }

        findClass(
            "com.android.systemui.miui.volume.MiuiVolumeSeekBar",
            classLoader
        ).afterHookMethod(
            "onTouchEvent",
            MotionEvent::class.java
        ) {
            val mSeekBarOnclickListener = this.getObjectField("mSeekBarOnclickListener")

            val handler = Handler(Looper.getMainLooper())
            val mLongPressRunnable = Runnable {
                val mMoveY = this.getFloatField("mMoveY")
                if (longClick){
                    this as View
                    mSeekBarOnclickListener.callMethod( "onClick")
                }
            }

            if (mSeekBarOnclickListener != null) {
                val motionEvent = it.args?.get(0) as MotionEvent

                val action = motionEvent.action
                when (action) {
                    0 -> {

                        longClick = true
                        this.setLongField("mCurrentMS",0L)

                        handler.postDelayed(mLongPressRunnable, 300L)

                    }
                    1-> {
                        longClick = false
                        this.setLongField("mCurrentMS",0L)
                    }

                    2 -> {
                        longClick = false


                    }
                }

            }
        }


        val VolumeVerticalSeekBar = findClass("com.android.systemui.miui.volume.widget.VolumeVerticalSeekBar",classLoader)
        val util = Util(classLoader)
        val VerticalSeekBar = findClass("miui.systemui.widget.VerticalSeekBar",classLoader)
//        findClass(
//            "com.android.systemui.miui.volume.MiuiVolumeSeekBar",
//            classLoader
//        ).apply {
//            replaceHookMethod(
//                "onTouchEvent",
//                MotionEvent::class.java
//            ) {
//                val motionEvent = it.args[0] as MotionEvent
//                this.getObjectField("mInjector").callMethod("transformTouchEvent",motionEvent)
//                it.callSuperMethod() as Boolean
//            }
//            afterHookMethod(
//                "dispatchTouchEvent",
//                MotionEvent::class.java
//            ) {
//                this as View
//                starLog.log("mVolumeView touch")
//                val view = this.parent as View
//
//                val mLongPressRunnable = Runnable {
//                    val currentTimeMillis: Long =
//                        System.currentTimeMillis() - this.getLongField("mCurrentMS")
//                    val mMoveY = this.getFloatField("mMoveY")
//                    if (currentTimeMillis > 300 && mMoveY < 20.0f) {
//
//                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
//                        this.getObjectField("mAnimListener").callMethod("setScale")
//                        //this.callMethod("lambda\$processExpandTouch\$1")
//                        view.apply {
//                            scaleX = 1f
//                            scaleY = 1f
//                        }
//                    }
//                }
//
//                val motionEvent = it.args[0] as MotionEvent
//
//                val action = motionEvent.action
//
//
//                when (action) {
//                    0 -> {
//
//                        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
//                            view,
//                            PropertyValuesHolder.ofFloat("scaleX", 0.9f),
//                            PropertyValuesHolder.ofFloat("scaleY", 0.9f)
//                        ).apply {
//                            duration = 120
//                        }
//                        scaleDown.start()
//                        this.setFloatField("mDownY", motionEvent.y)
//                        this.setFloatField("mMoveY", 0.0f)
//                        this.setLongField("mCurrentMS", System.currentTimeMillis())
//                        view.postDelayed(mLongPressRunnable, 300L)
//
//                    }
//
//                    1 -> {
//                        view.removeCallbacks(mLongPressRunnable)
//                        val currentTimeMillis: Long =
//                            System.currentTimeMillis() - this.getLongField("mCurrentMS")
//                        val mMoveY = this.getFloatField("mMoveY")
//                        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
//                            view,
//                            PropertyValuesHolder.ofFloat("scaleX", 1.0f),
//                            PropertyValuesHolder.ofFloat("scaleY", 1.0f)
//                        ).apply {
//                            duration = 120
//                        }
//                        scaleUp.start()
//                    }
//
//                    2 -> {
//                        view.removeCallbacks(mLongPressRunnable)
//                        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
//                            view,
//                            PropertyValuesHolder.ofFloat("scaleX", 1.0f),
//                            PropertyValuesHolder.ofFloat("scaleY", 1.0f)
//                        ).apply {
//                            duration = 120
//                        }
//                        scaleUp.start()
//                        val mDownY = this.getFloatField("mDownY")
//
//                        this.setFloatField("mMoveY", abs(motionEvent.y - mDownY))
//                        this.setFloatField("mDownY", motionEvent.y)
//
//                    }
//
//                    else -> {
//
//                    }
//                }
//
//            }
//        }

    }

    private fun startCollpasedFootButton() {

        if (!isHideStandardView) return

        findClass(
            "com.android.systemui.miui.volume.MiuiRingerModeLayout\$RingerButtonHelper",
            classLoader
        ).apply {
            afterHookMethod(
                "updateState"
            ) {
                val mIcon = this.getObjectFieldAs<View>("mIcon")
                val mStandardView = this.getObjectFieldAs<View>("mStandardView")
                if (this.getBooleanField("mExpanded")) {
                    mIcon.visibility = View.VISIBLE
                    mStandardView.visibility = View.VISIBLE
                } else {
                    mIcon.visibility = View.GONE
                    mStandardView.visibility = View.GONE
                }
            }
            afterHookMethod(
                "onExpanded",
                Boolean::class.java,
                Boolean::class.java
            ){

                val z1 = it.args[0] as Boolean
                val z2 = it.args[1] as Boolean
                val mStandardView = this.getObjectFieldAs<View>("mStandardView")

                mStandardView.visibility = if (this.getBooleanField("mExpanded") != z1 || z2){
                    View.GONE
                }else{
                    View.VISIBLE
                }
            }
        }


        findClass("com.android.systemui.miui.volume.TimerItem",
            classLoader
        ).afterHookMethod(
            "updateExpanded",
            Boolean::class.java
        ){

            this.getObjectFieldAs<View>("mCountDownProgressBar").visibility = View.GONE

        }

    }



}