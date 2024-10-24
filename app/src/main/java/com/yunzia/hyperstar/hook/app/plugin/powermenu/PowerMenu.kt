package com.yunzia.hyperstar.hook.app.plugin.powermenu


import android.content.Context
import android.content.res.XModuleResources
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.FrameLayout
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.hook.app.plugin.powermenu.Action.Companion.apCode
import com.yunzia.hyperstar.hook.app.plugin.powermenu.Action.Companion.apScan
import com.yunzia.hyperstar.hook.app.plugin.powermenu.Action.Companion.bootloader
import com.yunzia.hyperstar.hook.app.plugin.powermenu.Action.Companion.recovery
import com.yunzia.hyperstar.hook.app.plugin.powermenu.Action.Companion.screenshot
import com.yunzia.hyperstar.hook.app.plugin.powermenu.Action.Companion.wcCode
import com.yunzia.hyperstar.hook.app.plugin.powermenu.Action.Companion.wcScaner
import com.yunzia.hyperstar.hook.app.plugin.powermenu.Action.Companion.xiaoai
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources


class PowerMenu : BaseHooker() {

    var icBootloader = 0
    var icRecovery = 0
    var icAirplaneOn = 0
    var icAirplaneOff = 0
    var icSilentOn = 0
    var icSilentOff = 0
    var icQsScreenshot = 0

    var alipayPay = 0
    var wechatScan = 0
    var alipayScan = 0
    var wechatPay = 0

    val isPowerMenuNavShow = XSPUtils.getBoolean("is_power_menu_nav_show",false)

    val isPowerMenuStyle = XSPUtils.getInt("is_power_menu_style",0)

    override fun doResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
        modRes: XModuleResources?
    ) {
        super.doResources(resparam, modRes)


        icBootloader = resparam?.res?.addResource(modRes,R.drawable.ic_bootloader)!!
        icRecovery = resparam.res?.addResource(modRes,R.drawable.ic_recovery)!!
        icAirplaneOn = resparam.res?.addResource(modRes,R.drawable.ic_airplane_on)!!
        icAirplaneOff = resparam.res?.addResource(modRes,R.drawable.ic_airplane_off)!!
        icSilentOn = resparam.res?.addResource(modRes,R.drawable.ic_silent_on)!!
        icSilentOff = resparam.res?.addResource(modRes,R.drawable.ic_silent_off)!!
        icQsScreenshot = resparam.res?.addResource(modRes,R.drawable.ic_qs_screenshot)!!
        alipayScan = resparam.res?.addResource(modRes,R.drawable.alipay_scan)!!
        alipayPay = resparam.res?.addResource(modRes,R.drawable.alipay_pay)!!
        wechatScan = resparam.res?.addResource(modRes,R.drawable.wechat_scan)!!
        wechatPay = resparam.res?.addResource(modRes,R.drawable.wechat_pay)!!

    }



    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)

        val MiuiGlobalActionsDialog = XposedHelpers.findClass("com.android.systemui.miui.globalactions.MiuiGlobalActionsDialog",classLoader)

        if (isPowerMenuNavShow){
            XposedHelpers.findAndHookMethod(MiuiGlobalActionsDialog,"initDialog",object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    val thisObj = param?.thisObject
                    val mRoot = XposedHelpers.getObjectField(thisObj,"mRoot") as FrameLayout
                    val flags = (View.SYSTEM_UI_FLAG_VISIBLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
                    mRoot.systemUiVisibility = flags

                }
            })

        }

        if (isPowerMenuStyle == 0) return

        var group: View? = null
        XposedHelpers.findAndHookMethod(MiuiGlobalActionsDialog,"initViews",object : XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject
                val mContext = XposedHelpers.getObjectField(thisObj,"mContext") as Context
                val res = mContext.resources
                val mTalkbackLayout = XposedHelpers.getObjectField(thisObj,"mTalkbackLayout") as FrameLayout
                val mSliderView = XposedHelpers.getObjectField(thisObj,"mSliderView") as FrameLayout

                val s = mSliderView.layoutParams as FrameLayout.LayoutParams
                //mSliderView.translationX = 250f

                val items1: List<Item?> = listOf(
                    recovery(mContext,icRecovery),
                    bootloader(mContext,icBootloader),
                    xiaoai(mContext,icAirplaneOn),
                    screenshot(mContext,icQsScreenshot),
                    wcScaner(mContext,wechatScan),
                    wcCode(mContext,wechatPay),
                    apScan(mContext,alipayScan),
                    apCode(mContext,alipayPay)
                )
                when(isPowerMenuStyle){
                    1->{
                        group = menuB(
                            mContext,thisObj,items1,mTalkbackLayout,mSliderView
                        )
                    }
                    2->{
                        group = menuA(
                            mContext,thisObj,items1,mTalkbackLayout,mSliderView
                        )
                    }
                }

            }
        })

        val SliderView = XposedHelpers.findClass("com.android.systemui.miui.globalactions.SliderView",classLoader)

        XposedHelpers.findAndHookMethod(SliderView,"handleActionMoveForAlpha",Float::class.java,object : XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                if (group == null) return
                val thisObj = param?.thisObject
                val mDark = XposedHelpers.getObjectField(thisObj,"mDark") as View



                group!!.alpha = (1-mDark.alpha)


            }
        })

        XposedHelpers.findAndHookMethod(MiuiGlobalActionsDialog,"dismiss",Int::class.java,object : XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                if (group == null) return
                //group!!.clearAnimation()
                //group!!.animatin

                group!!.visibility = View.GONE


            }
        })

    }

    data class Item(
        val image: Drawable? = null,
        val text: String? = null,
        val state: Boolean = false,
        val isEmpty: Boolean? = false,
        val click: ((View, Context) -> Unit)? = null
    )



}

