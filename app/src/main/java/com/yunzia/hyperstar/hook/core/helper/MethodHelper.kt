package com.yunzia.hyperstar.hook.core.helper

import com.yunzia.hyperstar.hook.core.StarLog
import com.yunzia.hyperstar.hook.core.StarLog.logE
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.MethodHelper.TAG
import com.yunzia.hyperstar.hook.core.helper.MethodHelper.findMethodBestMatch
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.concurrent.ConcurrentHashMap

object MethodHelper {

    const val TAG = "MethodHelper"
    private val NULL = Any()
    private val methodCache = ConcurrentHashMap<MethodCacheKey, Any>()


    /**
     * 查找精确方法（参数类型支持 Object...：Class、String、实例等）
     */
    fun findMethodExact(clazz: Class<*>?, methodName: String, vararg parameterTypes: Any?): Method? {
        clazz ?: return null
        return findMethodExact(
            clazz,
            methodName,
            *getParameterClasses(clazz.classLoader, parameterTypes)
        )
    }

    /**
     * 安全查找（不抛异常，返回 null 表示未找到）
     */
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


    /**
     * 安全版本（类名 + ClassLoader）
     */
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

    /**
     * 精确查找方法（仅当前类，不含继承）
     */
    fun findMethodExact(
        clazz: Class<*>?,
        methodName: String,
        vararg paramTypes: Class<*>
    ): Method? {
        clazz ?: return null
        val key = buildKey(clazz, methodName, paramTypes, "exact")
        return getCachedOrFind(key) {
            try {
                clazz.getDeclaredMethod(methodName, *paramTypes).apply { isAccessible = true }
            } catch (e: NoSuchMethodException) {
                StarLog.logE("Method not found: $key | ${e.message}")
                null
            }
        }
    }

    /**
     * 通过类名 + ClassLoader 查找（支持字符串类名、Class 对象等参数）
     */
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

