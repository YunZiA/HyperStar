package com.yunzia.hyperstar.hook.app.screenshot

import android.content.ContentResolver
import android.provider.Settings
import com.yunzia.hyperstar.hook.core.BaseHook
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.prefs.XSPUtils

object EnableClipboardWriteOnScreenshot : BaseHook() {

    val isEnable = XSPUtils.getBoolean("enable_clipboard_write_on_screenshot",false)

    override fun init() {
        

        if (!isEnable) return

        Settings.Secure::class.java.afterHookMethod(
            "getInt",
            ContentResolver::class.java,
            String::class.java,
            Int::class.java
        ){
            if (it.args[1] == "mi_screen_shots_write_clipboard_enable"){
                it.result = 1
            }

        }

    }


}