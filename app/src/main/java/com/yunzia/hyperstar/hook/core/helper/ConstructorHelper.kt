package com.yunzia.hyperstar.hook.core.helper

import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.StarLog.logE
import com.yunzia.hyperstar.hook.core.StarLog.logW
import com.yunzia.hyperstar.hook.core.provider.ClassLoaderProvider
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap

object ConstructorHelper : CoreHelper() {
    private const val TAG = "ConstructorHelper"
    private val NULL = Any()
    private val constructorCache = ConcurrentHashMap<ConstructorCacheKey, Any>()


    @JvmStatic
    private inline fun getCachedOrFind(
        key: ConstructorCacheKey,
        crossinline loader: () -> Constructor<*>?
    ): Constructor<*>? {
        constructorCache[key]?.let {
            return if (it === NULL) null else it as Constructor<*>
        }
        val result = loader()
        constructorCache.putIfAbsent(key, result ?: NULL)

        return result
    }

    /**
     * 安全查找构造函数：找不到时返回 null 并打日志，不抛异常
     */
    @JvmStatic
    fun findConstructorExact(clazz: Class<*>?, vararg parameterTypes: Class<*>): Constructor<*>? {
        clazz?: return null

        val key = ConstructorCacheKey(
            System.identityHashCode(clazz.classLoader),
            clazz.name,
            parameterTypes.contentDeepHashCode()
        )
        return getCachedOrFind(key) {
            try {
                clazz.getDeclaredConstructor(*parameterTypes).apply {
                    isAccessible = true
                }
            } catch (_: NoSuchMethodException) {
                null
            } catch (t: Throwable) {
                logE(TAG, "Unexpected error while finding constructor: $key", t)
                null
            }
        }
    }

    // 如果你还想支持 Object... 参数（自动转 Class）
    @JvmStatic
    fun findConstructorExact(clazz: Class<*>?, vararg parameterTypes: Any?): Constructor<*>? {
        return findConstructorExact(clazz, *getParameterClasses(clazz?.classLoader, parameterTypes))
    }

    @JvmStatic
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

    @JvmStatic
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

    internal data  class ConstructorCacheKey(
        private val loaderId: Int,
        private val className: String,
        private val signatureHash: Int,
    )

}

fun Class<*>?.findConstructorExact(vararg parameterTypes: Any?): Constructor<*>? = ConstructorHelper.findConstructorExact(this, *parameterTypes)
