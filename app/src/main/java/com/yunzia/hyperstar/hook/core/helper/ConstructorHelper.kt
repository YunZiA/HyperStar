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
    fun findConstructorExact(clazz: Class<*>?, vararg parameterTypes: Class<*>): Constructor<*>? {
        clazz?: return null

        val key = ConstructorCacheKey(
            System.identityHashCode(clazz.classLoader),
            clazz.name,
            parameterTypes
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

    internal class ConstructorCacheKey(
        private val loaderId: Int,
        private val className: String,
        parameterTypes: Array<out Class<*>>
    ) {
        private val params = parameterTypes.copyOf()
        private val paramsHash = params.contentHashCode()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ConstructorCacheKey) return false
            return loaderId == other.loaderId && className == other.className && params.contentEquals(other.params)
        }

        override fun hashCode(): Int {
            var result = loaderId
            result = 31 * result + className.hashCode()
            result = 31 * result + paramsHash
            return result
        }
    }
}

fun Class<*>?.findConstructorExact(vararg parameterTypes: Any?): Constructor<*>? = ConstructorHelper.findConstructorExact(this, *parameterTypes)
