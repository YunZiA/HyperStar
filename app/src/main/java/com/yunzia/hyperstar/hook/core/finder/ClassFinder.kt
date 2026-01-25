package com.yunzia.hyperstar.hook.core.finder

import com.yunzia.hyperstar.hook.core.Log.logE
import io.github.kyuubiran.ezxhelper.core.ClassLoaderProvider
import io.github.kyuubiran.ezxhelper.core.finder.ClassFinder

fun String.loadClass(): Class<*>? {
    return findClass(this)
}

fun String.loadClassBy(classLoader: ClassLoader?): Class<*>? {
    return findClass(this,classLoader)
}


fun findClass(className: String, classLoader: ClassLoader? = ClassLoaderProvider.safeClassLoader): Class<*>? {
    return try {
        Class.forName(className, false, classLoader)
    } catch (e: ClassNotFoundException) {
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
fun findClassWithPrefix(vararg className: String,classLoader: ClassLoader? = ClassLoaderProvider.safeClassLoader): Class<*>? {
    for (name in className) {
        val clazz = findClass(name, classLoader)
        if (clazz != null) {
            return clazz
        }
    }
    logE("$className is not find")
    return null
}