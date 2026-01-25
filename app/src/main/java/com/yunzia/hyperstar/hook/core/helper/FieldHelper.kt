package com.yunzia.hyperstar.hook.core.helper

import com.yunzia.hyperstar.hook.base.runCatchingOrNull
import com.yunzia.hyperstar.hook.core.Log.logE
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap

object FieldHelper {
    private val fieldCache = ConcurrentHashMap<String, Field>()

    // 假设 findField 已存在（返回 Field 并 setAccessible(true)）
    /**
     * 查找字段（精确名称），找不到则抛 NoSuchFieldError。
     */
    fun findField(clazz: Class<*>, fieldName: String): Field? {
        val fullFieldName = "${clazz.name}#$fieldName"

        // 检查缓存
        fieldCache[fullFieldName]?.let { cached ->
            return cached // 可能是 Field 或 null（表示已知不存在）
        }

        // 未缓存，尝试查找
        return try {
            val field = findFieldRecursiveImpl(clazz, fieldName).apply {
                isAccessible = true
            }
            fieldCache[fullFieldName] = field
            field
        } catch (e: NoSuchFieldException) {
            logE("Field not found: '$fieldName' in class '${clazz.name}'", e)
            null
        }
    }

    /**
     * 递归查找字段：从 clazz 开始，向上遍历继承链（不含 Object）。
     */
    @Throws(NoSuchFieldException::class)
    private fun findFieldRecursiveImpl(clazz: Class<*>, fieldName: String): Field {
        // 先尝试当前类
        try {
            return clazz.getDeclaredField(fieldName)
        } catch (ignored: NoSuchFieldException) {
            // 继续向上找父类
        }

        var current = clazz.superclass
        while (current != null && current != Any::class.java) { // Any::class.java == Object.class
            try {
                return current.getDeclaredField(fieldName)
            } catch (ignored: NoSuchFieldException) {
                current = current.superclass
            }
        }

        throw NoSuchFieldException("$clazz#$fieldName")
    }

    /**
     * 查找第一个类型完全匹配的字段（递归父类），找不到抛 NoSuchFieldError。
     */
    fun findFirstFieldByExactType(clazz: Class<*>, type: Class<*>): Field {
        var current: Class<*>? = clazz
        while (current != null) {
            for (field in current.declaredFields) {
                if (field.type == type) {
                    field.isAccessible = true
                    return field
                }
            }
            current = current.superclass
        }
        throw NoSuchFieldError("Field of type ${type.name} in class ${clazz.name}")
    }

    @Suppress("UNCHECKED_CAST")
    internal inline fun <reified T> getObjectField(obj: Any, fieldName: String): T {
        return getField(obj::class.java, fieldName).get(obj) as T
    }

    fun getBooleanField(obj: Any, fieldName: String): Boolean =
        getField(obj::class.java, fieldName).getBoolean(obj)

    fun getByteField(obj: Any, fieldName: String): Byte =
        getField(obj::class.java, fieldName).getByte(obj)

    fun getCharField(obj: Any, fieldName: String): Char =
        getField(obj::class.java, fieldName).getChar(obj)

    fun getDoubleField(obj: Any, fieldName: String): Double =
        getField(obj::class.java, fieldName).getDouble(obj)

    fun getFloatField(obj: Any, fieldName: String): Float =
        getField(obj::class.java, fieldName).getFloat(obj)

    fun getIntField(obj: Any, fieldName: String): Int =
        getField(obj::class.java, fieldName).getInt(obj)

    fun getLongField(obj: Any, fieldName: String): Long =
        getField(obj::class.java, fieldName).getLong(obj)

    fun getShortField(obj: Any, fieldName: String): Short =
        getField(obj::class.java, fieldName).getShort(obj)

    // Helper to reduce duplication
    private fun getField(clazz: Class<*>, fieldName: String): Field {
        return findField(clazz, fieldName)?.apply {
            try {
                isAccessible = true
            } catch (e: IllegalAccessException) {
                // 可选：记录日志
                logE("Failed to set field '$fieldName' in ${clazz.name} accessible", e)
                // 抛出运行时异常，避免强制 checked exception
                throw RuntimeException("Cannot access field '$fieldName' in ${clazz.simpleName}", e)
            }
        } ?: throw NoSuchFieldError("Field '$fieldName' not found in ${clazz.simpleName}")
    }

    // Setters
    internal inline  fun <reified T> setObjectField(obj: Any, fieldName: String, value: T) {
        setFieldValue(obj, null, fieldName, value)
    }

    fun setBooleanField(obj: Any, fieldName: String, value: Boolean) = setFieldValue(obj, null, fieldName, value)

    fun setByteField(obj: Any, fieldName: String, value: Byte) =
        setFieldValue(obj, null, fieldName, value)

    fun setCharField(obj: Any, fieldName: String, value: Char) =
        setFieldValue(obj, null, fieldName, value)

    fun setDoubleField(obj: Any, fieldName: String, value: Double) =
        setFieldValue(obj, null, fieldName, value)

    fun setFloatField(obj: Any, fieldName: String, value: Float) =
        setFieldValue(obj, null, fieldName, value)

    fun setIntField(obj: Any, fieldName: String, value: Int) =
        setFieldValue(obj, null, fieldName, value)

    fun setLongField(obj: Any, fieldName: String, value: Long) =
        setFieldValue(obj, null, fieldName, value)

