package com.yunzia.hyperstar.hook.tool

import android.content.res.Resources
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge

object ColorResourceReplacer {

    @Volatile
    private var isHooked = false // 使用 @Volatile 确保多线程可见性
    private val callbacks = mutableListOf<Any?.(Int) -> Int?>()

    /**
     * 注册一个新的颜色替换回调。
     * 如果尚未 Hook `Resources.getColor(int)` 方法，则进行 Hook。
     */
    @Synchronized
    fun registerColorReplacement(callback: Any?.(Int) -> Int?) {
        callbacks.add(callback)
        if (!isHooked) {
            hookGetColorMethod()
        }
    }

    /**
     * Hook `Resources.getColor(int)` 方法。
     * 确保只 Hook 一次。
     */
    private fun hookGetColorMethod() {
        synchronized(this) { // 双重检查避免重复 Hook
            if (isHooked) return
            XposedBridge.hookAllMethods(
                Resources::class.java,
                "getColor",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val resourceId = param.args[0] as Int
                        // 遍历所有回调函数，找到第一个匹配的结果
                        callbacks.firstNotNullOfOrNull { param.thisObject.it(resourceId) }?.let {
                            param.result = it
                        }
                    }
                }
            )
            isHooked = true
        }
    }
}