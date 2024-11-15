package com.yunzia.hyperstar.hook.app.plugin

import android.R
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
import android.widget.SeekBar
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
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


        val Builder = XposedHelpers.findClass("com.android.systemui.miui.ViewStateGroup.Builder",classLoader)


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
//                var top: Int
//                if (XposedHelpers.callMethod(thisObj,"isLandscape") as Boolean){
//                    return
////                    val miuiVolumeDialogShadowTopOffset = res.getIdentifier("miui_volume_dialog_shadow_top_offset","dimen",plugin)
////                    top= mVolumeView.top - res.getDimensionPixelSize(miuiVolumeDialogShadowTopOffset)
//                }

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


        val DndPopupWindow = XposedHelpers.findClass("com.android.systemui.miui.volume.DndPopupWindow",classLoader)

//        XposedHelpers.findAndHookMethod(DndPopupWindow,"getPositionY",Int::class.java,object :XC_MethodReplacement(){
//
//            override fun replaceHookedMethod(param: MethodHookParam?): Any? {
//                val thisObj = param?.thisObject
//                val i = param?.args?.get(0) as Int
//                val mContext = XposedHelpers.getObjectField(thisObj, "mContext") as Context
//                val res = mContext.theme.resources
////                dpToPx(res, 40f).toInt()
////                dpToPx(res, 150f).toInt()
//
//                val miuiVolumeOffsetTopCollapsed = res.getIdentifier("miui_volume_offset_top_collapsed","dimen",plugin)
//                val miuiVolumeFooterMarginTop = res.getIdentifier("miui_volume_footer_margin_top","dimen",plugin)
//                val miuiVolumeColumnHeight = res.getIdentifier("miui_volume_column_height","dimen",plugin)
//                param.result =(((res.getDimensionPixelSize(miuiVolumeOffsetTopCollapsed) + res.getDimensionPixelSize(miuiVolumeColumnHeight))
//                        + ((dpToPx(res, 150f) * 1.5)))
//                        + (res.getDimensionPixelSize(miuiVolumeFooterMarginTop) * 2)) - (i * 0.8f)
//                return null
//            }
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
        var miuiVolumeDialogMotion: Any? = null
        var mExpanded = false

        XposedHelpers.findAndHookMethod(MiuiVolumeDialogMotion,"processExpandTouch",object :XC_MethodHook(){

            override fun beforeHookedMethod(param: MethodHookParam?) {
                super.beforeHookedMethod(param)
                miuiVolumeDialogMotion  = param?.thisObject
                mExpanded =  XposedHelpers.getBooleanField(miuiVolumeDialogMotion,"mExpanded")

            }

        })


        XposedHelpers.findAndHookMethod(MiuiVolumeDialogMotion,"updateStateToExpand",Boolean::class.java,object :XC_MethodHook(){

            override fun beforeHookedMethod(param: MethodHookParam?) {
                super.beforeHookedMethod(param)
                miuiVolumeDialogMotion  = param?.thisObject
                mExpanded =  XposedHelpers.getBooleanField(miuiVolumeDialogMotion,"mExpanded")

            }

        })

        val VolumeSeekBarChangeListener = XposedHelpers.findClass("com.android.systemui.miui.volume.VolumePanelViewController\$VolumeSeekBarChangeListener",classLoader)


        XposedHelpers.findAndHookMethod(VolumeSeekBarChangeListener,"onStartTrackingTouch",SeekBar::class.java,object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                if (mExpanded) return
                val seekBar = param?.args?.get(0) as SeekBar
                val parent = seekBar.parent as View
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    if (longClick && miuiVolumeDialogMotion != null) {
                        with(AnimatorSet()) {
                            playTogether(
                                ObjectAnimator.ofFloat(parent, "scaleX", 0.95f),
                                ObjectAnimator.ofFloat(parent, "scaleY", 0.95f)
                            )
                            duration = 100L
                            addListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    super.onAnimationEnd(animation)
                                    XposedHelpers.setBooleanField(miuiVolumeDialogMotion, "mIsExpandButton", true)
                                    seekBar.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                                    XposedHelpers.callMethod(miuiVolumeDialogMotion, "lambda\$processExpandTouch\$1")

                                    parent.scaleX = 1f
                                    parent.scaleY = 1f
                                }
                            })
                            start()
                        }
                    }
                }, 300L)



            }
        })
        XposedHelpers.findAndHookMethod(VolumeSeekBarChangeListener,"onStopTrackingTouch",SeekBar::class.java,object :XC_MethodHook(){

            override fun afterHookedMethod(param: MethodHookParam?) {
                if (!longClick) return
                longClick = false

            }
        })
        XposedHelpers.findAndHookMethod(VolumeSeekBarChangeListener,"onProgressChanged",SeekBar::class.java,Int::class.java,Boolean::class.java,object :XC_MethodHook(){
            override fun beforeHookedMethod(param: MethodHookParam?) {
                super.beforeHookedMethod(param)
                if (longClick){
                    val seekBar = param?.args?.get(0) as SeekBar
                    val progress = param.args?.get(1) as Int
                    starLog.log("$progress")
                    if (progress ==0 || progress == seekBar.max) return
                    param.args?.set(2, false)
                    longClick = false
                    return
                }
            }
            override fun afterHookedMethod(param: MethodHookParam?) {
                if (!longClick) return



            }
        })

        val MiuiVolumeSeekBar = XposedHelpers.findClass("com.android.systemui.miui.volume.MiuiVolumeSeekBar",classLoader)
        XposedHelpers.findAndHookMethod(MiuiVolumeSeekBar,"onTouchEvent",MotionEvent::class.java,object :XC_MethodHook(){

            override fun afterHookedMethod(param: MethodHookParam?) {
                val motionEvent = param?.args?.get(0) as MotionEvent
                val action = motionEvent.action
                when(action){
                    0->{
                        longClick = true
                        starLog.log("MiuiVolumeSeekBar longClick")
                    }
                    2->{
                        longClick = false

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