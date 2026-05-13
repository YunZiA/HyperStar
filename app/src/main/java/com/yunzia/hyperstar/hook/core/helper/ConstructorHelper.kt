package com.yunzia.hyperstar.hook.core.helper

import com.yunzia.hyperstar.hook.core.StarLog.logE
import com.yunzia.hyperstar.hook.core.StarLog.logW
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.provider.ClassLoaderProvider
import java.lang.reflect.Constructor

object ConstructorHelper {
    private const val TAG = "ConstructorHelper"
    private val constructorCache = NullableCache<ConstructorCacheKey, Constructor<*>>()

    @JvmStatic
    fun findConstructorExact(clazz: Class<*>?, vararg parameterTypes: Class<*>): Constructor<*>? {
        clazz ?: return null

        val key = ConstructorCacheKey(
            System.identityHashCode(clazz.classLoader),
            clazz.name,
            parameterTypes.contentDeepHashCode()
        )
        return constructorCache.getOrPut(key) {
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

    @JvmStatic
    fun findConstructorExact(clazz: Class<*>?, vararg parameterTypes: Any?): Constructor<*>? {
        return findConstructorExact(
            clazz,
            *MethodHelper.getParameterClasses(clazz?.classLoader, parameterTypes)
        )
    }

    @JvmStatic
    fun findConstructorExact(
        className: String,
        classLoader: ClassLoader?,
        vararg parameterTypes: Any
    ): Constructor<*>? {
        return try {
            val clazz = findClass(className, classLoader)
            findConstructorExact(clazz, *MethodHelper.getParameterClasses(classLoader, parameterTypes))
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
            findConstructorExact(clazz, *MethodHelper.getParameterClasses(classLoader, parameterTypes))
        } catch (e: Exception) {
            logW(TAG, "Failed to find class or constructor: $className", e)
            null
        }
    }

    internal data class ConstructorCacheKey(
        private val loaderId: Int,
        private val className: String,
        private val signatureHash: Int,
    )

}

fun Class<*>?.findConstructorExact(vararg parameterTypes: Any?): Constructor<*>? = ConstructorHelper.findConstructorExact(this, *parameterTypes)