    /**
     * 查找最佳匹配方法（考虑继承链和参数可赋值性）
     */
    fun findMethodBestMatch(
        clazz: Class<*>?,
        methodName: String,
        vararg parameterTypes: Class<*>
    ): Method? {
        clazz ?: return null
        val key = buildKey(clazz, methodName, parameterTypes, "bestmatch")
        return getCachedOrFind(key) {

            val exact = runCatching {
                clazz.getDeclaredMethod(methodName, *parameterTypes)
            }.getOrNull()

            if (exact != null) {
                exact.isAccessible = true
                return@getCachedOrFind exact
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

    /**
     * 根据实际参数推断类型并查找最佳匹配
     */
    fun findMethodBestMatch(
        clazz: Class<*>?,
        methodName: String,
        vararg args: Any?
    ): Method? {
        clazz ?: return null
        val paramTypes = args.map { it.toParamType() }.toTypedArray()
        return findMethodBestMatch(clazz, methodName, *paramTypes)
    }

    /**
     * 混合显式类型与运行时参数
     */
    @Suppress("UNCHECKED_CAST")
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

    private fun buildKey(
        clazz: Class<*>,
        methodName: String,
        parameterTypes: Array<out Class<*>>,
        tag: String
    ) = MethodCacheKey(
            System.identityHashCode(clazz.classLoader),
            clazz.name,
            methodName,
            parameterTypes,
            tag
        )



    private inline fun getCachedOrFind(key: MethodCacheKey, crossinline loader: () -> Method?): Method? {
        methodCache[key]?.let {
            return if (it === NULL) null else it as Method
        }
        val result = loader()
        methodCache.putIfAbsent(key, result ?: NULL)
        return result
    }

    private fun getParameterClasses(
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

    private val wrapperPrimitiveMap =
        primitiveWrapperMap.entries.associate { it.value to it.key!! }

    private fun isAssignable(
        actual: Array<out Class<*>>,
        formal: Array<out Class<*>>
    ): Boolean {

        if (actual.size != formal.size) return false

        return actual.indices.all {

            isAssignable(actual[it], formal[it])

        }
    }

    private fun isAssignable(
        actual: Class<*>,
        formal: Class<*>
    ): Boolean {
        if (formal.isAssignableFrom(actual))
            return true
        if (primitiveWrapperMap[formal] == actual || primitiveWrapperMap[actual] == formal)
            return true
        return false
    }
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
    private val BOOLEAN_PRIMITIVE = Boolean::class.javaPrimitiveType!!
    private val INT_PRIMITIVE = Int::class.javaPrimitiveType!!
    private val Float_PRIMITIVE = Float::class.javaPrimitiveType!!
    private val LONG_PRIMITIVE = Long::class.javaPrimitiveType!!
    private val DOUBLE_PRIMITIVE = Double::class.javaPrimitiveType!!
    private val SHORT_PRIMITIVE = Short::class.javaPrimitiveType!!
    private val BYTE_PRIMITIVE = Byte::class.javaPrimitiveType!!
    private val CHAR_PRIMITIVE = Char::class.javaPrimitiveType!!
    private fun Any?.toParamType(): Class<*> {
        return when (this) {
            is Boolean -> BOOLEAN_PRIMITIVE
            is Int -> INT_PRIMITIVE
            is Float -> Float_PRIMITIVE
            is Long -> LONG_PRIMITIVE
            is Double -> DOUBLE_PRIMITIVE
            is Short -> SHORT_PRIMITIVE
            is Byte -> BYTE_PRIMITIVE
            is Char -> CHAR_PRIMITIVE
            else -> this?.javaClass ?: Any::class.java
        }
    }

    private data class MethodCacheKey(
        val loaderId: Int,
        val className: String,
        val methodName: String,
        val paramTypes: Array<out Class<*>>,
        val tag: String
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as MethodCacheKey

            if (loaderId != other.loaderId) return false
            if (className != other.className) return false
            if (methodName != other.methodName) return false
            if (!paramTypes.contentEquals(other.paramTypes)) return false
            if (tag != other.tag) return false

            return true
        }

        override fun hashCode(): Int {
            var result = loaderId
            result = 31 * result + className.hashCode()
            result = 31 * result + methodName.hashCode()
            result = 31 * result + paramTypes.contentHashCode()
            result = 31 * result + tag.hashCode()
            return result
        }
    }

}



fun  Any?.callMethod(methodName: String, vararg args: Any?): Any? {
    try {
        return findMethodBestMatch(this?.javaClass, methodName, *args)?.invoke(this, *args)
    } catch (t: Throwable) {
        logE(TAG, t.message)
    }
    return null
}

fun <T> Any?.callMethodAs(methodName: String, vararg args: Any?): T {
    return this.callMethod( methodName, *args) as T
}

fun  <T>  Class<*>?.callStaticMethodAs(methodName: String, vararg args: Any?):T {
    return this.callStaticMethod(methodName, *args) as T
}

fun Class<*>?.callStaticMethods(methodName: String, parameterTypes: Array<Class<*>>, vararg args: Any?):Any? {
    try {
        return findMethodBestMatch(this, methodName,parameterTypes, *args)?.invoke(null, *args)
    } catch (t: Throwable) {
        logE(TAG, t.message)
    }
    return null
}

fun Class<*>?.callStaticMethod(methodName: String, vararg args: Any?):Any? {
    try {
        return findMethodBestMatch(this, methodName, *args)?.invoke(null, *args)
    } catch (t: Throwable) {
        logE(TAG, t.message)
    }
    return null
}

//fun AfterHookParam.callSuperMethod(): Any? {
//    val thisObj = this.thisObject
//    val parameterTypes = this.args.map { it?.javaClass }.toTypedArray()
//    val superClass = thisObj.javaClass.superclass
//    val superMethod = findMethodBestMatch(superClass,this.member.name,*parameterTypes)
//    //MethodHandles.privateLookupIn(superClass,MethodHandles.lookup()).findSpecial(superClass,this.method.name,*parameterTypes,this.javaClass)
//    val methodHandle = MethodHandles.lookup().unreflectSpecial(superMethod, thisObj.javaClass)
//    return methodHandle.invokeWithArguments(thisObj, *this.args)
//}
//
//fun BeforeHookParam.callSuperMethod(): Any? {
//    val thisObj = this.thisObject
//    val parameterTypes = this.args.map { it?.javaClass }.toTypedArray()
//    val superClass = thisObj.javaClass.superclass
//    val superMethod = findMethodBestMatch(superClass,this.member.name,*parameterTypes)
//    //MethodHandles.privateLookupIn(superClass,MethodHandles.lookup()).findSpecial(superClass,this.method.name,*parameterTypes,this.javaClass)
//    val methodHandle = MethodHandles.lookup().unreflectSpecial(superMethod, thisObj.javaClass)
//    return methodHandle.invokeWithArguments(thisObj, *this.args)
//}