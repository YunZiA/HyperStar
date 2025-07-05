package com.yunzia.hyperstar.hook.app.screenshot

import android.content.ContentResolver
import android.provider.Settings
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.base.replaceHookMethod
import com.yunzia.hyperstar.utils.XSPUtils

class EnableClipboardWriteOnScreenshot : Hooker() {

    val isEnable = XSPUtils.getBoolean("enable_clipboard_write_on_screenshot",false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

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