    fun setShortField(obj: Any, fieldName: String, value: Short) =
        setFieldValue(obj, null, fieldName, value)

// endregion

// region === Static Field Getters / Setters ===

    @Suppress("UNCHECKED_CAST")
    internal inline fun <reified T> getStaticObjectField(clazz: Class<*>, fieldName: String): T? {
        return getField(clazz, fieldName).get(null) as T?
    }

    fun getStaticBooleanField(clazz: Class<*>, fieldName: String): Boolean =
        getField(clazz, fieldName).getBoolean(null)

    fun getStaticByteField(clazz: Class<*>, fieldName: String): Byte =
        getField(clazz, fieldName).getByte(null)

    fun getStaticCharField(clazz: Class<*>, fieldName: String): Char =
        getField(clazz, fieldName).getChar(null)

    fun getStaticDoubleField(clazz: Class<*>, fieldName: String): Double =
        getField(clazz, fieldName).getDouble(null)

    fun getStaticFloatField(clazz: Class<*>, fieldName: String): Float =
        getField(clazz, fieldName).getFloat(null)

    fun getStaticIntField(clazz: Class<*>, fieldName: String): Int =
        getField(clazz, fieldName).getInt(null)

    fun getStaticLongField(clazz: Class<*>, fieldName: String): Long =
        getField(clazz, fieldName).getLong(null)

    fun getStaticShortField(clazz: Class<*>, fieldName: String): Short =
        getField(clazz, fieldName).getShort(null)

    // Setters
    internal inline fun <reified T> setStaticObjectField(clazz: Class<*>, fieldName: String, value: T) {
        setFieldValue(null, clazz, fieldName, value)
    }

    fun setStaticBooleanField(clazz: Class<*>, fieldName: String, value: Boolean) =
        setFieldValue(null, clazz, fieldName, value)

    fun setStaticByteField(clazz: Class<*>, fieldName: String, value: Byte) =
        setFieldValue(null, clazz, fieldName, value)

    fun setStaticCharField(clazz: Class<*>, fieldName: String, value: Char) =
        setFieldValue(null, clazz, fieldName, value)

    fun setStaticDoubleField(clazz: Class<*>, fieldName: String, value: Double) =
        setFieldValue(null, clazz, fieldName, value)

    fun setStaticFloatField(clazz: Class<*>, fieldName: String, value: Float) =
        setFieldValue(null, clazz, fieldName, value)

    fun setStaticIntField(clazz: Class<*>, fieldName: String, value: Int) =
        setFieldValue(null, clazz, fieldName, value)

    fun setStaticLongField(clazz: Class<*>, fieldName: String, value: Long) =
        setFieldValue(null, clazz, fieldName, value)

    fun setStaticShortField(clazz: Class<*>, fieldName: String, value: Short) =
        setFieldValue(null, clazz, fieldName, value)

// endregion

// region === Unified Field Access Helper ===

    private fun setFieldValue(
        obj: Any?,
        clazz: Class<*>?,
        fieldName: String,
        value: Any?
    ) {
        val targetClass = clazz ?: obj!!::class.java
        try {
            findField(targetClass, fieldName)?.apply {
                isAccessible = true
                when (value) {
                    is Boolean -> setBoolean(obj, value)
                    is Byte -> setByte(obj, value)
                    is Char -> setChar(obj, value)
                    is Double -> setDouble(obj, value)
                    is Float -> setFloat(obj, value)
                    is Int -> setInt(obj, value)
                    is Long -> setLong(obj, value)
                    is Short -> setShort(obj, value)
                    else -> set(obj, value)
                }
            }
        } catch (e: IllegalAccessException) {
            logE("Field access error: ${e.message}", e)
            throw IllegalAccessError(e.message)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

}


fun Any?.setIntField(fieldName: String,value: Int) = this?.let { FieldHelper.setIntField(this, fieldName, value) }
fun Any?.getIntField(fieldName: String) = this?.let { FieldHelper.getIntField(this, fieldName) }

fun Any?.setFloatField(fieldName: String,value: Float) = this?.let { FieldHelper.setFloatField(this, fieldName,value) }
fun Any?.getFloatField(fieldName: String) = this?.let { FieldHelper.getFloatField(this, fieldName) }

fun Any?.setLongField(fieldName: String,value: Long) = this?.let { FieldHelper.setLongField(this, fieldName, value) }
fun Any?.getLongField(fieldName: String) = this?.let { FieldHelper.getLongField(this, fieldName) }

fun Any?.getStringField(fieldName: String) = this?.let { FieldHelper.getObjectField(this, fieldName) as String }

fun Any?.getBooleanField(fieldName: String) = this?.let { FieldHelper.getBooleanField(this, fieldName) }

fun Any?.setObjectField(fieldName: String, value: Any?) = this?.let { FieldHelper.setObjectField(this, fieldName, value) }
fun Any?.getObjectField(fieldName: String): Any? = this?.let { FieldHelper.getObjectField(this, fieldName) }
fun <T> Any?.getObjectFieldAs(fieldName: String): T {
    return this.getObjectField(fieldName) as T
}
fun <T> Any?.getObjectFieldOrNullAs(fieldName: String) = runCatchingOrNull {
    this.getObjectField(fieldName) as T
}


fun Class<*>?.getStaticObjectField(fieldName: String): Any? = this?.let { FieldHelper.getStaticObjectField(it, fieldName) }

fun Class<*>?.getStaticBooleanField(fieldName: String): Any? = this?.let { FieldHelper.getStaticBooleanField(it, fieldName) }
