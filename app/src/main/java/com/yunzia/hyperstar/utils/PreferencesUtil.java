package com.yunzia.hyperstar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;


public class PreferencesUtil {
    public static String PREFERENCE_NAME = "HyperStar_pr";

    public static PreferencesUtil xsp;
    public SharedPreferences sp;

    private PreferencesUtil() {

    }

    public static synchronized PreferencesUtil getInstance() {
        if (xsp == null) {
            xsp = new PreferencesUtil();
        }
        return xsp;
    }

    /**
     * 初始化
     * @param context
     */
    public void init(Context context) {
        if (sp != null){
            Log.d("ggc", "PreferencesUtil init: sp is not null");
            return;
        }
        sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);

    }

    /*用户名的key值*/
    //public static String USERNAME = "username";

    /*存储字符串*/
    public static boolean putString(String key, String value) {
        SharedPreferences.Editor editor = PreferencesUtil.getInstance().sp.edit();
        editor.putString(key, value);
        return editor.commit();
    }
    /*读取字符串*/
    public static String getString(String key) {
        return getString(key, null);
    }
    /*读取字符串（带默认值的）*/
    public static String getString(String key, String defaultValue) {
        return PreferencesUtil.getInstance().sp.getString(key, defaultValue);
    }
    /*存储整型数字*/
    public static boolean putInt(String key, int value) {
        SharedPreferences preferences = PreferencesUtil.getInstance().sp;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        return editor.commit();
    }
    /*读取整型数字*/
    public static int getInt(String key) {
        return getInt(key, -1);
    }
    /**读取整型数字（带默认值的）*/
    public static int getInt(String key, int defaultValue) {
        SharedPreferences preferences = PreferencesUtil.getInstance().sp;
        if (preferences == null){
            return defaultValue;
        }
        return preferences.getInt(key, defaultValue);
    }
    /*存储长整型数字*/
    public static boolean putLong(String key, long value) {
        SharedPreferences preferences = PreferencesUtil.getInstance().sp;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        return editor.commit();
    }
    /**读取长整型数字*/
    public static long getLong(String key) {
        return getLong(key, 0xffffffff);
    }
    /**读取长整型数字（带默认值的）*/
    public static long getLong(String key, long defaultValue) {
        SharedPreferences preferences = PreferencesUtil.getInstance().sp;
        return preferences.getLong(key, defaultValue);
    }
    /**
     * 存储Float数字
     */
    public static void putFloat(String key, float value) {
        SharedPreferences preferences = PreferencesUtil.getInstance().sp;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }
    /**读取Float数字*/
    public static float getFloat(String key) {
        return getFloat( key, -1.0f);
    }
    /**读取Float数字（带默认值的）*/
    public static float getFloat(String key, float defaultValue) {
        SharedPreferences preferences = PreferencesUtil.getInstance().sp;
        return preferences.getFloat(key, defaultValue);
    }
    /**存储boolean类型数据*/
    public static boolean putBoolean(String key, boolean value) {
        SharedPreferences preferences = PreferencesUtil.getInstance().sp;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }
    /**读取boolean类型数据*/
    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }
    /**读取boolean类型数据（带默认值的）*/
    public static boolean getBoolean(String key, boolean defaultValue) {
        SharedPreferences preferences = PreferencesUtil.getInstance().sp;
        return preferences.getBoolean(key, defaultValue);
    }
    /**清除数据*/
    public static boolean clearPreferences() {
        SharedPreferences pref = PreferencesUtil.getInstance().sp;
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        return editor.commit();
    }

    public static void getAllPreferences() {
        SharedPreferences pref = PreferencesUtil.getInstance().sp;
        Map<String, ?> allEntries = pref.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String) {
                String stringValue = (String) value;
                Log.d("SharedPref", "Key: " + key + ", Value: " + stringValue);
            }
        }
    }
}



