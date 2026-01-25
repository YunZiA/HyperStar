package com.yunzia.hyperstar.hook.app.mms

import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import com.yunzia.hyperstar.hook.core.BaseHook
import com.yunzia.hyperstar.hook.core.Log.logD
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.prefs.XSPUtils


object AutoCopyVerificationCode: BaseHook() {
    val autoCopyVerificationCode = XSPUtils.getBoolean("auto_copy_verification_code",false)

    override fun init() {
        
        if (!autoCopyVerificationCode) return
        PendingIntent::class.java.apply {
            afterHookMethod(
                "getActivity",
                Context::class.java,
                Int::class.java,
                Intent::class.java,
                Int::class.java
            ){
                val intent = it.args[2] as Intent
                val extraText = intent.getStringExtra("extra_text") ?: return@afterHookMethod
                val context = it.args[0] as Context
                context.copyVerificationCodeToClipboard(extraText)
                logD("New verification code: $extraText")
            }
            afterHookMethod(
                "getActivity",
                Context::class.java,
                Int::class.java,
                Intent::class.java,
                Int::class.java,
                Bundle::class.java
            ){
                val intent = it.args[2] as Intent
                val extraText = intent.getStringExtra("extra_text") ?: return@afterHookMethod
                val context = it.args[0] as Context
                context.copyVerificationCodeToClipboard(extraText)
                logD("New verification code: $extraText")
            }
        }
    }


    private fun Context.copyVerificationCodeToClipboard(text: CharSequence) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return
        val extras = PersistableBundle().apply {
            putBoolean("mms_is_ververification_code", true)
        }
        val clip = ClipData.newPlainText(null, text).apply {
            description.extras = extras
        }
        clipboard.setPrimaryClip(clip)
    }

}