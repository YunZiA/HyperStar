package com.yunzia.hyperstar.hook.core.finder

import com.yunzia.hyperstar.hook.core.StarLog.logE
import com.yunzia.hyperstar.hook.core.provider.ClassLoaderProvider
import java.util.concurrent.ConcurrentHashMap

private data class CacheKey(val className: String, val classLoaderId: Int)

private val classCache = ConcurrentHashMap<CacheKey, Class<*>>()
private val missingClassCache = ConcurrentHashMap.newKeySet<CacheKey>()

fun String.loadClass(): Class<*>? {
    return findClass(this)
}

fun String.loadClassBy(classLoader: ClassLoader?): Class<*>? {
    return findClass(this, classLoader)
}


fun findClass(className: String, classLoader: ClassLoader? = ClassLoaderProvider.safeClassLoader): Class<*>? {
    val cacheKey = CacheKey(className, classLoader?.let(System::identityHashCode) ?: 0)
    classCache[cacheKey]?.let { return it }
    if (cacheKey in missingClassCache) return null
    return try {
        Class.forName(className, false, classLoader).also { classCache[cacheKey] = it }
    } catch (e: ClassNotFoundException) {
        missingClassCache.add(cacheKey)
        logE("Class not found: '$className' in ClassLoader: $classLoader\n${e.message}")
        null
    }
}

fun ClassLoader?.findClassWithPrefix(vararg className: String): Class<*>? {
    for (name in className) {
        val clazz = findClass(name, classLoader = this)
        if (clazz != null) {
            return clazz
        }
    }
    logE("$className is not find")
    return null
}

fun findClassWithPrefix(vararg className: String, classLoader: ClassLoader? = ClassLoaderProvider.safeClassLoader): Class<*>? {
    for (name in className) {
        val clazz = findClass(name, classLoader)
        if (clazz != null) {
            return clazz
        }
    }
    logE("$className is not find")
    return null
}
