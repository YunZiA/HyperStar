package com.yunzia.hyperstar.hook.core.helper

import com.yunzia.hyperstar.hook.core.StarLog
import com.yunzia.hyperstar.hook.core.StarLog.logE
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.MethodHelper.TAG
import com.yunzia.hyperstar.hook.core.helper.MethodHelper.findMethodBestMatch
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.concurrent.ConcurrentHashMap

object MethodHelper {

    const val TAG = "MethodHelper"
    private val methodCache = NullableCache<MethodCacheKey, Method>()

    @JvmStatic
    fun getCacheSize(): Int = methodCache.size()

    @JvmStatic
    fun findMethodExact(clazz: Class<*>?, methodName: String, vararg parameterTypes: Any?): Method? {
        clazz ?: return null
        return findMethodExact(
            clazz,
            methodName,
            *getParameterClasses(clazz.classLoader, parameterTypes)
        )
    }

    @JvmStatic
    fun findMethodExactIfExists(
        clazz: Class<*>?,
        methodName: String,
        vararg parameterTypes: Any?
    ): Method? {
        return try {
            findMethodExact(clazz, methodName, *parameterTypes)
        } catch (_: NoSuchMethodError) {
            null
        } catch (_: ClassNotFoundException) {
            null
        }
    }

    @JvmStatic
    fun findMethodExactIfExists(
        className: String,
        classLoader: ClassLoader,
        methodName: String,
        vararg parameterTypes: Any?
    ): Method? {
        return try {
            findMethodExact(className, classLoader, methodName, *parameterTypes)
        } catch (_: NoSuchMethodError) {
            null
        } catch (_: ClassNotFoundException) {
            null
        }
    }

    @JvmStatic
    fun findMethodExact(
        clazz: Class<*>?,
        methodName: String,
        vararg paramTypes: Class<*>
    ): Method? {
        clazz ?: return null
        val key = buildKey(clazz, methodName, paramTypes, "exact")
        return methodCache.getOrPut(key) {
            try {
                clazz.getDeclaredMethod(methodName, *paramTypes).apply { isAccessible = true }
            } catch (e: NoSuchMethodException) {
                StarLog.logE("Method not found: $key | ${e.message}")
                null
            }
        }
    }

    @JvmStatic
    fun findMethodExact(
        className: String,
        classLoader: ClassLoader,
        methodName: String,
        vararg parameterTypes: Any?
    ): Method? {
        val clazz = findClass(className, classLoader) ?: return null
        val resolvedParams = getParameterClasses(classLoader, parameterTypes)
        return findMethodExact(clazz, methodName, *resolvedParams)
    }

    @JvmStatic
    fun findMethodBestMatch(
        clazz: Class<*>?,
        methodName: String,
        vararg parameterTypes: Class<*>
    ): Method? {
        clazz ?: return null
        val key = buildKey(clazz, methodName, parameterTypes, "bestmatch")
        return methodCache.getOrPut(key) {

            val exact = runCatching {
                clazz.getDeclaredMethod(methodName, *parameterTypes)
            }.getOrNull()

            if (exact != null) {
                exact.isAccessible = true
                return@getOrPut exact
            }

            var bestMatch: Method? = null
            var currentClass: Class<*>? = clazz
            var considerPrivate = true

            while (currentClass != null) {
                for (method in currentClass.declaredMethods) {
                    if (method.name != methodName) continue
                    if (!considerPrivate && Modifier.isPrivate(method.modifiers)) continue
                    if (!isAssignable(parameterTypes, method.parameterTypes)) continue

                    if (bestMatch == null || compareParameterTypes(
                            method.parameterTypes,
                            bestMatch.parameterTypes,
                            parameterTypes
                        ) < 0
                    ) {
                        bestMatch = method
                    }
                }
                considerPrivate = false
                currentClass = currentClass.superclass
            }
            bestMatch?.apply { isAccessible = true }
        }
    }

    @JvmStatic
    fun findMethodBestMatch(
        clazz: Class<*>?,
        methodName: String,
        vararg args: Any?
    ): Method? {
        clazz ?: return null
        val paramTypes = args.map { it.toParamType() }.toTypedArray()
        return findMethodBestMatch(clazz, methodName, *paramTypes)
    }

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun findMethodBestMatch(
        clazz: Class<*>?,
        methodName: String,
        parameterTypes: Array<Class<*>?>,
        args: Array<out Any?>
    ): Method? {
        clazz ?: return null
        val resolved = parameterTypes.mapIndexed { i, type ->
            type ?: args.getOrNull(i)?.javaClass ?: Any::class.java
        }.toTypedArray()
        return findMethodBestMatch(clazz, methodName, *resolved)
    }

