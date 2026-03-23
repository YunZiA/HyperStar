package com.yunzia.hyperstar.hook.core.helper

import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.StarLog.logE
import com.yunzia.hyperstar.hook.core.StarLog.logW
import com.yunzia.hyperstar.hook.core.provider.ClassLoaderProvider
import java.lang.reflect.Constructor
import java.util.concurrent.ConcurrentHashMap

object ConstructorHelper : CoreHelper() {
    private const val TAG = "ConstructorHelper"
    private val constructorCache = ConcurrentHashMap<String, Constructor<*>>()

    /**
     * 安全查找构造函数：找不到时返回 null 并打日志，不抛异常
     */
    fun findConstructorExact(clazz: Class<*>?, vararg parameterTypes: Class<*>): Constructor<*>? {
        clazz?: return null

        val fullConstructorName = "${clazz.name}${getParametersString(parameterTypes)}#exact"
        // 先查缓存
        constructorCache[fullConstructorName]?.let { cached ->
            return cached
        }

        return try {
            val constructor = clazz.getDeclaredConstructor(*parameterTypes)
            constructor.isAccessible = true
            constructorCache[fullConstructorName] = constructor
            constructor
        } catch (e: NoSuchMethodException) {
            logW(TAG, "Constructor not found: $fullConstructorName", e)
            null
        } catch (e: Exception) {
            logE(TAG, "Unexpected error while finding constructor: $fullConstructorName", e)
            null
        }
    }

    // 如果你还想支持 Object... 参数（自动转 Class）
    fun findConstructorExact(clazz: Class<*>?, vararg parameterTypes: Any?): Constructor<*>? {
        return findConstructorExact(clazz, *getParameterClasses(clazz?.classLoader, parameterTypes))
    }

    fun findConstructorExact(
        className: String,
        classLoader: ClassLoader?,
        vararg parameterTypes: Any
    ): Constructor<*>? {
        return try {
            val clazz = findClass(className, classLoader)
            findConstructorExact(clazz, *getParameterClasses(classLoader, parameterTypes))
        } catch (e: Exception) {
            logW(TAG, "Failed to find class or constructor: $className", e)
            null
        }
    }

    fun findConstructorExact(
        className: String,
        vararg parameterTypes: Any?
    ): Constructor<*>? {
        val classLoader = ClassLoaderProvider.safeClassLoader
        return try {
            val clazz = findClass(className, classLoader)
            findConstructorExact(clazz, *getParameterClasses(classLoader, parameterTypes))
        } catch (e: Exception) {
            logW(TAG, "Failed to find class or constructor: $className", e)
            null
        }
    }
}

fun Class<*>?.findConstructorExact(vararg parameterTypes: Any?): Constructor<*>? = ConstructorHelper.findConstructorExact(this, *parameterTypes)
