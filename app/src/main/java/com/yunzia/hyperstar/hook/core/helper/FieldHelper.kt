package com.yunzia.hyperstar.hook.core.helper

import com.yunzia.hyperstar.hook.base.runCatchingOrNull
import com.yunzia.hyperstar.hook.core.StarLog.logE
import java.lang.invoke.VarHandle
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap

object FieldHelper {
    const val TAG = "FieldHelper"
    private val NULL = Any()

    private val fieldCache = ConcurrentHashMap<FieldCacheKey, Any>()
    private val fieldTypeCache = ConcurrentHashMap<String, Field>()

    private inline fun getCachedOrFind(
        key: FieldCacheKey,
        crossinline loader: () -> Field?
    ): Field? {

        fieldCache[key]?.let {
            return if (it === NULL) null else it as Field
        }
        val result = loader()

        fieldCache.putIfAbsent(key, result ?: NULL)

        return result
    }

    /**
     * 查找字段（精确名称），找不到则抛 NoSuchFieldError。
     */
    fun findField(
        clazz: Class<*>,
        fieldName: String
    ): Field? {

        val key = FieldCacheKey(System.identityHashCode(clazz.classLoader),clazz.name, fieldName)
        return getCachedOrFind(key) {
            var current: Class<*>? = clazz
            while (current != null && current != Any::class.java) {
                try {
                    val field = current.getDeclaredField(fieldName)
                    field.isAccessible = true
                    return@getCachedOrFind field
                } catch (_: NoSuchFieldException) {
                    current = current.superclass
                }
            }
            null
        }
    }


    fun requireField(
        clazz: Class<*>,
        fieldName: String
    ): Field {
        return findField(clazz, fieldName) ?: throw NoSuchFieldError("${clazz.name}#$fieldName")
    }

    /**
     * 递归查找字段：从 clazz 开始，向上遍历继承链（不含 Object）。
     */
    @Throws(NoSuchFieldException::class)
    private fun findFieldRecursiveImpl(
        clazz: Class<*>,
        fieldName: String
    ): Field {
        var current: Class<*>? = clazz
        while (current != null && current != Any::class.java) {
            try {
                return current.getDeclaredField(fieldName)
            } catch (_: NoSuchFieldException) {
                current = current.superclass
            }
        }
        throw NoSuchFieldException("${clazz.name}#$fieldName")
    }

    /**
     * 查找第一个类型完全匹配的字段（递归父类），找不到抛 NoSuchFieldError。
     */
    fun findFirstFieldByExactType(
        clazz: Class<*>,
        type: Class<*>
    ): Field {
        val key = "${System.identityHashCode(clazz.classLoader)}:${clazz.name}#type:${type.name}"
        fieldTypeCache[key]?.let { return it }
        var current: Class<*>? = clazz
        while (current != null && current != Any::class.java) {
            for (field in current.declaredFields) {
                if (field.type == type) {
                    field.isAccessible = true
                    fieldTypeCache[key] = field
                    return field
                }
            }
            current = current.superclass
        }
        throw NoSuchFieldError("Field of type ${type.name} in class ${clazz.name}")
    }

    @JvmSynthetic
    internal inline fun <reified T> getObjectField(obj: Any, fieldName: String): T {
        return getField(obj::class.java, fieldName).get(obj) as T
    }
    private inline fun <T> getPrimitiveField(
        obj: Any,
        fieldName: String,
        getter: Field.(Any) -> T
    ): T {
        return getter(
            getField(obj::class.java, fieldName),
            obj
        )
    }

    fun getBooleanField(obj: Any, fieldName: String): Boolean = getPrimitiveField(obj, fieldName, Field::getBoolean)

    fun getByteField(obj: Any, fieldName: String): Byte = getPrimitiveField(obj, fieldName, Field::getByte)

    fun getCharField(obj: Any, fieldName: String): Char = getPrimitiveField(obj, fieldName, Field::getChar)

    fun getDoubleField(obj: Any, fieldName: String): Double = getPrimitiveField(obj, fieldName, Field::getDouble)

    fun getFloatField(obj: Any, fieldName: String): Float = getPrimitiveField(obj, fieldName, Field::getFloat)

    fun getIntField(obj: Any, fieldName: String) = getPrimitiveField(obj, fieldName, Field::getInt)

    fun getLongField(obj: Any, fieldName: String): Long = getPrimitiveField(obj, fieldName, Field::getLong)

    fun getShortField(obj: Any, fieldName: String): Short = getPrimitiveField(obj, fieldName, Field::getShort)

    // Helper to reduce duplication
    private fun getField(clazz: Class<*>, fieldName: String): Field {
        return findField(clazz, fieldName) ?: throw NoSuchFieldError("Field '$fieldName' not found in ${clazz.simpleName}")
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
            requireField(targetClass, fieldName).apply {
                when (type) {
                    Boolean::class.javaPrimitiveType -> setBoolean(obj, value as Boolean)
                    Byte::class.javaPrimitiveType -> setByte(obj, value as Byte)
                    Char::class.javaPrimitiveType -> setChar(obj, value as Char)
                    Double::class.javaPrimitiveType -> setDouble(obj, value as Double)
                    Float::class.javaPrimitiveType -> setFloat(obj, value as Float)
                    Int::class.javaPrimitiveType -> setInt(obj, value as Int)
                    Long::class.javaPrimitiveType  -> setLong(obj, value as Long)
                    Short::class.javaPrimitiveType  -> setShort(obj, value as Short)
                    else -> set(obj, value)
                }
            }
        } catch (e: IllegalAccessException) {
            logE(TAG, "Field access error: ${e.message}", e)
            throw IllegalAccessError(e.message)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

    internal class FieldCacheKey(
        loaderId: Int,
        className: String,
        fieldName: String
    )

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
@JvmSynthetic
inline fun <reified T> Any?.getObjectFieldAs(fieldName: String): T {
    return this.getObjectField(fieldName) as T
}
@JvmSynthetic
inline fun <reified T> Any?.getObjectFieldOrNullAs(fieldName: String) = runCatchingOrNull {
    this.getObjectField(fieldName) as T
}

fun Class<*>?.getStaticObjectField(fieldName: String): Any? = this?.let { FieldHelper.getStaticObjectField(it, fieldName) }

fun Class<*>?.getStaticBooleanField(fieldName: String): Boolean? = this?.let { FieldHelper.getStaticBooleanField(it, fieldName) }
