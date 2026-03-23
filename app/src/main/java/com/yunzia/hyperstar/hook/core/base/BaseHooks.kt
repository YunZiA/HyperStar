package com.yunzia.hyperstar.hook.core.base

import com.yunzia.hyperstar.hook.core.StarLog.logE

abstract class BaseHooks: BaseHook() {
    fun initHooks(vararg hooks: BaseHook) {
        for (h in hooks) {
            try {
                if (h.isInit) continue
                h.init()
                h.isInit = true
            } catch (e: Exception) {
                logE("Failed to initialize hook: ${h.className}\n$e")
            }
        }
    }
}