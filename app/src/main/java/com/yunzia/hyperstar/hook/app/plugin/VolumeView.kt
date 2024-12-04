package com.yunzia.hyperstar.hook.app.plugin

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
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import yunzia.utils.DensityUtil.Companion.dpToPx


class VolumeView: BaseHooker() {

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

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        startCollpasedColumn()
        startCollpasedColumnPress()


        startCollpasedFootButton()



    }

    private fun startCollpasedColumn() {


        val Builder = XposedHelpers.findClass("com.android.systemui.miui.ViewStateGroup\$Builder",classLoader)


        XposedHelpers.findAndHookMethod(Builder,"addStateWithIntDimen",Int::class.java,Int::class.java,Int::class.java,object :XC_MethodHook(){

            override fun afterHookedMethod(param: MethodHookParam?) {
                val thisObj = param?.thisObject
                val mContext = XposedHelpers.getObjectField(thisObj,"mContext") as Context
                val res = mContext.theme.resources
                val i3 = param?.args?.get(2) as Int
                val miuiVolumeOffsetTopCollapsed = res.getIdentifier("miui_volume_offset_top_collapsed","dimen",plugin)

                val miuiVolumeDialogShadowMarginTop = res.getIdentifier("miui_volume_dialog_shadow_margin_top","dimen",plugin)
                when(i3){
                    miuiVolumeOffsetTopCollapsed->{
                        val VolumeOffsetTop:Float
                        if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                            if (VolumeOffsetTopCollapsedP == -1f) return
                            VolumeOffsetTop = VolumeOffsetTopCollapsedP
                        }else{
                            if (VolumeOffsetTopCollapsedL == -1f) return
                            VolumeOffsetTop = VolumeOffsetTopCollapsedL

                        }
                        param.result = XposedHelpers.callMethod(thisObj,"addState",param.args?.get(0),param.args?.get(1),dpToPx(res,VolumeOffsetTop).toInt())

                    }
                    miuiVolumeDialogShadowMarginTop->{
                        val ShadowMarginTop:Float
                        if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                            if (ShadowMarginTopP == -1f) return
                            ShadowMarginTop = ShadowMarginTopP
                        }else{
                            if (ShadowMarginTopL == -1f) return
                            ShadowMarginTop = ShadowMarginTopL

                        }
                        param.result = XposedHelpers.callMethod(thisObj,"addState",param.args?.get(0),param.args?.get(1),dpToPx(res,ShadowMarginTop).toInt())

                    }
                }



            }
        })



        val MiuiVolumeDialogMotion = XposedHelpers.findClass("com.android.systemui.miui.volume.MiuiVolumeDialogMotion",classLoader)
        XposedHelpers.findAndHookMethod(MiuiVolumeDialogMotion,"updateShadowState",object :XC_MethodHook(){

            override fun afterHookedMethod(param: MethodHookParam?) {
                val thisObj = param?.thisObject
                val mContext = XposedHelpers.getObjectField(thisObj,"mContext") as Context
                val res = mContext.theme.resources
                val mShadowView = XposedHelpers.getObjectField(thisObj,"mShadowView") as View
                val mVolumeView =  XposedHelpers.getObjectField(thisObj,"mVolumeView") as View
                val mSuperVolume = XposedHelpers.getObjectField(thisObj,"mSuperVolume") as View
                val mRingerModeLayout = XposedHelpers.getObjectField(thisObj,"mRingerModeLayout") as View
                val shadowHeight:Float
                val layoutParams = mShadowView.layoutParams as ViewGroup.MarginLayoutParams

                if (!(XposedHelpers.callMethod(thisObj,"isLandscape") as Boolean)){
                    if (ShadowMarginTopP == -1f) return
                    var top= dpToPx(res,ShadowMarginTopP).toInt()
                    if (mSuperVolume.visibility == View.VISIBLE) {
                        val miuiShadowSuperVolumeHeight = res.getIdentifier("miui_shadow_super_volume_height","dimen",plugin)
                        top -= res.getDimensionPixelSize(miuiShadowSuperVolumeHeight)
                    }
                    layoutParams.topMargin = top
                    mShadowView.layoutParams = layoutParams

                }
                if (mRingerModeLayout.visibility == View.VISIBLE){

                    if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                        if (ShadowHeightP == -1f) return
                        shadowHeight = ShadowHeightP
                    }else{
                        if (ShadowHeightL == -1f) return
                        shadowHeight = ShadowHeightL

                    }
                }else{
                    return
                }
                mShadowView.layoutParams.height = dpToPx(res,shadowHeight).toInt()





            }
        })

