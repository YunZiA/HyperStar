//package com.yunzia.hyperstar.hook.help
//
//import com.yunzia.hyperstar.hook.help.MethodHelper.findMethodExact
//import com.yunzia.hyperstar.hook.util.XposedHelpers
//import de.robv.android.xposed.XC_MethodHook
//import de.robv.android.xposed.XposedBridge
//import de.robv.android.xposed.XposedHelpers.ClassNotFoundError
//import java.lang.reflect.InvocationTargetException
//import java.lang.reflect.Method
//import java.lang.reflect.Modifier
//import java.util.LinkedList
//
//object MethodHelper {
//
//    private val methodCache: HashMap<String, Method> = HashMap<String, Method>()
//    fun findMethodExact(clazz: Class<*>, methodName: String, vararg parameterTypes: Any): Method {
//        return findMethodExact(
//            clazz,
//            methodName,
//            *getParameterClasses(clazz.getClassLoader(), *parameterTypes)
//        )
//    }
//    private fun getParametersString(vararg clazzes: Class<*>?): String {
//        val sb = StringBuilder("(")
//        var first = true
//        for (clazz in clazzes) {
//            if (first) first = false
//            else sb.append(",")
//
//            if (clazz != null) sb.append(clazz.getCanonicalName())
//            else sb.append("null")
//        }
//        sb.append(")")
//        return sb.toString()
//    }
//
//    private fun getParameterClasses(
//        classLoader: ClassLoader?,
//        parameterTypesAndCallback: Array<Any>
//    ): Array<Class<*>?> {
//        var parameterClasses: Array<Class<*>?>? = null
//        for (i in parameterTypesAndCallback.indices.reversed()) {
//            val type = parameterTypesAndCallback[i] ?: throw ClassNotFoundError("parameter type must not be null", null)
//
//            // ignore trailing callback
//            if (type is XC_MethodHook) continue
//
//            if (parameterClasses == null) parameterClasses = arrayOfNulls<Class<*>>(i + 1)
//
//            if (type is Class<*>) parameterClasses[i] = type
//            else if (type is String) parameterClasses[i] =
//                de.robv.android.xposed.XposedHelpers.findClass(type, classLoader)
//            else throw ClassNotFoundError(
//                "parameter type must either be specified as Class or String",
//                null
//            )
//        }
//
//        // if there are no arguments for the method
//        if (parameterClasses == null) parameterClasses = arrayOfNulls<Class<*>>(0)
//
//        return parameterClasses
//    }
//    fun findMethodExactIfExists(
//        clazz: Class<*>,
//        methodName: String,
//        vararg parameterTypes: Any
//    ): Method? {
//        try {
//            return findMethodExact(clazz, methodName, *parameterTypes)
//        } catch (e: NoSuchMethodError) {
//            return null
//        } catch (e: ClassNotFoundError) {
//            return null
//        }
//    }
//
//    fun findMethodExact(
//        className: String?,
//        classLoader: ClassLoader?,
//        methodName: String,
//        vararg parameterTypes: Any
//    ): Method {
//        return findMethodExact(
//            XposedHelpers.findClass(className, classLoader),
//            methodName,
//            *getParameterClasses(classLoader, parameterTypes)
//        )
//    }
//
//    /**
//     * Look up and return a method if it exists.
//     * Like [.findMethodExact], but doesn't throw an
//     * exception if the method doesn't exist.
//     *
//     * @param className The name of the class which implements the method.
//     * @param classLoader The class loader for resolving the target and parameter classes.
//     * @param methodName The target method name.
//     * @param parameterTypes The parameter types of the target method.
//     * @return A reference to the method, or `null` if it doesn't exist.
//     */
//    fun findMethodExactIfExists(
//        className: String?,
//        classLoader: ClassLoader?,
//        methodName: String,
//        vararg parameterTypes: Any
//    ): Method? {
//        try {
//            return findMethodExact(className, classLoader, methodName, *parameterTypes)
//        } catch (e: NoSuchMethodError) {
//            return null
//        } catch (e: ClassNotFoundError) {
//            return null
//        }
//    }
//
//    /**
//     * Look up a method in a class and set it to accessible.
//     * See [.findMethodExact] for details.
//     *
//     *
//     * This variant requires that you already have reference to all the parameter types.
//     */
//    fun findMethodExact(
//        clazz: Class<*>,
//        methodName: String,
//        vararg parameterTypes: Class<*>?
//    ): Method {
//        val fullMethodName =
//            clazz.getName() + '#' + methodName + getParametersString(*parameterTypes) + "#exact"
//
//        if (methodCache.containsKey(fullMethodName)) {
//            val method: Method = methodCache.get(fullMethodName) ?:throw NoSuchMethodError(fullMethodName)
//            return method
//        }
//
//        try {
//            val method = clazz.getDeclaredMethod(methodName, *parameterTypes)
//            method.isAccessible = true
//            methodCache[fullMethodName] = method
//            return method
//        } catch (e: NoSuchMethodException) {
//            methodCache.put(fullMethodName, null)
//            throw NoSuchMethodError(fullMethodName)
//        }
//    }
//
//    /**
//     * Returns an array of all methods declared/overridden in a class with the specified parameter types.
//     *
//     *
//     * The return type is optional, it will not be compared if it is `null`.
//     * Use `void.class` if you want to search for methods returning nothing.
//     *
//     * @param clazz The class to look in.
//     * @param returnType The return type, or `null` (see above).
//     * @param parameterTypes The parameter types.
//     * @return An array with matching methods, all set to accessible already.
//     */
//    fun findMethodsByExactParameters(
//        clazz: Class<*>,
//        returnType: Class<*>?,
//        vararg parameterTypes: Class<*>?
//    ): Array<Method?> {
//        val result: MutableList<Method?> = LinkedList<Method?>()
//        for (method in clazz.getDeclaredMethods()) {
//            if (returnType != null && returnType != method.getReturnType()) continue
//
//            val methodParameterTypes = method.getParameterTypes()
//            if (parameterTypes.size != methodParameterTypes.size) continue
//
//            var match = true
//            for (i in parameterTypes.indices) {
//                if (parameterTypes[i] != methodParameterTypes[i]) {
//                    match = false
//                    break
//                }
//            }
//
//            if (!match) continue
//
//            method.setAccessible(true)
//            result.add(method)
//        }
//        return result.toTypedArray<Method?>()
//    }
//
//    /**
//     * Look up a method in a class and set it to accessible.
//     *
//     *
//     * This does'nt only look for exact matches, but for the best match. All considered candidates
//     * must be compatible with the given parameter types, i.e. the parameters must be assignable
//     * to the method's formal parameters. Inherited methods are considered here.
//     *
//     * @param clazz The class which declares, inherits or overrides the method.
//     * @param methodName The method name.
//     * @param parameterTypes The types of the method's parameters.
//     * @return A reference to the best-matching method.
//     * @throws NoSuchMethodError In case no suitable method was found.
//     */
//    fun findMethodBestMatch(
//        clazz: Class<*>,
//        methodName: String,
//        vararg parameterTypes: Class<*>?
//    ): Method {
//        val fullMethodName =
//            clazz.getName() + '#' + methodName + getParametersString(*parameterTypes) + "#bestmatch"
//
//        if (methodCache.containsKey(fullMethodName)) {
//            val method: Method = methodCache.get(fullMethodName)
//                ?: throw NoSuchMethodError(fullMethodName)
//            return method
//        }
//
//        try {
//            val method = findMethodExact(clazz, methodName, *parameterTypes)
//            methodCache[fullMethodName] = method
//            return method
//        } catch (ignored: NoSuchMethodError) {
//        }
//
//        var bestMatch: Method? = null
//        var clz = clazz
//        var considerPrivateMethods = true
//        do {
//            for (method in clz.getDeclaredMethods()) {
//                // don't consider private methods of superclasses
//                if (!considerPrivateMethods && Modifier.isPrivate(method.getModifiers())) continue
//
//                // compare name and parameters
//                if (method.getName() == methodName && ClassUtils.isAssignable(
//                        parameterTypes,
//                        method.getParameterTypes(),
//                        true
//                    )
//                ) {
//                    // get accessible version of method
//                    if (bestMatch == null || MemberUtils.compareParameterTypes(
//                            method.getParameterTypes(),
//                            bestMatch.getParameterTypes(),
//                            parameterTypes
//                        ) < 0
//                    ) {
//                        bestMatch = method
//                    }
//                }
//            }
//            considerPrivateMethods = false
//        } while ((clz.getSuperclass().also { clz = it }) != null)
//
//        if (bestMatch != null) {
//            bestMatch.isAccessible = true
//            methodCache[fullMethodName] = bestMatch
//            return bestMatch
//        } else {
//            val e = NoSuchMethodError(fullMethodName)
//            methodCache.put(fullMethodName, null)
//            throw e
//        }
//    }
//
//    fun findMethodBestMatch(clazz: Class<*>, methodName: String, vararg args: Any?): Method {
//        return findMethodBestMatch(clazz, methodName, *getParameterTypes(*args))
//    }
//
//    /**
//     * Look up a method in a class and set it to accessible.
//     *
//     *
//     * See [.findMethodBestMatch] for details. This variant
//     * determines the parameter types from the classes of the given objects. For any item that is
//     * `null`, the type is taken from `parameterTypes` instead.
//     */
//    fun findMethodBestMatch(
//        clazz: Class<*>,
//        methodName: String,
//        parameterTypes: Array<Class<*>?>,
//        args: Array<Any?>
//    ): Method {
//        var argsClasses: Array<Class<*>?>? = null
//        for (i in parameterTypes.indices) {
//            if (parameterTypes[i] != null) continue
//            if (argsClasses == null) argsClasses = getParameterTypes(*args)
//            parameterTypes[i] = argsClasses[i]
//        }
//        return findMethodBestMatch(clazz, methodName, *parameterTypes)
//    }
//
//    /**
//     * Returns an array with the classes of the given objects.
//     */
//    fun getParameterTypes(vararg args: Any?): Array<Class<*>?> {
//        val clazzes = arrayOfNulls<Class<*>>(args.size)
//        for (i in args.indices) {
//            clazzes[i] = if (args[i] != null) args[i]!!.javaClass else null
//        }
//        return clazzes
//    }
//
//    fun callMethod(obj: Any, methodName: String, vararg args: Any?): Any? {
//        try {
//            return findMethodBestMatch(obj.javaClass, methodName, *args)
//                .invoke(obj, *args)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        } catch (e: InvocationTargetException) {
//            throw XposedHelpers.InvocationTargetError(e.cause)
//        }
//    }
//
//    fun callMethod(
//        obj: Any,
//        methodName: String,
//        parameterTypes: Array<Class<*>?>,
//        vararg args: Any?
//    ): Any? {
//        try {
//            return findMethodBestMatch(
//                obj.javaClass,
//                methodName,
//                parameterTypes,
//                args
//            ).invoke(obj, *args)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        } catch (e: InvocationTargetException) {
//            throw XposedHelpers.InvocationTargetError(e.cause)
//        }
//    }
//
//    fun callStaticMethod(clazz: Class<*>, methodName: String, vararg args: Any?): Any? {
//        try {
//            return findMethodBestMatch(clazz, methodName, *args).invoke(null, *args)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        } catch (e: InvocationTargetException) {
//            throw XposedHelpers.InvocationTargetError(e.cause)
//        }
//    }
//
//    fun callStaticMethod(
//        clazz: Class<*>,
//        methodName: String,
//        parameterTypes: Array<Class<*>?>,
//        vararg args: Any?
//    ): Any? {
//        try {
//            return findMethodBestMatch(clazz, methodName, parameterTypes, args)
//                .invoke(null, *args)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        } catch (e: InvocationTargetException) {
//            throw XposedHelpers.InvocationTargetError(e.cause)
//        }
//    }
//}