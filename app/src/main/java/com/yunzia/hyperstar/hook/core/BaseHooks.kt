package com.yunzia.hyperstar.hook.core

import com.yunzia.hyperstar.hook.core.Log.logE
import io.github.kyuubiran.ezxhelper.xposed.EzXposed

abstract class BaseHooks: BaseHook() {
    fun initHooks(vararg hooks: BaseHook) {
        for (h in hooks) {
            try {
                if (h.isInit) continue
                h.init()
                h.isInit = true
            } catch (e: Exception) {
                logE("Failed to initialize hook: ${h.className}", e)
            }
        }
    }
}