package com.yunzia.hyperstar.hook.app.mms

import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.PersistableBundle
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.utils.XSPUtils


class AutoCopyVerificationCode: Hooker() {

    val autoCopyVerificationCode = XSPUtils.getBoolean("auto_copy_verification_code",false)


    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        if (!autoCopyVerificationCode) return
//        findClass(
//            "com.android.mms.transaction.i\$c",
//            classLoader
//        ).afterHookMethod(
//            "a",
//            Context::class.java,
//            Boolean::class.java,
//            Boolean::class.java,
//        ){
//            val itemExtra = this.getObjectField("l")?:return@afterHookMethod
//            val code = itemExtra.callMethod("getOTP") as String
//            val context = it.args[0] as Context
//            context.copyClipboard(code)
//            starLog.log("getOTP:$code")
//
//        }


        PendingIntent::class.java.afterHookMethod(
            "getActivity",
            Context::class.java,
            Int::class.java,
            Intent::class.java,
            Int::class.java
        ){
            val intent = it.args[2] as Intent
            val extraText = intent.getStringExtra("extra_text")
            if (extraText != null) {
                val context = it.args[0] as Context
                context.copyVerificationCodeToClipboard(extraText)
                logD("New verification code: $extraText")

            }
        }


    }


    private fun Context.copyVerificationCodeToClipboard(text: CharSequence) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            ?: return
        val extras = PersistableBundle().apply {
            putBoolean("mms_is_ververification_code", true)
        }
        val clip = ClipData.newPlainText(null, text).apply {
            description.extras = extras
        }
        clipboard.setPrimaryClip(clip)
    }

}