package com.yunzia.hyperstar.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;


public class SPUtils {

    public static SPUtils xsp;
    public SharedPreferences sp;

    private SPUtils() {

    }

    public static synchronized SPUtils getInstance() {
        if (xsp == null) {
            xsp = new SPUtils();
        }
        return xsp;
    }

    /**
     * 初始化
     * @param context
     */
    public void init(Context context) {
        sp = context.getSharedPreferences("HyperStar_SP", Context.MODE_WORLD_READABLE);

    }

    /**
     * 下面的是读取数据
     * @param key
     * @param def
     * @return
     */
    public static String getString(String key, String def) {
        if(SPUtils.getInstance().sp == null){
            return def;
        }
        return SPUtils.getInstance().sp.getString(key, def);
    }

    public static int getInt(String key, int def) {
        if(SPUtils.getInstance().sp == null){
            return def;
        }
        Log.d("ggc", "getInt: "+SPUtils.getInstance().sp.getInt(key, def));
        return SPUtils.getInstance().sp.getInt(key, def);
    }

    public static float getFloat(String key, float def) {
        if(SPUtils.getInstance().sp == null){
            return def;
        }
        return SPUtils.getInstance().sp.getFloat(key, def);
    }

    public static long getLong(String key, long def) {
        if(SPUtils.getInstance().sp == null){
            return def;
        }
        return SPUtils.getInstance().sp.getLong(key, def);
    }
    public static boolean getBoolean(String key, boolean def) {
        if(SPUtils.getInstance().sp == null){
            return def;
        }
        return SPUtils.getInstance().sp.getBoolean(key, def);
    }

    /**
     * 下面是保存数据
     * @param key
     * @param v
     * @return
     */
    public static boolean setString(String key, String v) {
        if(SPUtils.getInstance().sp == null){
            return false;
        }
        return SPUtils.getInstance().sp.edit().putString(key, v).commit();
    }

    public static boolean setInt(String key, int v) {
        if(SPUtils.getInstance().sp == null){
            return false;
        }
        return SPUtils.getInstance().sp.edit().putInt(key, v).commit();
    }

    public static boolean setBoolean(String key, boolean v) {
        if(SPUtils.getInstance().sp == null){
            return false;
        }
        return SPUtils.getInstance().sp.edit().putBoolean(key, v).commit();
    }
    public static boolean setFloat(String key, float v) {
        if(SPUtils.getInstance().sp == null){
            return false;
        }
        return SPUtils.getInstance().sp.edit().putFloat(key, v).commit();
    }

    public static boolean setLong(String key, long v) {
        if(SPUtils.getInstance().sp == null){
            return false;
        }
        return SPUtils.getInstance().sp.edit().putLong(key, v).commit();
    }

    public static boolean clearPreferences() {
        SharedPreferences pref = SPUtils.getInstance().sp;
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        return editor.commit();
    }

    public static void getAllPreferences(ArrayList<SP> sputils) {
        SharedPreferences pref = SPUtils.getInstance().sp;
        Map<String, ?> allEntries = pref.getAll();
        Log.d("SPUtils", "Key: " + allEntries);

        String type  = "SPUtils";
        //ArrayList<SP> sputils = new ArrayList<>();
        //JSONObject json = new JSONObject();

        allEntries.entrySet().stream()
                .filter(entry -> !"is_Hook_Channel".equals(entry.getKey()))
                .forEach(entry -> {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value == null) {
                        // 处理null值的情况，如果需要的话
                        return;
                    }
                    int sp_type;
                    if (value instanceof String) {
                        sp_type = SP.type_string;
                    } else if (value instanceof Integer) {
                        sp_type = SP.type_int;
                    } else if (value instanceof Boolean) {
                        sp_type = SP.type_boolean;
                    } else if (value instanceof Float) {
                        sp_type = SP.type_float;
                    } else if (value instanceof Long) {
                        sp_type = SP.type_long;
                    } else {
                        sp_type = SP.type_string; // 默认作为字符串处理
                        value = value.toString(); // 确保值为字符串类型
                    }
                    sputils.add(new SP(type, key, sp_type, value));
                });

//        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//            String key = entry.getKey();
//            Object value = entry.getValue();
//            if (Objects.equals(key, "is_Hook_Channel")){
//                continue;
//            }
//            if (value instanceof String) {
//                sputils.add(new SP(type,key,SP.type_string,value));
//            } else if (value instanceof Integer) {
//                sputils.add(new SP(type,key,SP.type_int,value));
//            } else if (value instanceof Boolean) {
//                sputils.add(new SP(type,key,SP.type_boolean,value));
//            } else if (value instanceof Float) {
//                sputils.add(new SP(type,key,SP.type_float,value));
//            } else if (value instanceof Long) {
//                sputils.add(new SP(type,key,SP.type_long,value));
//            } else {
//                sputils.add(new SP(type,key,SP.type_string,value));
//            }
//        }
        //return gson.toJsonTree(sputils,new TypeToken<ArrayList<SP>>() {}.getType());

    }


}


