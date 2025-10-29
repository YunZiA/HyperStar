package com.yunzia.hyperstar.prefs

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.yunzia.hyperstar.prefs.util.PrefUtils
import com.yunzia.hyperstar.prefs.util.SP
import java.util.*

object PreferencesUtil : PrefUtils() {
    private const val PREFERENCE_NAME = "HyperStar_pr"
    private var sp: SharedPreferences? = null

    /**
     * 初始化
     * @param service XposedService 实例
     */
    fun init(context: Context?) {
        if (context == null){
            Log.d(TAG, "init: context is null")
            return

        }
        pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        Log.d(TAG, "initialized")
    }

    // === 获取所有配置项 ===
    fun getAllPreferences(preferencesList: ArrayList<SP>) {
        val allEntries = sp?.all ?: return

        val type = this.javaClass.simpleName

        allEntries.entries.stream()
            .filter { entry -> entry.key != "is_first_use" }
            .filter { entry -> entry.key != "isFold" }
            .filter { entry -> entry.key != "isPad" }
            .forEach { entry ->
                val key = entry.key
                val value = entry.value
                Log.d(type, "Key: $key, Value: $value")

                when (value) {
                    is String -> preferencesList.add(SP(type, key, SP.type_string, value))
                    is Int -> preferencesList.add(SP(type, key, SP.type_int, value))
                    is Boolean -> preferencesList.add(SP(type, key, SP.type_boolean, value))
                    is Float -> preferencesList.add(SP(type, key, SP.type_float, value))
                    is Long -> preferencesList.add(SP(type, key, SP.type_long, value))
                    else -> preferencesList.add(SP(type, key, SP.type_string, value.toString()))
                }
            }
    }
}