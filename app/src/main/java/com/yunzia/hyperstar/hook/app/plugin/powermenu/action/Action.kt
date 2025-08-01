package com.yunzia.hyperstar.hook.app.plugin.powermenu.action

import android.R
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.StateListDrawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.os.RemoteException
import android.os.UserHandle
import android.provider.Settings
import com.yunzia.hyperstar.hook.app.plugin.powermenu.base.MenuItem
import de.robv.android.xposed.XposedHelpers

class Action(
    val mContext: Context,
    var xiaoai:Int,
    var icBootloader:Int,
    var icRecovery :Int,
    var icAirplaneOn :Int,
    var icAirplaneOff :Int,
    var icSilentOn :Int,
    var icSilentOff :Int,
    var icQsScreenshot :Int,
    var alipayPay :Int,
    var wechatScan :Int,
    var alipayScan :Int,
    var wechatPay :Int,
    val VolumeUtil: Class<*>
) {

    fun getAction(
        action:String
    ): MenuItem {

        when(action){
            "empty"->{
                return MenuItem(isEmpty = true)
            }
            "recovery"->{
                return recovery(mContext,icRecovery)
            }
            "bootloader"->{
                return bootloader(mContext,icBootloader)
            }
            "xiaoai"->{
                return xiaoai(mContext,xiaoai)

            }
            "screenshot"->{
                return screenshot(mContext,icQsScreenshot)
            }
            "silentMode"->{
                return silentMode(mContext, VolumeUtil, icSilentOn, icSilentOff)
            }
            "airPlane"->{
                return airPlane(mContext,icAirplaneOn, icAirplaneOff)
            }
            "wcScan"->{
                return wcScaner(mContext,wechatScan)
            }
            "wcCode"->{
                return wcCode(mContext,wechatPay)
            }
            "apScan"->{
                return apScan(mContext,alipayScan)
            }
            "apCode"->{
                return apCode(mContext,alipayPay)
            }
            else->{
                return MenuItem(isEmpty = true)

            }

        }


    }

    fun rebootToMode(context: Context, mode:String) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        try {
            powerManager.reboot(mode)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun createStateListDrawable(context: Context, state: Boolean, selectedID:Int, defaultID:Int): StateListDrawable {

        val selectedDrawable = context.getDrawable(selectedID)

        val defaultDrawable = context.getDrawable(defaultID)

        val stateListDrawable = StateListDrawable().apply {
            addState(intArrayOf(R.attr.state_selected), selectedDrawable)
            addState(intArrayOf(), if (state) selectedDrawable else defaultDrawable)
        }

        return stateListDrawable
    }
    fun recovery (
        mContext: Context,
        icRecovery:Int
    ): MenuItem {
        return MenuItem(
            mContext.getDrawable(icRecovery),
            "Recovery",
            false
        ) { _, _ ->
            rebootToMode(mContext, "recovery")
        }
    }
    fun bootloader (
        mContext: Context,
        icBootloader:Int
    ): MenuItem {
        return MenuItem(
            mContext.getDrawable(icBootloader),
            "Fastboot",
            false
        ) { _, _ ->
            rebootToMode(mContext, "bootloader")
        }
    }
    @SuppressLint("MissingPermission")
    fun screenshot (
        mContext: Context,
        icQsScreenshot:Int
    ): MenuItem {
        return MenuItem(
            mContext.getDrawable(icQsScreenshot),
            "SCREENSHOT",
            false
        ) { _, context: Context ->
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    val clazz = Class.forName("android.os.UserHandle")
                    val field = clazz.getDeclaredField("CURRENT")
                    val currentUserHandle = field.get(null) as UserHandle
                    context.sendBroadcastAsUser(
                        Intent("android.intent.action.CAPTURE_SCREENSHOT"),
                        currentUserHandle
                    )
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                } catch (e: NoSuchFieldException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }, 400)

        }
    }
    @SuppressLint("WrongConstant")
    fun xiaoai (
        mContext: Context,
        isXiaoai:Int
    ): MenuItem {
        return MenuItem(
            mContext.getDrawable(isXiaoai),
            "小爱同学",
            false
        ) { _, context: Context ->
            val intent = Intent("android.intent.action.ASSIST")
            intent.setPackage("com.miui.voiceassist")
            intent.putExtra("voice_assist_start_from_key", "FOD")
            intent.setFlags(0x14800000)
            context.startActivity(intent)

        }

    }
    fun airPlane (
        mContext: Context,
        icAirplaneOn:Int,
        icAirplaneOff:Int
    ): MenuItem {
        val airPlaneMode = Settings.Global.getInt(mContext.contentResolver,"airplane_mode_on",0)==1
        return MenuItem(
            createStateListDrawable(mContext, airPlaneMode, icAirplaneOn, icAirplaneOff),
            "飞行模式",
            airPlaneMode
        ) { v, context ->
            val enable = !airPlaneMode
            v.isSelected = enable
            Settings.Global.putInt(
                context.contentResolver,
                Settings.Global.AIRPLANE_MODE_ON,
                if (enable) 1 else 0
            )
            val intent = Intent("android.intent.action.AIRPLANE_MODE")
            intent.putExtra("state", enable)
            context.sendBroadcast(intent)
        }
    }
    fun silentMode(
        mContext: Context,
        VolumeUtil: Class<*>,
        icSilentOn:Int,
        icSilentOff:Int
    ): MenuItem {
        val silentMode = XposedHelpers.callStaticMethod(VolumeUtil,"isSilentMode",mContext)  as Boolean
        return MenuItem(
            createStateListDrawable(mContext, silentMode, icSilentOn, icSilentOff),
            "静音",
            silentMode
        ) { v, _ ->
            v.isSelected = !silentMode
            XposedHelpers.callStaticMethod(VolumeUtil, "setSilenceMode", mContext, !silentMode)
        }
    }
    fun wcScaner (
        mContext: Context,
        wechatScan:Int
    ): MenuItem {
        return MenuItem(
            mContext.getDrawable(wechatScan),
            "微信扫一扫",
            false
        ) { _, context: Context ->

            val intent = Intent()
            intent.setComponent(ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"))
            intent.putExtra("LauncherUI.From.Scaner.Shortcut", true)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setAction("android.intent.action.VIEW")
            context.startActivity(intent)

        }
    }
    @SuppressLint("WrongConstant")
    fun wcCode (
        mContext: Context,
        wechatPay:Int
    ): MenuItem {
        return MenuItem(
            mContext.getDrawable(wechatPay),
            "微信收付款",
            false
        ) { _, context: Context ->

            val intent = Intent("android.intent.action.VIEW")
            intent.setFlags(0x14800000)
            intent.setComponent(
                ComponentName(
                    "com.tencent.mm",
                    "com.tencent.mm.plugin.offline.ui.WalletOfflineCoinPurseUI"
                )
            )
            intent.putExtra("key_entry_scene", 2)
            context.startActivity(intent)

        }
    }
    fun apCode (
        mContext: Context,
        alipayPay:Int
    ): MenuItem {
        return MenuItem(
            mContext.getDrawable(alipayPay),
            "支付宝收款码",
            false
        ) { _, context: Context ->

            val uri = Uri.parse("alipayqr://platformapi/startapp?saId=20000056")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)

        }
    }
    fun apScan (
        mContext: Context,
        alipayScan:Int
    ): MenuItem {
        return MenuItem(
            mContext.getDrawable(alipayScan),
            "支付宝扫一扫",
            false
        ) { _, context: Context ->
            val uri = Uri.parse("alipayqr://platformapi/startapp?saId=20000056");
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)

        }
    }




}