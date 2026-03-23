package com.yunzia.hyperstar.prefs

import android.content.SharedPreferences
import com.yunzia.hyperstar.hook.core.StarLog.log
import com.yunzia.hyperstar.hook.core.base.BaseXposedModule

fun BaseXposedModule.loadPref(){
    XSPUtils.init(getRemotePreferences(SPUtils.PREFERENCE_NAME))
    log("RemotePreferences initialized")
}

object XSPUtils {
    private var pref: SharedPreferences? = null

    private val TAG = this.javaClass.simpleName

    /**
     * 初始化
     * @param pref XposedService
     */
    fun init(pref: SharedPreferences) {
        this.pref = pref
//        XSPUtils.pref?.registerOnSharedPreferenceChangeListener { pref, key ->
//            val value = pref.getBoolean(key, true)
//        }
    }

    fun getBoolean(key: String, defValue: Boolean) = pref?.getBoolean(key, defValue) ?: defValue
    fun getInt(key: String, defValue: Int) = pref?.getInt(key, defValue) ?: defValue
    fun getString(key: String, defValue: String) = pref?.getString(key, defValue) ?: defValue
    fun getFloat(key: String, defValue: Float) = pref?.getFloat(key, defValue) ?: defValue

}