package com.yunzia.hyperstar.prefs.util

import android.content.SharedPreferences
import androidx.core.content.edit

abstract class PrefUtils {
    var pref: SharedPreferences? = null
    val TAG = this.javaClass.simpleName
    // === 字符串操作 ===
    fun putString(key: String, value: String) = pref?.edit { putString(key, value) }
    fun getString(key: String, defaultValue: String): String = pref?.getString(key, defaultValue) ?: defaultValue

    // === 整型操作 ===
    fun putInt(key: String, value: Int) = pref?.edit { putInt(key, value) }
    fun getInt(key: String, defaultValue: Int): Int = pref?.getInt(key, defaultValue) ?: defaultValue

    // === 长整型操作 ===
    fun putLong(key: String, value: Long) = pref?.edit { putLong(key, value) }
    fun getLong(key: String, defaultValue: Long): Long = pref?.getLong(key, defaultValue) ?: defaultValue

    // === Float 操作 ===
    fun putFloat(key: String, value: Float) = pref?.edit { putFloat(key, value) }
    fun getFloat(key: String, defaultValue: Float): Float = pref?.getFloat(key, defaultValue) ?: defaultValue

    // === Boolean 操作 ===
    fun putBoolean(key: String, value: Boolean) = pref?.edit { putBoolean(key, value) }
    fun getBoolean(key: String, defaultValue: Boolean): Boolean = pref?.getBoolean(key, defaultValue) ?: defaultValue

    // === 清除与移除 ===
    fun clearPreferences(): Boolean = pref?.edit()?.clear()?.commit() ?: false

    fun removePreferences(key: String): Boolean = pref?.edit()?.remove(key)?.commit() ?: false
}