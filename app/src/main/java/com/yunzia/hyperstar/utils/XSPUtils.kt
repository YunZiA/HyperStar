package com.yunzia.hyperstar.utils

import com.yunzia.hyperstar.BuildConfig
import de.robv.android.xposed.XSharedPreferences

object XSPUtils {
    private var prefs: XSharedPreferences? = null

    // 私有方法来获取或创建XSharedPreferences实例
    private fun getPrefs(): XSharedPreferences {
        if (prefs == null || prefs?.hasFileChanged() == true) {
            // 假设XSharedPreferences有一个合适的单例或创建逻辑
            // 这里我们简单地重新创建它，但在实际应用中可能需要更复杂的逻辑
            synchronized(this) {
                if (prefs == null || prefs?.hasFileChanged() == true) {
                    prefs = XSharedPreferences(BuildConfig.APPLICATION_ID, "HyperStar_SP")
                    prefs!!.makeWorldReadable()
                    if (prefs?.hasFileChanged() == true) {
                        prefs?.reload()
                    }
                }
            }
        }
        return prefs!!
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return getPrefs().getBoolean(key, defValue)
    }

    fun getInt(key: String, defValue: Int): Int {
        return getPrefs().getInt(key, defValue)
    }

    fun getString(key: String, defValue: String): String? {
        return getPrefs().getString(key, defValue)
    }

    fun getFloat(key: String, defValue: Float): Float {
        return getPrefs().getFloat(key, defValue)
    }
}