//        XposedHelpers.findAndHookMethod(MiuiVolumeDialogMotion,"updateShadowState",object :XC_MethodReplacement(){
//            override fun replaceHookedMethod(param: MethodHookParam?): Any {
//                val thisObj = param?.thisObject
//                val mContext = XposedHelpers.getObjectField(thisObj, "mContext") as Context
//                val res = mContext.theme.resources
//
//                val miui_volume_column_width = res.getIdentifier("miui_volume_column_width","dimen",plugin)
//                var width = res.getDimensionPixelSize(miui_volume_column_width);
//
//                if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
//                    if (volumeWidthCollapsedP != -1f) {
//                        width = dpToPx(res,volumeWidthCollapsedP).toInt()
//                    }
//                } else {
//                    if (volumeWidthCollapsedL != -1f){
//                        width= dpToPx(res,volumeWidthCollapsedL).toInt()
//
//                    }
//                }
//
//
//                val widthPixels = (XposedHelpers.callMethod(thisObj,"provideDisplayMetrics") as DisplayMetrics).widthPixels
//                val mVisibleNumber = XposedHelpers.getIntField(thisObj,"mVisibleNumber")
//                var i3 = widthPixels - (width * mVisibleNumber)
//
//                if (width == 2) {
//                    val miui_volume_temp_margin_active = res.getIdentifier("miui_volume_temp_margin_active","dimen",plugin)
//                    i3 -= res.getDimensionPixelSize(miui_volume_temp_margin_active);
//                }
//
//                var seekbarMarginRight = i3 - (XposedHelpers.callMethod(thisObj,"getSeekbarMarginRight") as Int)
//                if (XposedHelpers.callMethod(thisObj,"volumeShowLeft") as Boolean) {
//                    seekbarMarginRight = XposedHelpers.callMethod(thisObj,"getSeekbarMarginRight") as Int
//                }
//                return if(
//                    XposedHelpers.callMethod(thisObj,"isLandscape") as Boolean){
//                    seekbarMarginRight - (XposedHelpers.callMethod(thisObj,"getInsetRight")as Int )
//                } else{
//                    seekbarMarginRight
//                }
//
//            }
//
//        })

        val DndPopupWindow = XposedHelpers.findClass("com.android.systemui.miui.volume.DndPopupWindow",classLoader)

        XposedHelpers.findAndHookMethod(DndPopupWindow,"getPositionY",Int::class.java,object :XC_MethodReplacement(){

            override fun replaceHookedMethod(param: MethodHookParam?): Any {
                val thisObj = param?.thisObject
                val i = param?.args?.get(0) as Int
                val mContext = XposedHelpers.getObjectField(thisObj, "mContext") as Context
                val res = mContext.theme.resources

                var miuiVolumeOffsetTopCollapsed = res.getIdentifier("miui_volume_offset_top_collapsed","dimen",plugin)

                if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                    if (VolumeHeightCollapsedP != -1f) {

                        miuiVolumeOffsetTopCollapsed = dpToPx(res,VolumeHeightCollapsedP).toInt()

                    }
                } else {
                    if (VolumeHeightCollapsedL != -1f){

                        miuiVolumeOffsetTopCollapsed= dpToPx(res,VolumeHeightCollapsedL).toInt()

                    }
                }
                val miuiVolumeFooterMarginTop = res.getIdentifier("miui_volume_footer_margin_top","dimen",plugin)
                val miuiVolumeSilenceButtonHeight = res.getIdentifier("miui_volume_silence_button_height","dimen",plugin)
                var miuiVolumeColumnHeight = res.getDimensionPixelSize(res.getIdentifier("miui_volume_column_height","dimen",plugin))
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
                        + ((res.getDimensionPixelSize(miuiVolumeSilenceButtonHeight)  * 1.5)))
                        + (res.getDimensionPixelSize(miuiVolumeFooterMarginTop) * 2)) - (i * 0.8f)
                return result.toInt()
            }
        })

