package com.yunzia.hyperstar.hook.app.plugin.powermenu


import android.content.Context
import android.content.res.XModuleResources
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.FrameLayout
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.os1.app.plugin.powermenu.Action
import com.yunzia.hyperstar.hook.os1.app.plugin.powermenu.menuA
import com.yunzia.hyperstar.hook.os1.app.plugin.powermenu.menuB
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.callbacks.XC_InitPackageResources


class PowerMenu : Hooker() {

    var icBootloader = 0
    var icRecovery = 0
    var icAirplaneOn = 0
    var icAirplaneOff = 0
    var icSilentOn = 0
    var icSilentOff = 0
    var icQsScreenshot = 0

    var xiaoai = 0
    var alipayPay = 0
    var wechatScan = 0
    var alipayScan = 0
    var wechatPay = 0

    val isPowerMenuNavShow = XSPUtils.getBoolean("is_power_menu_nav_show",false)

    val isPowerMenuStyle = XSPUtils.getInt("is_power_menu_style",0)

    override fun initResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
        modRes: XModuleResources?
    ) {
        super.initResources(resparam, modRes)


        xiaoai = resparam?.res?.addResource(modRes,R.drawable.xiaoai)!!
        icBootloader = resparam.res?.addResource(modRes,R.drawable.ic_bootloader)!!
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



    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        val MiuiGlobalActionsDialog = findClass("com.android.systemui.miui.globalactions.MiuiGlobalActionsDialog",classLoader)
        val VolumeUtil = findClass("com.android.systemui.miui.volume.VolumeUtil",classLoader)

        if (isPowerMenuNavShow) {
            MiuiGlobalActionsDialog.afterHookMethod(
                "initDialog"
            ) {
                val flags = (View.SYSTEM_UI_FLAG_VISIBLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
                this.getObjectFieldAs<FrameLayout>("mRoot").systemUiVisibility = flags

            }
        }


        if (isPowerMenuStyle == 0) return
        var group: View? = null
        MiuiGlobalActionsDialog.apply {
            afterHookMethod(
                "initViews"
            ) {
                val mContext = this.getObjectFieldAs<Context>("mContext")
                val res = mContext.resources
                val mTalkbackLayout = this.getObjectFieldAs<FrameLayout>("mTalkbackLayout")
                val mSliderView = this.getObjectFieldAs<FrameLayout>("mSliderView")

                val s = mSliderView.layoutParams as FrameLayout.LayoutParams
                //mSliderView.translationX = 250f
                val action = Action(
                    mContext,xiaoai, icBootloader, icRecovery, icAirplaneOn, icAirplaneOff,
                    icSilentOn, icSilentOff, icQsScreenshot, alipayPay, wechatScan, alipayScan, wechatPay , VolumeUtil!!
                )

                when(isPowerMenuStyle){
                    1->{

                        val items1: List<Item?> = listOf(
                            action.getAction("recovery"),
                            action.getAction("bootloader"),
                        )

                        group = menuB(
                            mContext, this, items1, mTalkbackLayout, mSliderView
                        )
                    }
                    2->{

                        var items1: List<Item?> = emptyList()
                        val items1m  = items1.toMutableList()
                        for (i in 0..7){
                            val type = XSPUtils.getString("power_menu_style_b_$i","null").toString()
                            if (type != "null"){
                                items1m.add(action.getAction(type))

                            }

                        }
                        items1 = items1m.toList()


                        group = menuA(
                            mContext, this, items1, mTalkbackLayout, mSliderView
                        )
                    }
                }
            }
            afterHookMethod(
                "dismiss",
                Int::class.java
            ){
                if (group == null) return@afterHookMethod

                group!!.visibility = View.GONE

            }

        }
        findClass(
            "com.android.systemui.miui.globalactions.SliderView",
            classLoader
        ).afterHookMethod(
            "handleActionMoveForAlpha",
            Float::class.java
        ) {
            if (group == null) return@afterHookMethod
            val mDark = this.getObjectFieldAs<View>("mDark")
            group!!.alpha = (1-mDark.alpha)
        }

    }

    data class Item(
        val image: Drawable? = null,
        val text: String? = null,
        val state: Boolean = false,
        val isEmpty: Boolean? = false,
        val click: ((View, Context) -> Unit)? = null
    )



}

