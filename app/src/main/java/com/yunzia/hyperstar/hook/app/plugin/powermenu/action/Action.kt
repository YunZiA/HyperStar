package com.yunzia.hyperstar.hook.app.plugin.powermenu.action

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.StateListDrawable
import android.os.Handler
import android.os.Looper
import android.os.UserHandle
import android.provider.Settings
import androidx.core.net.toUri
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.hook.app.plugin.powermenu.base.MenuItem
import com.yunzia.hyperstar.hook.core.StarLog
import com.yunzia.hyperstar.hook.core.helper.callStaticMethod

object Action {

    private val serviceManagerClass by lazy { Class.forName("android.os.ServiceManager") }
    private val getServiceMethod by lazy { serviceManagerClass.getMethod("getService", String::class.java) }
    private val stubClass by lazy { Class.forName("android.os.IPowerManager\$Stub") }
    private val asInterfaceMethod by lazy { stubClass.getMethod("asInterface", android.os.IBinder::class.java) }
    private val userHandleClass by lazy { Class.forName("android.os.UserHandle") }
    private val currentField by lazy { userHandleClass.getDeclaredField("CURRENT") }

    private val powerManagerProxy by lazy {
        val binder = getServiceMethod.invoke(null, "power")
        asInterfaceMethod.invoke(null, binder)
    }

    private val rebootMethod by lazy {
        powerManagerProxy.javaClass.getMethod(
            "reboot",
            Boolean::class.javaPrimitiveType,
            String::class.java,
            Boolean::class.javaPrimitiveType
        )
    }

    @JvmStatic
    fun getAction(mContext: Context, VolumeUtil: Class<*>, action: String): MenuItem = when (action) {
        "empty" -> MenuItem(isEmpty = true)
        "recovery" -> recovery(mContext, R.drawable.ic_recovery)
        "bootloader" -> bootloader(mContext, R.drawable.ic_bootloader)
        "xiaoai" -> xiaoai(mContext, R.drawable.xiaoai)
        "screenshot" -> screenshot(mContext, R.drawable.ic_qs_screenshot)
        "silentMode" -> silentMode(mContext, VolumeUtil, R.drawable.ic_silent_on, R.drawable.ic_silent_off)
        "airPlane" -> airPlane(mContext, R.drawable.ic_airplane_on, R.drawable.ic_airplane_off)
        "wcScan" -> wcScaner(mContext, R.drawable.wechat_scan)
        "wcCode" -> wcCode(mContext, R.drawable.wechat_pays)
        "apScan" -> apScan(mContext, R.drawable.alipay_scan)
        "apCode" -> apCode(mContext, R.drawable.alipay_pay)
        else -> MenuItem(isEmpty = true)
    }

    @JvmStatic
    fun rebootToMode(context: Context, mode: String) = reboot(context, mode)

    fun reboot(context: Context, mode: String) {
        try {
            rebootMethod.invoke(powerManagerProxy, false, mode, false)
        } catch (e: Throwable) {
            StarLog.logE("rebootToMode", mode, e)
        }
    }

    // --- Action factories ---

    @JvmStatic
    fun recovery(mContext: Context, icon: Int, action: (() -> Unit)? = null): MenuItem {
        return rebootAction(mContext, icon, "Recovery", "recovery", action)
    }

    @JvmStatic
    fun bootloader(mContext: Context, icon: Int, action: (() -> Unit)? = null): MenuItem {
        return rebootAction(mContext, icon, "Fastboot", "bootloader", action)
    }

    private fun rebootAction(
        context: Context,
        icon: Int,
        label: String,
        mode: String,
        action: (() -> Unit)?
    ) = MenuItem(context.getDrawable(icon), label, false) { _, _ ->
        action?.invoke() ?: reboot(context, mode)
    }

