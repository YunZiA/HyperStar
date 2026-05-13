package com.yunzia.hyperstar.prefs

import android.content.SharedPreferences
import com.yunzia.hyperstar.hook.core.StarLog.log
import com.yunzia.hyperstar.hook.core.base.BaseXposedModule
import java.util.concurrent.ConcurrentHashMap

fun BaseXposedModule.loadPref(){
    XSPUtils.init(getRemotePreferences(SPUtils.PREFERENCE_NAME))
    log("RemotePreferences initialized")
}

object XSPUtils {
    private var pref: SharedPreferences? = null

    private val TAG = this.javaClass.simpleName

    private val cache = ConcurrentHashMap<String, Any?>()

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if (key != null) {
            val value = sharedPreferences.all[key]
            cache[key] = value
        }
    }

    /**
     * 初始化
     * @param pref XposedService
     */
    fun init(pref: SharedPreferences) {
        this.pref = pref
        cache.clear()
        cache.putAll(pref.all)
        pref.registerOnSharedPreferenceChangeListener(listener)
    }

    @Suppress("UNCHECKED_CAST")
    fun getBoolean(key: String, defValue: Boolean): Boolean {
        val cached = cache[key]
        return if (cached is Boolean) cached else pref?.getBoolean(key, defValue) ?: defValue
    }

    @Suppress("UNCHECKED_CAST")
    fun getInt(key: String, defValue: Int): Int {
        val cached = cache[key]
        return if (cached is Int) cached else pref?.getInt(key, defValue) ?: defValue
    }

    @Suppress("UNCHECKED_CAST")
    fun getString(key: String, defValue: String): String {
        val cached = cache[key]
        return if (cached is String) cached else pref?.getString(key, defValue) ?: defValue
    }

    @Suppress("UNCHECKED_CAST")
    fun getFloat(key: String, defValue: Float): Float {
        val cached = cache[key]
        return if (cached is Float) cached else pref?.getFloat(key, defValue) ?: defValue
    }

}