//        XposedHelpers.findAndHookMethod(DndPopupWindow,"init",object :XC_MethodHook(){
//            override fun afterHookedMethod(param: MethodHookParam?) {
//                super.afterHookedMethod(param)
//                val thisObj = param?.thisObject
//                val mContext = XposedHelpers.getObjectField(thisObj, "mContext") as Context
//                val res = mContext.theme.resources
//                val mParams = XposedHelpers.getObjectField(thisObj,"mParams") as WindowManager.LayoutParams
//                val miui_volume_offset_end = res.getIdentifier("miui_volume_offset_end","dimen",plugin)
//                val miui_volume_column_width = res.getIdentifier("miui_volume_column_width","dimen",plugin)
//
//                val end = res.getDimensionPixelSize(miui_volume_offset_end) * 2
//                var width = res.getDimensionPixelSize(miui_volume_column_width)
//                if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
//                    if (volumeWidthCollapsedP != -1f){
//                        width = dpToPx(res,volumeWidthCollapsedP).toInt()
//
//                    }
//                } else {
//                    if (volumeWidthCollapsedL != -1f){
//                        width = dpToPx(res,volumeWidthCollapsedL).toInt()
//
//                    }
//                }
//                mParams.x = end + width
//
//            }
//
//        })

        val VolumePanelViewController = XposedHelpers.findClass("com.android.systemui.miui.volume.VolumePanelViewController",classLoader)

        XposedHelpers.findAndHookMethod(VolumePanelViewController,"updateColumnsSizeH",View::class.java,object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                val thisObj = param?.thisObject
                val view = param?.args?.get(0) as View
                val mContext = XposedHelpers.getObjectField(thisObj,"mContext") as Context
                val res = mContext.theme.resources
                val mExpanded =  XposedHelpers.getBooleanField(thisObj,"mExpanded")
                if (!mExpanded) {
                    val marginLayoutParams = view.layoutParams
                    //marginLayoutParams.width = dpToPx(res,80f).toInt()
                    val height :Float
                    if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                        if (VolumeHeightCollapsedP == -1f) return
                        height = VolumeHeightCollapsedP
                    } else {
                        if (VolumeHeightCollapsedL == -1f) return
                        height= VolumeHeightCollapsedL
                    }
                    marginLayoutParams.height = dpToPx(res,height).toInt()
                    view.layoutParams = marginLayoutParams
                }



            }
        })
        val MiuiVolumeSeekBarProgressView = XposedHelpers.findClass("com.android.systemui.miui.volume.MiuiVolumeSeekBarProgressView",classLoader)

        XposedHelpers.findAndHookMethod(MiuiVolumeSeekBarProgressView,"updateProgressViewSize",Boolean::class.java,Boolean::class.java,object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                val thisObj = param?.thisObject
                val mContext = XposedHelpers.getObjectField(thisObj,"mContext") as Context
                val res = mContext.theme.resources
                val mExpanded =  param?.args?.get(0) as Boolean
                if (!mExpanded) {
                    val view = thisObj as View
                    val marginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
                    //marginLayoutParams.width = dpToPx(res,80f).toInt()
                    var height :Int
                    if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                        if (VolumeHeightCollapsedP == -1f) return
                        height = dpToPx(res,VolumeHeightCollapsedP).toInt()
                    } else {
                        if (VolumeHeightCollapsedL == -1f) return
                        height= dpToPx(res,VolumeHeightCollapsedL).toInt()
                    }
                    if (height %2 == 1){
                        height++
                        marginLayoutParams.height = height
                    }else{
                        marginLayoutParams.height = height

                    }
                    XposedHelpers.setObjectField(thisObj,"mHeight",height)
                    view.layoutParams = marginLayoutParams
                }



            }
        })

        val RoundRectFrameLayout = XposedHelpers.findClass("com.android.systemui.miui.volume.RoundRectFrameLayout",classLoader)

        XposedHelpers.findAndHookMethod(RoundRectFrameLayout,"updateProgressViewSize",Boolean::class.java,Boolean::class.java,object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                val thisObj = param?.thisObject
                val mContext = XposedHelpers.getObjectField(thisObj,"mContext") as Context
                val res = mContext.resources
                val mExpanded =  param?.args?.get(0) as Boolean
                if (!mExpanded) {
                    val view = thisObj as View
                    val marginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
                    //marginLayoutParams.width = dpToPx(res,80f).toInt()
                    var height :Int
                    if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                        if (VolumeHeightCollapsedP == -1f) return
                        height = dpToPx(res,VolumeHeightCollapsedP).toInt()
                    } else {
                        if (VolumeHeightCollapsedL == -1f) return
                        height= dpToPx(res,VolumeHeightCollapsedL).toInt()
                    }
                    if (height %2 == 1){
                        height++
                        marginLayoutParams.height = height
                    }else{
                        marginLayoutParams.height = height

                    }
                    view.layoutParams = marginLayoutParams
                }



            }
        })

    }

    private fun startCollpasedColumnPress() {

        if (!isPressExpandVolume) return

        val MiuiVolumeDialogView = XposedHelpers.findClass("com.android.systemui.miui.volume.MiuiVolumeDialogView",classLoader)

        XposedHelpers.findAndHookMethod(MiuiVolumeDialogView,"onFinishInflate",object :XC_MethodHook(){

            override fun afterHookedMethod(param: MethodHookParam?) {
                val thisObj = param?.thisObject
                val mExpandButton = XposedHelpers.getObjectField(thisObj,"mExpandButton") as View
                mExpandButton.setOnClickListener(null)
                mExpandButton.alpha = 0f
                mExpandButton.isClickable = false
                mExpandButton.visibility = View.GONE



            }
        })


        XposedHelpers.findAndHookMethod(MiuiVolumeDialogView,"notifyAccessibilityChanged",Boolean::class.java,object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                val thisObj = param?.thisObject
                val mExpandButton = XposedHelpers.getObjectField(thisObj,"mExpandButton") as View
                mExpandButton.setOnClickListener(null)
                mExpandButton.isClickable = false
                mExpandButton.visibility = View.GONE


            }
        })



        val MiuiVolumeDialogMotion = XposedHelpers.findClass("com.android.systemui.miui.volume.MiuiVolumeDialogMotion",classLoader)

        XposedBridge.hookAllConstructors(MiuiVolumeDialogMotion,object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject
                val mExpandButton =  XposedHelpers.getObjectField(thisObj,"mExpandButton") as View
                mExpandButton.setOnTouchListener(null)



            }
        })



        var longClick = false
        XposedHelpers.findAndHookMethod(MiuiVolumeDialogMotion,"lambda\$processExpandTouch\$1",object :XC_MethodReplacement(){

            override fun replaceHookedMethod(param: MethodHookParam?): Any? {
                val thisObj = param?.thisObject
                if ( XposedHelpers.getBooleanField(thisObj,"mExpanded") || !longClick ) return null
                val mVolumeView = XposedHelpers.getObjectField(thisObj, "mVolumeView") as View

                starLog.log("processExpandTouch")

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
                            val mVolumeExpandCollapsedAnimator = XposedHelpers.getObjectField(thisObj, "mVolumeExpandCollapsedAnimator")
                            val mCallback = XposedHelpers.getObjectField(thisObj, "mCallback")
                            XposedHelpers.callMethod(mVolumeExpandCollapsedAnimator,"calculateFromViewValues",true)
                            XposedHelpers.callMethod(mCallback,"onExpandClicked")

                            mVolumeView.scaleX = 1f
                            mVolumeView.scaleY = 1f
                        }
                    })
                    start()
                }
                return null
            }

        })



        val MiuiVolumeSeekBar = XposedHelpers.findClass("com.android.systemui.miui.volume.MiuiVolumeSeekBar",classLoader)



        XposedHelpers.findAndHookMethod(MiuiVolumeSeekBar,"onTouchEvent",MotionEvent::class.java,object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject
                val mSeekBarOnclickListener =
                    XposedHelpers.getObjectField(thisObj, "mSeekBarOnclickListener")

                val handler = Handler(Looper.getMainLooper())
                val mLongPressRunnable = Runnable {
                    val mMoveY = XposedHelpers.getFloatField(thisObj, "mMoveY")
                    if (longClick){
                        thisObj as View
                        //thisObj.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        XposedHelpers.callMethod(mSeekBarOnclickListener, "onClick")
                    }
                }

                if (mSeekBarOnclickListener != null) {
                    val motionEvent = param?.args?.get(0) as MotionEvent

                    val action = motionEvent.action
                    when (action) {
                        0 -> {

                            longClick = true
                            XposedHelpers.setLongField(thisObj,"mCurrentMS",0L)

                            handler.postDelayed(mLongPressRunnable, 300L)

                        }
                        1-> {
                            longClick = false
                            handler.removeCallbacks(mLongPressRunnable)
                            XposedHelpers.setLongField(thisObj,"mCurrentMS",0L)
                        }

                        2 -> {
                            longClick = false
                            handler.removeCallbacks(mLongPressRunnable)


                        }
                    }

                }
            }

        })

    }

    private fun startCollpasedFootButton() {

        if (!isHideStandardView) return

        val RingerButtonHelper = XposedHelpers.findClass("com.android.systemui.miui.volume.MiuiRingerModeLayout\$RingerButtonHelper",classLoader)
        XposedHelpers.findAndHookMethod(RingerButtonHelper,"updateState",object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {

                val thisObj = param?.thisObject

                val mIcon = XposedHelpers.getObjectField(thisObj,"mIcon") as View
                val mStandardView = XposedHelpers.getObjectField(thisObj,"mStandardView") as View
                val mExpanded = XposedHelpers.getBooleanField(thisObj,"mExpanded")
                if (mExpanded){
                    mIcon.visibility = View.VISIBLE
                    mStandardView.visibility = View.VISIBLE
                }else{
                    mIcon.visibility = View.GONE
                    mStandardView.visibility = View.GONE

                }

            }
        })


        XposedHelpers.findAndHookMethod(RingerButtonHelper,"onExpanded",Boolean::class.java,Boolean::class.java,object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {

                val thisObj = param?.thisObject

                val z1 = param?.args?.get(0) as Boolean
                val z2 = param.args?.get(1) as Boolean

                val mStandardView = XposedHelpers.getObjectField(thisObj,"mStandardView") as View
                val mExpanded = XposedHelpers.getBooleanField(thisObj,"mExpanded")

                if (mExpanded != z1 || z2){
                    mStandardView.visibility = View.GONE
                }else{
                    mStandardView.visibility = View.VISIBLE
                }

            }
        })
        val TimerItem = XposedHelpers.findClass("com.android.systemui.miui.volume.TimerItem",classLoader)

        XposedHelpers.findAndHookMethod(TimerItem,"updateExpanded",Boolean::class.java,object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {

                val thisObj = param?.thisObject

                val mCountDownProgressBar = XposedHelpers.getObjectField(thisObj,"mCountDownProgressBar") as View

                mCountDownProgressBar.visibility = View.GONE

            }
        })


    }



}