package com.yunzia.hyperstar.hook.app.plugin

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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

        startCollpasedFootButton()



    }

    private fun startCollpasedColumn() {


        if (VolumeOffsetTopCollapsedP != -1f || VolumeOffsetTopCollapsedL != -1f) {

            val Builder = XposedHelpers.findClass("com.android.systemui.miui.ViewStateGroup\$Builder",classLoader)


            XposedHelpers.findAndHookMethod(Builder,"addStateWithIntDimen",Int::class.java,Int::class.java,Int::class.java,object :XC_MethodHook(){

                override fun afterHookedMethod(param: MethodHookParam?) {
                    val thisObj = param?.thisObject
                    val mContext = XposedHelpers.getObjectField(thisObj,"mContext") as Context
                    val res = mContext.theme.resources
                    val i3 = param?.args?.get(2) as Int
                    val miuiVolumeOffsetTopCollapsed = res.getIdentifier("miui_volume_offset_top_collapsed","dimen",plugin)

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

                    }



                }
            })

        }



        if (ShadowMarginTopP != -1f || ShadowMarginTopL != -1f){

            val MiuiVolumeDialogMotion5 = XposedHelpers.findClass("com.android.systemui.miui.volume.MiuiVolumeDialogMotion\$5",classLoader)

            XposedHelpers.findAndHookMethod(MiuiVolumeDialogMotion5,"onGlobalLayout",object :XC_MethodHook() {

                override fun afterHookedMethod(param: MethodHookParam?) {
                    val thisObj = param?.thisObject
                    val this0 = XposedHelpers.getObjectField(thisObj,"this$0")
                    val mContext = XposedHelpers.getObjectField(this0,"mContext") as Context
                    val res = mContext.resources
                    val mShadowView = XposedHelpers.getObjectField(this0,"mShadowView") as View

                    if ((XposedHelpers.callMethod(this0,"isLandscape") as Boolean)){
//                        val mDialogView = XposedHelpers.getObjectField(this0,"mDialogView") as View
//                        var top = mDialogView.top - res.getDimensionPixelSize(res.getIdentifier("miui_volume_dialog_shadow_top_offset","dimen",plugin))
////                        if (ShadowMarginTopP == -1f) return
////                        top = mDialogView.top - dpToPx(res,ShadowMarginTopP).toInt()
//                        XposedHelpers.callMethod(this0,"setTopMargin",mShadowView,top)

                    }else{
                        val ShadowMarginTop:Int
                        if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                            if (ShadowMarginTopP == -1f) return
                            ShadowMarginTop = dpToPx(res,ShadowMarginTopP).toInt()
                        }else{
                            if (ShadowMarginTopL == -1f) return
                            ShadowMarginTop = dpToPx(res,ShadowMarginTopL).toInt()

                        }

                        XposedHelpers.callMethod(this0,"setTopMargin",mShadowView,ShadowMarginTop)

                    }


                }
            })

        }
        if (ShadowHeightP != -1f || ShadowHeightL != -1f ){

            val MiuiVolumeDialogMotion = XposedHelpers.findClass("com.android.systemui.miui.volume.MiuiVolumeDialogMotion",classLoader)

            XposedHelpers.findAndHookMethod(MiuiVolumeDialogMotion,"createShowAnimator",object :XC_MethodHook(){

                override fun afterHookedMethod(param: MethodHookParam?) {
                    val thisObj = param?.thisObject
                    val mContext = XposedHelpers.getObjectField(thisObj,"mContext") as Context
                    val res = mContext.resources
                    val mShadowView = XposedHelpers.getObjectField(thisObj,"mShadowView") as View
                    val mRingerModeLayout = XposedHelpers.getObjectField(thisObj,"mRingerModeLayout") as View
                    val shadowHeight:Float

                    if (mRingerModeLayout.visibility == View.VISIBLE){

                        if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                            if (ShadowHeightP == -1f) return
                            shadowHeight = ShadowHeightP
                        }else{
                            if (ShadowHeightL == -1f) return
                            shadowHeight = ShadowHeightL

                        }
                        mShadowView.layoutParams.height = dpToPx(res,shadowHeight).toInt()
                    }





                }
            })

        }

        if (VolumeOffsetTopCollapsedP != -1f || VolumeOffsetTopCollapsedL != -1f || VolumeHeightCollapsedP != -1f || VolumeHeightCollapsedL != -1f){


            val DndPopupWindow = XposedHelpers.findClass("com.android.systemui.miui.volume.DndPopupWindow",classLoader)

            XposedHelpers.findAndHookMethod(DndPopupWindow,"getPositionY",Int::class.java,object :XC_MethodReplacement(){

                override fun replaceHookedMethod(param: MethodHookParam?): Any {
                    val thisObj = param?.thisObject
                    val i = param?.args?.get(0) as Int
                    val mContext = XposedHelpers.getObjectField(thisObj, "mContext") as Context
                    val res = mContext.theme.resources

                    var miuiVolumeOffsetTopCollapsed = res.getDimensionPixelSize(res.getIdentifier("miui_volume_offset_top_collapsed","dimen",plugin))

                    if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                        if (VolumeOffsetTopCollapsedP != -1f) {

                            miuiVolumeOffsetTopCollapsed = dpToPx(res,VolumeOffsetTopCollapsedP).toInt()

                        }
                    } else {
                        if (VolumeOffsetTopCollapsedL != -1f){

                            miuiVolumeOffsetTopCollapsed= dpToPx(res,VolumeOffsetTopCollapsedL).toInt()

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
        }




        val MiuiVolumeDialogImpl = XposedHelpers.findClass("com.android.systemui.miui.volume.MiuiVolumeDialogImpl",classLoader)

        XposedHelpers.findAndHookMethod(MiuiVolumeDialogImpl,"updateColumnsSizeH",View::class.java,object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                val thisObj = param?.thisObject
                val view = param?.args?.get(0) as View
                val mContext = XposedHelpers.getObjectField(thisObj,"mContext") as Context
                val res = mContext.theme.resources
                val mExpanded =  XposedHelpers.getBooleanField(thisObj,"mExpanded")
                if (!mExpanded) {
                    val marginLayoutParams = view.layoutParams
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
                    XposedHelpers.setObjectField(thisObj,"mHeight",height)
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


        XposedHelpers.findAndHookMethod(RingerButtonHelper,"onExpanded",Boolean::class.java,object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {

                val thisObj = param?.thisObject

                val z1 = param?.args?.get(0) as Boolean

                val mStandardView = XposedHelpers.getObjectField(thisObj,"mStandardView") as View
                val mExpanded = XposedHelpers.getBooleanField(thisObj,"mExpanded")

                if (mExpanded != z1){
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