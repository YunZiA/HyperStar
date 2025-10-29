package com.yunzia.hyperstar.prefs

import android.util.Log
import io.github.libxposed.service.XposedService
import java.util.*
import com.yunzia.hyperstar.prefs.util.PrefUtils
import com.yunzia.hyperstar.prefs.util.SP

object SPUtils : PrefUtils()  {
    const val PREFERENCE_NAME = "HyperStar_SP"

    /**
     * 初始化
     * @param service XposedService
     */
    fun init(service: XposedService?) {
        if (service == null){
            Log.d(TAG, "init: service is null")
            return
        }
        pref =  service.getRemotePreferences(PREFERENCE_NAME)
        Log.d(TAG, "initialized")
    }

    fun getAllPreferences(sputils: ArrayList<SP>) {
        val allEntries = pref?.all ?: return
        allEntries.entries.stream()
            .filter { entry -> entry.key != "is_Hook_Channel" }
            .forEach { entry ->
                val key = entry.key
                var value = entry.value ?: return@forEach
                val sp_type: Int = when (value) {
                    is String -> SP.type_string
                    is Int -> SP.type_int
                    is Boolean -> SP.type_boolean
                    is Float -> SP.type_float
                    is Long -> SP.type_long
                    else -> {
                        value.toString().also { value = it }
                        SP.type_string
                    }
                }

                sputils.add(SP(TAG, key, sp_type, value))
            }
    }


}