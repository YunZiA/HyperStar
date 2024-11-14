package yunzia.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressLint("PrivateApi")
public class SystemProperties {
    public static final int PROP_NAME_MAX = 31;
    public static final int PROP_VALUE_MAX = 91;
    private static final String TAG = "SystemProperties";
    private static Class<?> classSystemProperties;
    private static boolean isSupportGet;
    private static boolean isSupportGetBoolean;
    private static boolean isSupportGetInt;
    private static boolean isSupportGetLong;
    private static boolean isSupportSet;

    static {
        try {
            classSystemProperties = Class.forName("android.os.SystemProperties");
        } catch (Exception e) {
            classSystemProperties = null;
        }
        if (classSystemProperties != null) {
            isSupportSet = true;
            try {
                classSystemProperties.getMethod("get", String.class, String.class);
                isSupportGet = true;
            } catch (Exception e2) {
                isSupportGet = false;
            }
            try {
                classSystemProperties.getMethod("getInt", String.class, Integer.TYPE);
                isSupportGetInt = true;
            } catch (Exception e3) {
                isSupportGetInt = false;
            }
            try {
                classSystemProperties.getMethod("getLong", String.class, Long.TYPE);
                isSupportGetLong = true;
            } catch (Exception e4) {
                isSupportGetLong = false;
            }
            try {
                classSystemProperties.getMethod("getBoolean", String.class, Boolean.TYPE);
                isSupportGetBoolean = true;
            } catch (Exception e5) {
                isSupportGetBoolean = false;
            }
            try {
                classSystemProperties.getMethod("set", String.class, String.class);
            } catch (Exception e6) {
                isSupportSet = false;
            }
        }
    }

    public static Object invoke(Object obj, Method method, Object... args) {
        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Method callMethod(String name, Class<?> parameterTypes) {
        return getMethod(classSystemProperties, name, String.class, parameterTypes);
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            Method method = clazz.getMethod(name, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(String key, String def) {
        if (isSupportGet) {
            try {
                return (String) invoke(null, callMethod("get", String.class), key, def);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "key: " + key + " detail:" + e.toString());
            }
        }
        return def;
    }

    public static String get(String str) {
        return get(str, "");
    }

    public static int getInt(String key, int def) {
        if (isSupportGetInt) {
            try {
                return ((Integer) invoke(null, callMethod("getInt", Integer.TYPE), key, Integer.valueOf(def))).intValue();
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "key: " + key + " detail:" + e.toString());
            }
        }
        return def;
    }

    public static long getLong(String key, long def) {
        if (isSupportGetLong) {
            try {
                return ((Long) invoke(null, callMethod("getLong", Long.TYPE), key, Long.valueOf(def))).longValue();
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "key: " + key + " detail:" + e.toString());
            }
        }
        return def;
    }

    public static boolean getBoolean(String key, boolean def) {
        if (isSupportGetBoolean) {
            try {
                return ((Boolean) invoke(null, callMethod("getBoolean", Long.TYPE), key, Boolean.valueOf(def))).booleanValue();
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "key: " + key + " detail:" + e.toString());
            }
        }
        return def;
    }

    public static void set(String name, String val) {
        if (isSupportSet) {
            if (name.length() > 31) {
                throw new IllegalArgumentException("key.length > 31");
            }
            if (val != null && val.length() > 91) {
                throw new IllegalArgumentException("val.length > 91");
            }
            invoke(null, callMethod("set", String.class), name, val);
        }
    }

    public static void set(String name, int val) {
        set(name, Integer.toString(val));
    }

    public static void set(String name, long val) {
        set(name, Long.toString(val));
    }

    public static void set(String name, boolean val) {
        set(name, Boolean.toString(val));
    }
}