    @JvmStatic
    fun findMethodsByExactParameters(
        clazz: Class<*>?,
        returnType: Class<*>? = null,
        vararg parameterTypes: Class<*>
    ): Array<Method?> {
        clazz ?: return emptyArray()
        return clazz.declaredMethods
            .filter { method ->
                (returnType == null || method.returnType == returnType) &&
                        method.parameterTypes.contentEquals(parameterTypes)
            }
            .onEach { it.isAccessible = true }
            .toTypedArray()
    }

    @JvmStatic
    private fun buildKey(
        clazz: Class<*>,
        methodName: String,
        parameterTypes: Array<out Class<*>>,
        tag: String
    ) = MethodCacheKey(
        System.identityHashCode(clazz.classLoader),
        clazz.name,
        methodName,
        parameterTypes.contentHashCode(),
        tag
    )

    @JvmStatic
    fun getParameterClasses(
        loader: ClassLoader?,
        params: Array<out Any?>
    ): Array<Class<*>> = params.map { param ->
        when (param) {
            null -> Any::class.java
            is Class<*> -> param
            is String -> findClass(param, loader) ?: Any::class.java
            else -> param.toParamType()
        }
    }.toTypedArray()

    @JvmStatic
    private val primitiveWrapperMap = mapOf(
        Boolean::class.javaPrimitiveType to Boolean::class.java,
        Int::class.javaPrimitiveType to Int::class.java,
        Float::class.javaPrimitiveType to Float::class.java,
        Long::class.javaPrimitiveType to Long::class.java,
        Double::class.javaPrimitiveType to Double::class.java,
        Short::class.javaPrimitiveType to Short::class.java,
        Byte::class.javaPrimitiveType to Byte::class.java,
        Char::class.javaPrimitiveType to Char::class.java
    )

    @JvmStatic
    private val wrapperPrimitiveMap =
        primitiveWrapperMap.entries.associate { it.value to it.key!! }

    @JvmStatic
    private fun isAssignable(
        actual: Array<out Class<*>>,
        formal: Array<out Class<*>>
    ): Boolean {
        if (actual.size != formal.size) return false
        return actual.indices.all { isAssignable(actual[it], formal[it]) }
    }

    @JvmStatic
    private fun isAssignable(
        actual: Class<*>,
        formal: Class<*>
    ): Boolean {
        if (formal.isAssignableFrom(actual)) return true
        if (primitiveWrapperMap[formal] == actual || primitiveWrapperMap[actual] == formal) return true
        return false
    }

    @JvmStatic
    private fun compareParameterTypes(
        m1: Array<out Class<*>>,
        m2: Array<out Class<*>>,
        target: Array<out Class<*>>
    ): Int {
        for (i in target.indices) {
            val t = target[i]
            val a1 = m1[i]
            val a2 = m2[i]
            if (a1 == a2) continue
            if (a1 == t) return -1
            if (a2 == t) return 1
            if (a2.isAssignableFrom(a1)) return -1
            if (a1.isAssignableFrom(a2)) return 1
        }
        return 0
    }

    @JvmStatic
    private fun Any?.toParamType(): Class<*> {
        if (this == null) return Any::class.java
        return wrapperPrimitiveMap[this::class.java] ?: this.javaClass
    }

    private data class MethodCacheKey(
        val loaderId: Int,
        val className: String,
        val methodName: String,
        val signatureHash: Int,
        val tag: String
    )

}


fun Any?.callMethod(methodName: String, vararg args: Any?): Any? {
    try {
        return findMethodBestMatch(this?.javaClass, methodName, *args)?.invoke(this, *args)
    } catch (t: Throwable) {
        logE(TAG, t.message)
    }
    return null
}

fun <T> Any?.callMethodAs(methodName: String, vararg args: Any?): T {
    return this.callMethod(methodName, *args) as T
}

fun <T> Class<*>?.callStaticMethodAs(methodName: String, vararg args: Any?): T {
    return this.callStaticMethod(methodName, *args) as T
}

fun Class<*>?.callStaticMethods(methodName: String, parameterTypes: Array<Class<*>>, vararg args: Any?): Any? {
    try {
        return findMethodBestMatch(this, methodName, parameterTypes, *args)?.invoke(null, *args)
    } catch (t: Throwable) {
        logE(TAG, t.message)
    }
    return null
}

fun Class<*>?.callStaticMethod(methodName: String, vararg args: Any?): Any? {
    try {
        return findMethodBestMatch(this, methodName, *args)?.invoke(null, *args)
    } catch (t: Throwable) {
        logE(TAG, t.message)
    }
    return null
}
