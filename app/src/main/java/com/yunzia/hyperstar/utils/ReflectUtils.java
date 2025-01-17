package com.yunzia.hyperstar.utils;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ReflectUtils {
    private static final String TAG = "ReflectUtils";
    private static final Map<String, Method> sMethodCache = new HashMap();
    private static final Map<String, Field> sFieldCache = new HashMap();
    private static final Class<?>[] PRIMITIVE_CLASSES = {Boolean.TYPE, Byte.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Void.TYPE};
    private static final String[] SIGNATURE_OF_PRIMTIVE_CLASSES = {"Z", "B", "C", "S", "I", "J", "F", "D", "V"};

    private ReflectUtils() {
    }

    public static boolean hasMethod(Class<? extends Object> cls, String str, Class<?>[] clsArr) {
        try {
            cls.getDeclaredMethod(str, clsArr);
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public static <T> T callObjectMethod(Object obj, Class<T> cls, String str, Class<?>[] clsArr, Object... objArr) {
        try {
            Method declaredMethod = obj.getClass().getDeclaredMethod(str, clsArr);
            declaredMethod.setAccessible(true);
            return (T) declaredMethod.invoke(obj, objArr);
        } catch (Exception e) {
            Log.d(TAG, "callObjectMethod", e);
            return null;
        }
    }

    public static <T> T callObjectMethod2(Object obj, Class<T> cls, String str, Class<?>[] clsArr, Object... objArr) {
        try {
            Method method = obj.getClass().getMethod(str, clsArr);
            method.setAccessible(true);
            return (T) method.invoke(obj, objArr);
        } catch (Exception e) {
            Log.e(TAG, "callObjectMethod2", e);
            return null;
        }
    }

    public static <T> T callStaticMethod(Class<?> cls, Class<T> cls2, String str, Class<?>[] clsArr, Object... objArr) {
        return (T) invokeObject(cls, null, str, cls2, clsArr, objArr);
    }

    public static <T> T callStaticMethod(String str, Class<T> cls, String str2, Class<?>[] clsArr, Object... objArr) {
        try {
            return (T) callStaticMethod(Class.forName(str), cls, str2, clsArr, objArr);
        } catch (Exception e) {
            Log.d(TAG, "callStaticMethod", e);
            return null;
        }
    }

    public static <T> T createNewInstance(Class<T> cls, Class<?>[] clsArr, Object... objArr) {
        try {
            return cls.getConstructor(clsArr).newInstance(objArr);
        } catch (Exception e) {
            Log.d(TAG, "createNewInstance", e);
            return null;
        }
    }

    public static void invoke(Class<?> cls, Object obj, String str, Class<?> cls2, Class<?>[] clsArr, Object... objArr) {
        try {
            Method method = getMethod(cls, str, getMethodSignature(cls2, clsArr), clsArr);
            if (method != null) {
                method.invoke(obj, objArr);
            }
        } catch (Exception e) {
            Log.e(TAG, "invoke", e);
        }
    }

    public static <T> T invokeObject(Class<?> cls, Object obj, String str, Class<?> cls2, Class<?>[] clsArr, Object... objArr) {
        try {
            Method method = getMethod(cls, str, getMethodSignature(cls2, clsArr), clsArr);
            if (method != null) {
                return (T) method.invoke(obj, objArr);
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "invokeObject", e);
            return null;
        }
    }

    public static Object getObjectField(Object obj, String str) {
        try {
            Field declaredField = obj.getClass().getDeclaredField(str);
            declaredField.setAccessible(true);
            return declaredField.get(obj);
        } catch (Exception e) {
            Log.e(TAG, "getObjectField", e);
            return null;
        }
    }

    public static Field getField(Class<?> cls, String str, String str2) {
        try {
            String generateFieldCacheKey = generateFieldCacheKey(cls, str, str2);
            Field field = sFieldCache.get(generateFieldCacheKey);
            if (field == null) {
                while (true) {
                    if (cls == null) {
                        break;
                    }
                    try {
                        field = cls.getDeclaredField(str);
                    } catch (Throwable unused) {
                    }
                    if (field != null) {
                        field.setAccessible(true);
                        break;
                    }
                    cls = cls.getSuperclass();
                }
                sFieldCache.put(generateFieldCacheKey, field);
            }
            return field;
        } catch (Exception e) {
            Log.e(TAG, "getField", e);
            return null;
        }
    }

    public static Field getField(Class<?> cls, String str, Class<?> cls2) {
        return getField(cls, str, getSignature(cls2));
    }

    public static <T> T getStaticFieldValue(Class<?> cls, String str, Class<?> cls2) {
        try {
            return (T) getField(cls, str, cls2).get(null);
        } catch (Exception e) {
            Log.d(TAG, "getStaticFieldValue", e);
            //Log.d(TAG, "getStaticFieldValue "+str +" is not find");
            return null;
        }
    }
    public static <T> T getStaticFieldValue(Class<?> cls, String str, Class<?> cls2,T value) {
        try {
            return (T) getField(cls, str, cls2).get(null);
        } catch (Exception e) {
            //Log.d(TAG, "getStaticFieldValue", e);
            Log.d(TAG, "getStaticFieldValue "+str +" is not find");
            return value;
        }
    }

    public static <T> T getFieldValue(Class<?> cls, Object obj, String str, String str2) {
        Field field = getField(cls, str, str2);
        if (field == null) {
            return null;
        }
        try {
            return (T) field.get(obj);
        } catch (IllegalAccessException e) {
            Log.d(TAG, "getFieldValue", e);
            return null;
        }
    }

    public static void setValue(Object obj, String str, Class cls, Object obj2) {
        try {
            Field field = getField(obj.getClass(), str, (Class<?>) cls);
            if (cls == Integer.TYPE) {
                field.set(obj, Integer.valueOf(((Integer) obj2).intValue()));
            } else if (cls == Float.TYPE) {
                field.set(obj, Float.valueOf(((Float) obj2).floatValue()));
            } else if (cls == Long.TYPE) {
                field.set(obj, Long.valueOf(((Long) obj2).longValue()));
            } else if (cls == Boolean.TYPE) {
                field.set(obj, Boolean.valueOf(((Boolean) obj2).booleanValue()));
            } else if (cls == Double.TYPE) {
                field.set(obj, Double.valueOf(((Double) obj2).doubleValue()));
            } else if (cls == Character.TYPE) {
                field.set(obj, Character.valueOf(((Character) obj2).charValue()));
            } else if (cls == Byte.TYPE) {
                field.set(obj, Byte.valueOf(((Byte) obj2).byteValue()));
            } else if (cls == Short.TYPE) {
                field.set(obj, Short.valueOf(((Short) obj2).shortValue()));
            } else {
                field.set(obj, cls.cast(obj2));
            }
        } catch (Exception e) {
            Log.d(TAG, "setValue", e);
        }
    }

    public static Method getMethod(Class<?> cls, String str, String str2, Class<?>... clsArr) {
        try {
            String generateMethodCacheKey = generateMethodCacheKey(cls, str, str2);
            Method method = sMethodCache.get(generateMethodCacheKey);
            if (method != null) {
                return method;
            }
            Method method2 = cls.getMethod(str, clsArr);
            sMethodCache.put(generateMethodCacheKey, method2);
            return method2;
        } catch (Exception e) {
            Log.d(TAG, "getMethod", e);
            return null;
        }
    }

    private static String generateMethodCacheKey(Class<?> cls, String str, String str2) {
        return cls.toString() + "/" + str + "/" + str2;
    }

    private static String generateFieldCacheKey(Class<?> cls, String str, String str2) {
        return cls.toString() + "/" + str + "/" + str2;
    }

    public static String getMethodSignature(Class<?> cls, Class<?>... clsArr) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        if (clsArr != null) {
            for (Class<?> cls2 : clsArr) {
                sb.append(getSignature(cls2));
            }
        }
        sb.append(')');
        sb.append(getSignature(cls));
        return sb.toString();
    }

    public static void setDeclaredBooleanField(Class<?> cls,String fieldName, boolean bool) {
        try {
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.setBoolean(cls, bool);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static String getSignature(Class<?> cls) {
        int i = 0;
        while (true) {
            Class<?>[] clsArr = PRIMITIVE_CLASSES;
            if (i < clsArr.length) {
                if (cls == clsArr[i]) {
                    return SIGNATURE_OF_PRIMTIVE_CLASSES[i];
                }
                i++;
            } else {
                return getSignature(cls.getName());
            }
        }
    }

    public static String getSignature(String str) {
        int i = 0;
        while (true) {
            Class<?>[] clsArr = PRIMITIVE_CLASSES;
            if (i >= clsArr.length) {
                break;
            }
            if (clsArr[i].getName().equals(str)) {
                str = SIGNATURE_OF_PRIMTIVE_CLASSES[i];
            }
            i++;
        }
        String replace = str.replace(".", "/");
        if (replace.startsWith("[")) {
            return replace;
        }
        return "L" + replace + ";";
    }

    public static Class<?> getClass(String str) {
        try {
            return Class.forName(str);
        } catch (Exception e) {
            Log.e(TAG, "getClass", e);
            return null;
        }
    }
}