    @SuppressLint("MissingPermission")
    @JvmStatic
    fun screenshot(mContext: Context, icon: Int) = MenuItem(
        mContext.getDrawable(icon), "SCREENSHOT", false
    ) { _, context: Context ->
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                val currentUserHandle = currentField.get(null) as UserHandle
                context.sendBroadcastAsUser(
                    Intent("android.intent.action.CAPTURE_SCREENSHOT"),
                    currentUserHandle
                )
            } catch (e: ReflectiveOperationException) {
                e.printStackTrace()
            }
        }, 400)
    }

    @JvmStatic
    @SuppressLint("WrongConstant")
    fun xiaoai(mContext: Context, icon: Int) = MenuItem(
        mContext.getDrawable(icon), "小爱同学", false
    ) { _, context: Context ->
        val intent = Intent("android.intent.action.ASSIST").apply {
            setPackage("com.miui.voiceassist")
            putExtra("voice_assist_start_from_key", "FOD")
            setFlags(0x14800000)
        }
        context.startActivity(intent)
    }

    @JvmStatic
    fun airPlane(mContext: Context, iconOn: Int, iconOff: Int): MenuItem {
        val enabled = Settings.Global.getInt(mContext.contentResolver, "airplane_mode_on", 0) == 1
        return MenuItem(
            createStateListDrawable(mContext, iconOn, iconOff),
            "飞行模式", enabled
        ) { v, context ->
            val newState = !v.isSelected
            v.isSelected = newState
            Settings.Global.putInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, if (newState) 1 else 0)
            context.sendBroadcast(Intent("android.intent.action.AIRPLANE_MODE").putExtra("state", newState))
        }
    }

    @JvmStatic
    fun silentMode(mContext: Context, VolumeUtil: Class<*>, iconOn: Int, iconOff: Int): MenuItem {
        val enabled = VolumeUtil.callStaticMethod("isSilentMode", mContext) as Boolean
        return MenuItem(
            createStateListDrawable(mContext, iconOn, iconOff),
            "静音", enabled
        ) { v, _ ->
            val newState = !v.isSelected
            v.isSelected = newState
            VolumeUtil.callStaticMethod("setSilenceMode", mContext, newState)
        }
    }

    @JvmStatic
    fun wcScaner(mContext: Context, icon: Int) = intentAction(mContext, icon, "微信扫一扫") {
        Intent().apply {
            component = ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI")
            putExtra("LauncherUI.From.Scaner.Shortcut", true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            action = "android.intent.action.VIEW"
        }
    }

    @SuppressLint("WrongConstant")
    @JvmStatic
    fun wcCode(mContext: Context, icon: Int) = intentAction(mContext, icon, "微信收付款") {
        Intent("android.intent.action.VIEW").apply {
            flags = 0x14800000
            component = ComponentName("com.tencent.mm", "com.tencent.mm.plugin.offline.ui.WalletOfflineCoinPurseUI")
            putExtra("key_entry_scene", 2)
        }
    }

    @JvmStatic
    fun apCode(mContext: Context, icon: Int) = intentAction(mContext, icon, "支付宝收款码") {
        Intent(Intent.ACTION_VIEW, "alipayqr://platformapi/startapp?saId=20000056".toUri()).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    @JvmStatic
    fun apScan(mContext: Context, icon: Int) = intentAction(mContext, icon, "支付宝扫一扫") {
        Intent(Intent.ACTION_VIEW, "alipayqr://platformapi/startapp?saId=20000056".toUri()).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    // --- Helpers ---

    private fun intentAction(context: Context, icon: Int, label: String, intentBuilder: () -> Intent) =
        MenuItem(context.getDrawable(icon), label, false) { _, ctx: Context ->
            ctx.startActivity(intentBuilder())
        }

    @JvmStatic
    fun createStateListDrawable(context: Context, selectedID: Int, defaultID: Int): StateListDrawable {
        val selectedDrawable = context.getDrawable(selectedID)
        val defaultDrawable = context.getDrawable(defaultID)
        return StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_selected), selectedDrawable)
            addState(intArrayOf(), defaultDrawable)
        }
    }
}