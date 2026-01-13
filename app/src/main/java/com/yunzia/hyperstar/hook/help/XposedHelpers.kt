//package com.yunzia.hyperstar.hook.help
//
//import android.content.res.Resources
//import de.robv.android.xposed.XC_MethodHook
////import de.robv.android.xposed.XposedBridge
//import de.robv.android.xposed.XposedHelpers
//import external.org.apache.commons.lang3.ClassUtils
//import external.org.apache.commons.lang3.reflect.MemberUtils
//import java.io.ByteArrayOutputStream
//import java.io.FileInputStream
//import java.io.IOException
//import java.io.InputStream
//import java.lang.reflect.Constructor
//import java.lang.reflect.Field
//import java.lang.reflect.InvocationTargetException
//import java.lang.reflect.Method
//import java.lang.reflect.Modifier
//import java.math.BigInteger
//import java.security.MessageDigest
//import java.security.NoSuchAlgorithmException
//import java.util.LinkedList
//import java.util.WeakHashMap
//import java.util.concurrent.atomic.AtomicInteger
//
///**
// * Helpers that simplify hooking and calling methods/constructors, getting and settings fields, ...
// */
//object XposedHelpers {
//    private val fieldCache = HashMap<String?, Field>()
//    private val methodCache = HashMap<String?, Method?>()
//    private val constructorCache = HashMap<String?, Constructor<*>?>()
//    private val additionalFields = WeakHashMap<Any?, HashMap<String?, Any?>?>()
//    private val sMethodDepth = HashMap<String?, ThreadLocal<AtomicInteger>>()
//
//    /**
//     * Look up a class with the specified class loader.
//     *
//     *
//     * There are various allowed syntaxes for the class name, but it's recommended to use one of
//     * these:
//     *
//     *  * `java.lang.String`
//     *  * `java.lang.String[]` (array)
//     *  * `android.app.ActivityThread.ResourcesKey`
//     *  * `android.app.ActivityThread$ResourcesKey`
//     *
//     *
//     * @param className The class name in one of the formats mentioned above.
//     * @param classLoader The class loader, or `null` for the boot class loader.
//     * @return A reference to the class.
//     * @throws ClassNotFoundError In case the class was not found.
//     */
//    fun findClass(className: String?, classLoader: ClassLoader?): Class<*> {
//        var classLoader = classLoader
//        if (classLoader == null) classLoader = XposedBridge.BOOTCLASSLOADER
//        try {
//            return ClassUtils.getClass(classLoader, className, false)
//        } catch (e: ClassNotFoundException) {
//            throw ClassNotFoundError(e)
//        }
//    }
//
//    /**
//     * Look up and return a class if it exists.
//     * Like [.findClass], but doesn't throw an exception if the class doesn't exist.
//     *
//     * @param className The class name.
//     * @param classLoader The class loader, or `null` for the boot class loader.
//     * @return A reference to the class, or `null` if it doesn't exist.
//     */
//    fun findClassIfExists(className: String?, classLoader: ClassLoader?): Class<*>? {
//        try {
//            return findClass(className, classLoader)
//        } catch (e: ClassNotFoundError) {
//            return null
//        }
//    }
//
//    /**
//     * Look up a field in a class and set it to accessible.
//     *
//     * @param clazz The class which either declares or inherits the field.
//     * @param fieldName The field name.
//     * @return A reference to the field.
//     * @throws NoSuchFieldError In case the field was not found.
//     */
//    fun findField(clazz: Class<*>, fieldName: String): Field {
//        val fullFieldName = clazz.getName() + '#' + fieldName
//
//        if (fieldCache.containsKey(fullFieldName)) {
//            val field: Field = fieldCache.get(fullFieldName)
//            if (field == null) throw NoSuchFieldError(fullFieldName)
//            return field
//        }
//
//        try {
//            val field: Field = findFieldRecursiveImpl(clazz, fieldName)
//            field.setAccessible(true)
//            fieldCache.put(fullFieldName, field)
//            return field
//        } catch (e: NoSuchFieldException) {
//            fieldCache.put(fullFieldName, null)
//            throw NoSuchFieldError(fullFieldName)
//        }
//    }
//
//    /**
//     * Look up and return a field if it exists.
//     * Like [.findField], but doesn't throw an exception if the field doesn't exist.
//     *
//     * @param clazz The class which either declares or inherits the field.
//     * @param fieldName The field name.
//     * @return A reference to the field, or `null` if it doesn't exist.
//     */
//    fun findFieldIfExists(clazz: Class<*>, fieldName: String): Field? {
//        try {
//            return findField(clazz, fieldName)
//        } catch (e: NoSuchFieldError) {
//            return null
//        }
//    }
//
//    @Throws(NoSuchFieldException::class)
//    private fun findFieldRecursiveImpl(clazz: Class<*>?, fieldName: String): Field {
//        var clazz = clazz
//        try {
//            return clazz!!.getDeclaredField(fieldName)
//        } catch (e: NoSuchFieldException) {
//            while (true) {
//                clazz = clazz!!.getSuperclass()
//                if (clazz == null || clazz == Any::class.java) break
//
//                try {
//                    return clazz.getDeclaredField(fieldName)
//                } catch (ignored: NoSuchFieldException) {
//                }
//            }
//            throw e
//        }
//    }
//
//    /**
//     * Returns the first field of the given type in a class.
//     * Might be useful for Proguard'ed classes to identify fields with unique types.
//     *
//     * @param clazz The class which either declares or inherits the field.
//     * @param type The type of the field.
//     * @return A reference to the first field of the given type.
//     * @throws NoSuchFieldError In case no matching field was not found.
//     */
//    fun findFirstFieldByExactType(clazz: Class<*>, type: Class<*>): Field {
//        var clz = clazz
//        do {
//            for (field in clz.getDeclaredFields()) {
//                if (field.getType() == type) {
//                    field.setAccessible(true)
//                    return field
//                }
//            }
//        } while ((clz.getSuperclass().also { clz = it }) != null)
//
//        throw NoSuchFieldError("Field of type " + type.getName() + " in class " + clazz.getName())
//    }
//
//    /**
//     * Look up a method and hook it. See [.findAndHookMethod]
//     * for details.
//     */
//    fun findAndHookMethod(
//        clazz: Class<*>,
//        methodName: String,
//        vararg parameterTypesAndCallback: Any
//    ): XC_MethodHook.Unhook {
//        require(!(parameterTypesAndCallback.size == 0 || parameterTypesAndCallback[parameterTypesAndCallback.size - 1] !is XC_MethodHook)) { "no callback defined" }
//
//        val callback =
//            parameterTypesAndCallback[parameterTypesAndCallback.size - 1] as XC_MethodHook?
//        val m = findMethodExact(
//            clazz,
//            methodName,
//            *getParameterClasses(clazz.getClassLoader(), parameterTypesAndCallback)
//        )
//
//        return XposedBridge.hookMethod(m, callback)
//    }
//
//    /**
//     * Look up a method and hook it. The last argument must be the callback for the hook.
//     *
//     *
//     * This combines calls to [.findMethodExact] and
//     * [XposedBridge.hookMethod].
//     *
//     *
//     * The method must be declared or overridden in the given class, inherited
//     * methods are not considered! That's because each method implementation exists only once in
//     * the memory, and when classes inherit it, they just get another reference to the implementation.
//     * Hooking a method therefore applies to all classes inheriting the same implementation. You
//     * have to expect that the hook applies to subclasses (unless they override the method), but you
//     * shouldn't have to worry about hooks applying to superclasses, hence this "limitation".
//     * There could be undesired or even dangerous hooks otherwise, e.g. if you hook
//     * `SomeClass.equals()` and that class doesn't override the `equals()` on some ROMs,
//     * making you hook `Object.equals()` instead.
//     *
//     *
//     * There are two ways to specify the parameter types. If you already have a reference to the
//     * [Class], use that. For Android framework classes, you can often use something like
//     * `String.class`. If you don't have the class reference, you can simply use the
//     * full class name as a string, e.g. `java.lang.String` or `com.example.MyClass`.
//     * It will be passed to [.findClass] with the same class loader that is used for the target
//     * method, see its documentation for the allowed notations.
//     *
//     *
//     * Primitive types, such as `int`, can be specified using `int.class` (recommended)
//     * or `Integer.TYPE`. Note that `Integer.class` doesn't refer to `int` but to
//     * `Integer`, which is a normal class (boxed primitive). Therefore it must not be used when
//     * the method expects an `int` parameter - it has to be used for `Integer` parameters
//     * though, so check the method signature in detail.
//     *
//     *
//     * As last argument to this method (after the list of target method parameters), you need
//     * to specify the callback that should be executed when the method is invoked. It's usually
//     * an anonymous subclass of [XC_MethodHook] or [XC_MethodReplacement].
//     *
//     *
//     * **Example**
//     * <pre class="prettyprint">
//     * // In order to hook this method ...
//     * package com.example;
//     * public class SomeClass {
//     * public int doSomething(String s, int i, MyClass m) {
//     * ...
//     * }
//     * }
//     *
//     * // ... you can use this call:
//     * findAndHookMethod("com.example.SomeClass", lpparam.classLoader, String.class, int.class, "com.example.MyClass", new XC_MethodHook() {
//     * &#64;Override
//     * protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//     * String oldText = (String) param.args[0];
//     * Log.d("MyModule", oldText);
//     *
//     * param.args[0] = "test";
//     * param.args[1] = 42; // auto-boxing is working here
//     * setBooleanField(param.args[2], "great", true);
//     *
//     * // This would not work (as MyClass can't be resolved at compile time):
//     * //   MyClass myClass = (MyClass) param.args[2];
//     * //   myClass.great = true;
//     * }
//     * });
//    </pre> *
//     *
//     * @param className The name of the class which implements the method.
//     * @param classLoader The class loader for resolving the target and parameter classes.
//     * @param methodName The target method name.
//     * @param parameterTypesAndCallback The parameter types of the target method, plus the callback.
//     * @throws NoSuchMethodError In case the method was not found.
//     * @throws ClassNotFoundError In case the target class or one of the parameter types couldn't be resolved.
//     * @return An object which can be used to remove the callback again.
//     */
//    fun findAndHookMethod(
//        className: String?,
//        classLoader: ClassLoader?,
//        methodName: String,
//        vararg parameterTypesAndCallback: Any
//    ): XC_MethodHook.Unhook {
//        return findAndHookMethod(
//            findClass(className, classLoader),
//            methodName,
//            *parameterTypesAndCallback
//        )
//    }
//
//    /**
//     * Look up a method in a class and set it to accessible.
//     * See [.findMethodExact] for details.
//     */
//    fun findMethodExact(clazz: Class<*>, methodName: String, vararg parameterTypes: Any): Method {
//        return findMethodExact(
//            clazz,
//            methodName,
//            *getParameterClasses(clazz.getClassLoader(), parameterTypes)
//        )
//    }
//
//    /**
//     * Look up and return a method if it exists.
//     * See [.findMethodExactIfExists] for details.
//     */
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
//    /**
//     * Look up a method in a class and set it to accessible.
//     * The method must be declared or overridden in the given class.
//     *
//     *
//     * See [.findAndHookMethod] for details about
//     * the method and parameter type resolution.
//     *
//     * @param className The name of the class which implements the method.
//     * @param classLoader The class loader for resolving the target and parameter classes.
//     * @param methodName The target method name.
//     * @param parameterTypes The parameter types of the target method.
//     * @throws NoSuchMethodError In case the method was not found.
//     * @throws ClassNotFoundError In case the target class or one of the parameter types couldn't be resolved.
//     * @return A reference to the method.
//     */
//    fun findMethodExact(
//        className: String?,
//        classLoader: ClassLoader?,
//        methodName: String,
//        vararg parameterTypes: Any
//    ): Method {
//        return findMethodExact(
//            findClass(className, classLoader),
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
//            return findMethodExact(
//                className,
//                classLoader,
//                methodName,
//                *parameterTypes
//            )
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
//            val method: Method = methodCache.get(fullMethodName)
//            if (method == null) throw NoSuchMethodError(fullMethodName)
//            return method
//        }
//
//        try {
//            val method = clazz.getDeclaredMethod(methodName, *parameterTypes)
//            method.setAccessible(true)
//            methodCache.put(fullMethodName, method)
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
//            if (method == null) throw NoSuchMethodError(fullMethodName)
//            return method
//        }
//
//        try {
//            val method = findMethodExact(clazz, methodName, *parameterTypes)
//            methodCache.put(fullMethodName, method)
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
//    /**
//     * Look up a method in a class and set it to accessible.
//     *
//     *
//     * See [.findMethodBestMatch] for details. This variant
//     * determines the parameter types from the classes of the given objects.
//     */
//    fun findMethodBestMatch(clazz: Class<*>, methodName: String, vararg args: Any?): Method {
//        return findMethodBestMatch(
//            clazz,
//            methodName,
//            *getParameterTypes(*args)
//        )
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
//    /**
//     * Retrieve classes from an array, where each element might either be a Class
//     * already, or a String with the full class name.
//     */
//    private fun getParameterClasses(
//        classLoader: ClassLoader?,
//        parameterTypesAndCallback: Array<Any>
//    ): Array<Class<*>?> {
//        var parameterClasses: Array<Class<*>?>? = null
//        for (i in parameterTypesAndCallback.indices.reversed()) {
//            val type = parameterTypesAndCallback[i] ?: throw ClassNotFoundError(
//                "parameter type must not be null",
//                null
//            )
//
//            // ignore trailing callback
//            if (type is XC_MethodHook) continue
//
//            if (parameterClasses == null) parameterClasses = arrayOfNulls<Class<*>>(i + 1)
//
//            if (type is Class<*>) parameterClasses[i] = type
//            else if (type is String) parameterClasses[i] =
//                findClass(type, classLoader)
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
//
//    /**
//     * Returns an array of the given classes.
//     */
//    fun getClassesAsArray(vararg clazzes: Class<*>?): Array<Class<*>?>? {
//        return clazzes
//    }
//
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
//    /**
//     * Look up a constructor of a class and set it to accessible.
//     * See [.findMethodExact] for details.
//     */
//    fun findConstructorExact(clazz: Class<*>, vararg parameterTypes: Any): Constructor<*> {
//        return findConstructorExact(
//            clazz,
//            *getParameterClasses(clazz.getClassLoader(), parameterTypes)
//        )
//    }
//
//    /**
//     * Look up and return a constructor if it exists.
//     * See [.findMethodExactIfExists] for details.
//     */
//    fun findConstructorExactIfExists(clazz: Class<*>, vararg parameterTypes: Any): Constructor<*>? {
//        try {
//            return findConstructorExact(clazz, *parameterTypes)
//        } catch (e: NoSuchMethodError) {
//            return null
//        } catch (e: ClassNotFoundError) {
//            return null
//        }
//    }
//
//    /**
//     * Look up a constructor of a class and set it to accessible.
//     * See [.findMethodExact] for details.
//     */
//    fun findConstructorExact(
//        className: String?,
//        classLoader: ClassLoader?,
//        vararg parameterTypes: Any
//    ): Constructor<*> {
//        return findConstructorExact(
//            findClass(className, classLoader),
//            *getParameterClasses(classLoader, parameterTypes)
//        )
//    }
//
//    /**
//     * Look up and return a constructor if it exists.
//     * See [.findMethodExactIfExists] for details.
//     */
//    fun findConstructorExactIfExists(
//        className: String?,
//        classLoader: ClassLoader?,
//        vararg parameterTypes: Any
//    ): Constructor<*>? {
//        try {
//            return findConstructorExact(className, classLoader, *parameterTypes)
//        } catch (e: NoSuchMethodError) {
//            return null
//        } catch (e: ClassNotFoundError) {
//            return null
//        }
//    }
//
//    /**
//     * Look up a constructor of a class and set it to accessible.
//     * See [.findMethodExact] for details.
//     */
//    fun findConstructorExact(clazz: Class<*>, vararg parameterTypes: Class<*>?): Constructor<*> {
//        val fullConstructorName =
//            clazz.getName() + getParametersString(*parameterTypes) + "#exact"
//
//        if (constructorCache.containsKey(fullConstructorName)) {
//            val constructor: Constructor<*> =
//                constructorCache.get(fullConstructorName)
//            if (constructor == null) throw NoSuchMethodError(fullConstructorName)
//            return constructor
//        }
//
//        try {
//            val constructor: Constructor<*> = clazz.getDeclaredConstructor(*parameterTypes)
//            constructor.setAccessible(true)
//            constructorCache.put(fullConstructorName, constructor)
//            return constructor
//        } catch (e: NoSuchMethodException) {
//            constructorCache.put(fullConstructorName, null)
//            throw NoSuchMethodError(fullConstructorName)
//        }
//    }
//
//    /**
//     * Look up a constructor and hook it. See [.findAndHookMethod]
//     * for details.
//     */
//    fun findAndHookConstructor(
//        clazz: Class<*>,
//        vararg parameterTypesAndCallback: Any
//    ): XC_MethodHook.Unhook {
//        require(!(parameterTypesAndCallback.size == 0 || parameterTypesAndCallback[parameterTypesAndCallback.size - 1] !is XC_MethodHook)) { "no callback defined" }
//
//        val callback =
//            parameterTypesAndCallback[parameterTypesAndCallback.size - 1] as XC_MethodHook?
//        val m = findConstructorExact(
//            clazz,
//            *getParameterClasses(clazz.getClassLoader(), parameterTypesAndCallback)
//        )
//
//        return XposedBridge.hookMethod(m, callback)
//    }
//
//    /**
//     * Look up a constructor and hook it. See [.findAndHookMethod]
//     * for details.
//     */
//    fun findAndHookConstructor(
//        className: String?,
//        classLoader: ClassLoader?,
//        vararg parameterTypesAndCallback: Any
//    ): XC_MethodHook.Unhook {
//        return findAndHookConstructor(
//            findClass(className, classLoader),
//            *parameterTypesAndCallback
//        )
//    }
//
//    /**
//     * Look up a constructor in a class and set it to accessible.
//     *
//     *
//     * See [.findMethodBestMatch] for details.
//     */
//    fun findConstructorBestMatch(
//        clazz: Class<*>,
//        vararg parameterTypes: Class<*>?
//    ): Constructor<*> {
//        val fullConstructorName =
//            clazz.getName() + getParametersString(*parameterTypes) + "#bestmatch"
//
//        if (constructorCache.containsKey(fullConstructorName)) {
//            val constructor: Constructor<*> =
//                constructorCache.get(fullConstructorName)
//            if (constructor == null) throw NoSuchMethodError(fullConstructorName)
//            return constructor
//        }
//
//        try {
//            val constructor = findConstructorExact(clazz, *parameterTypes)
//            constructorCache.put(fullConstructorName, constructor)
//            return constructor
//        } catch (ignored: NoSuchMethodError) {
//        }
//
//        var bestMatch: Constructor<*>? = null
//        val constructors = clazz.getDeclaredConstructors()
//        for (constructor in constructors) {
//            // compare name and parameters
//            if (ClassUtils.isAssignable(parameterTypes, constructor.getParameterTypes(), true)) {
//                // get accessible version of method
//                if (bestMatch == null || MemberUtils.compareParameterTypes(
//                        constructor.getParameterTypes(),
//                        bestMatch.getParameterTypes(),
//                        parameterTypes
//                    ) < 0
//                ) {
//                    bestMatch = constructor
//                }
//            }
//        }
//
//        if (bestMatch != null) {
//            bestMatch.setAccessible(true)
//            constructorCache.put(fullConstructorName, bestMatch)
//            return bestMatch
//        } else {
//            val e = NoSuchMethodError(fullConstructorName)
//            constructorCache.put(fullConstructorName, null)
//            throw e
//        }
//    }
//
//    /**
//     * Look up a constructor in a class and set it to accessible.
//     *
//     *
//     * See [.findMethodBestMatch] for details. This variant
//     * determines the parameter types from the classes of the given objects.
//     */
//    fun findConstructorBestMatch(clazz: Class<*>, vararg args: Any?): Constructor<*> {
//        return findConstructorBestMatch(
//            clazz,
//            *getParameterTypes(*args)
//        )
//    }
//
//    /**
//     * Look up a constructor in a class and set it to accessible.
//     *
//     *
//     * See [.findMethodBestMatch] for details. This variant
//     * determines the parameter types from the classes of the given objects. For any item that is
//     * `null`, the type is taken from `parameterTypes` instead.
//     */
//    fun findConstructorBestMatch(
//        clazz: Class<*>,
//        parameterTypes: Array<Class<*>?>,
//        args: Array<Any?>
//    ): Constructor<*> {
//        var argsClasses: Array<Class<*>?>? = null
//        for (i in parameterTypes.indices) {
//            if (parameterTypes[i] != null) continue
//            if (argsClasses == null) argsClasses = getParameterTypes(*args)
//            parameterTypes[i] = argsClasses[i]
//        }
//        return findConstructorBestMatch(clazz, *parameterTypes)
//    }
//
//    //#################################################################################################
//    /** Sets the value of an object field in the given object instance. A class reference is not sufficient! See also [.findField].  */
//    fun setObjectField(obj: Any, fieldName: String, value: Any?) {
//        try {
//            findField(obj.javaClass, fieldName).set(obj, value)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of a `boolean` field in the given object instance. A class reference is not sufficient! See also [.findField].  */
//    fun setBooleanField(obj: Any, fieldName: String, value: Boolean) {
//        try {
//            findField(obj.javaClass, fieldName).setBoolean(obj, value)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of a `byte` field in the given object instance. A class reference is not sufficient! See also [.findField].  */
//    fun setByteField(obj: Any, fieldName: String, value: Byte) {
//        try {
//            findField(obj.javaClass, fieldName).setByte(obj, value)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of a `char` field in the given object instance. A class reference is not sufficient! See also [.findField].  */
//    fun setCharField(obj: Any, fieldName: String, value: Char) {
//        try {
//            findField(obj.javaClass, fieldName).setChar(obj, value)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of a `double` field in the given object instance. A class reference is not sufficient! See also [.findField].  */
//    fun setDoubleField(obj: Any, fieldName: String, value: Double) {
//        try {
//            findField(obj.javaClass, fieldName).setDouble(obj, value)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of a `float` field in the given object instance. A class reference is not sufficient! See also [.findField].  */
//    fun setFloatField(obj: Any, fieldName: String, value: Float) {
//        try {
//            findField(obj.javaClass, fieldName).setFloat(obj, value)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of an `int` field in the given object instance. A class reference is not sufficient! See also [.findField].  */
//    fun setIntField(obj: Any, fieldName: String, value: Int) {
//        try {
//            findField(obj.javaClass, fieldName).setInt(obj, value)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of a `long` field in the given object instance. A class reference is not sufficient! See also [.findField].  */
//    fun setLongField(obj: Any, fieldName: String, value: Long) {
//        try {
//            findField(obj.javaClass, fieldName).setLong(obj, value)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of a `short` field in the given object instance. A class reference is not sufficient! See also [.findField].  */
//    fun setShortField(obj: Any, fieldName: String, value: Short) {
//        try {
//            findField(obj.javaClass, fieldName).setShort(obj, value)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    //#################################################################################################
//    /** Returns the value of an object field in the given object instance. A class reference is not sufficient! See also [.findField].  */
//    fun getObjectField(obj: Any, fieldName: String): Any? {
//        try {
//            return findField(obj.javaClass, fieldName).get(obj)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** For inner classes, returns the surrounding instance, i.e. the `this` reference of the surrounding class.  */
//    fun getSurroundingThis(obj: Any): Any? {
//        return getObjectField(obj, "this$0")
//    }
//
//    /** Returns the value of a `boolean` field in the given object instance. A class reference is not sufficient! See also [.findField].  */
//    fun getBooleanField(obj: Any, fieldName: String): Boolean {
//        try {
//            return findField(obj.javaClass, fieldName).getBoolean(obj)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Returns the value of a `byte` field in the given object instance. A class reference is not sufficient! See also [.findField].  */
//    fun getByteField(obj: Any, fieldName: String): Byte {
//        try {
//            return findField(obj.javaClass, fieldName).getByte(obj)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Returns the value of a `char` field in the given object instance. A class reference is not sufficient! See also [.findField].  */
//    fun getCharField(obj: Any, fieldName: String): Char {
//        try {
//            return findField(obj.javaClass, fieldName).getChar(obj)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Returns the value of a `double` field in the given object instance. A class reference is not sufficient! See also [.findField].  */
//    fun getDoubleField(obj: Any, fieldName: String): Double {
//        try {
//            return findField(obj.javaClass, fieldName).getDouble(obj)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Returns the value of a `float` field in the given object instance. A class reference is not sufficient! See also [.findField].  */
//    fun getFloatField(obj: Any, fieldName: String): Float {
//        try {
//            return findField(obj.javaClass, fieldName).getFloat(obj)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Returns the value of an `int` field in the given object instance. A class reference is not sufficient! See also [.findField].  */
//    fun getIntField(obj: Any, fieldName: String): Int {
//        try {
//            return findField(obj.javaClass, fieldName).getInt(obj)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Returns the value of a `long` field in the given object instance. A class reference is not sufficient! See also [.findField].  */
//    fun getLongField(obj: Any, fieldName: String): Long {
//        try {
//            return findField(obj.javaClass, fieldName).getLong(obj)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Returns the value of a `short` field in the given object instance. A class reference is not sufficient! See also [.findField].  */
//    fun getShortField(obj: Any, fieldName: String): Short {
//        try {
//            return findField(obj.javaClass, fieldName).getShort(obj)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    //#################################################################################################
//    /** Sets the value of a static object field in the given class. See also [.findField].  */
//    fun setStaticObjectField(clazz: Class<*>, fieldName: String, value: Any?) {
//        try {
//            findField(clazz, fieldName).set(null, value)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of a static `boolean` field in the given class. See also [.findField].  */
//    fun setStaticBooleanField(clazz: Class<*>, fieldName: String, value: Boolean) {
//        try {
//            findField(clazz, fieldName).setBoolean(null, value)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of a static `byte` field in the given class. See also [.findField].  */
//    fun setStaticByteField(clazz: Class<*>, fieldName: String, value: Byte) {
//        try {
//            findField(clazz, fieldName).setByte(null, value)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of a static `char` field in the given class. See also [.findField].  */
//    fun setStaticCharField(clazz: Class<*>, fieldName: String, value: Char) {
//        try {
//            findField(clazz, fieldName).setChar(null, value)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of a static `double` field in the given class. See also [.findField].  */
//    fun setStaticDoubleField(clazz: Class<*>, fieldName: String, value: Double) {
//        try {
//            findField(clazz, fieldName).setDouble(null, value)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of a static `float` field in the given class. See also [.findField].  */
//    fun setStaticFloatField(clazz: Class<*>, fieldName: String, value: Float) {
//        try {
//            findField(clazz, fieldName).setFloat(null, value)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of a static `int` field in the given class. See also [.findField].  */
//    fun setStaticIntField(clazz: Class<*>, fieldName: String, value: Int) {
//        try {
//            findField(clazz, fieldName).setInt(null, value)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of a static `long` field in the given class. See also [.findField].  */
//    fun setStaticLongField(clazz: Class<*>, fieldName: String, value: Long) {
//        try {
//            findField(clazz, fieldName).setLong(null, value)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of a static `short` field in the given class. See also [.findField].  */
//    fun setStaticShortField(clazz: Class<*>, fieldName: String, value: Short) {
//        try {
//            findField(clazz, fieldName).setShort(null, value)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    //#################################################################################################
//    /** Returns the value of a static object field in the given class. See also [.findField].  */
//    fun getStaticObjectField(clazz: Class<*>, fieldName: String): Any? {
//        try {
//            return findField(clazz, fieldName).get(null)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Returns the value of a static `boolean` field in the given class. See also [.findField].  */
//    fun getStaticBooleanField(clazz: Class<*>, fieldName: String): Boolean {
//        try {
//            return findField(clazz, fieldName).getBoolean(null)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of a static `byte` field in the given class. See also [.findField].  */
//    fun getStaticByteField(clazz: Class<*>, fieldName: String): Byte {
//        try {
//            return findField(clazz, fieldName).getByte(null)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of a static `char` field in the given class. See also [.findField].  */
//    fun getStaticCharField(clazz: Class<*>, fieldName: String): Char {
//        try {
//            return findField(clazz, fieldName).getChar(null)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of a static `double` field in the given class. See also [.findField].  */
//    fun getStaticDoubleField(clazz: Class<*>, fieldName: String): Double {
//        try {
//            return findField(clazz, fieldName).getDouble(null)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of a static `float` field in the given class. See also [.findField].  */
//    fun getStaticFloatField(clazz: Class<*>, fieldName: String): Float {
//        try {
//            return findField(clazz, fieldName).getFloat(null)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of a static `int` field in the given class. See also [.findField].  */
//    fun getStaticIntField(clazz: Class<*>, fieldName: String): Int {
//        try {
//            return findField(clazz, fieldName).getInt(null)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of a static `long` field in the given class. See also [.findField].  */
//    fun getStaticLongField(clazz: Class<*>, fieldName: String): Long {
//        try {
//            return findField(clazz, fieldName).getLong(null)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    /** Sets the value of a static `short` field in the given class. See also [.findField].  */
//    fun getStaticShortField(clazz: Class<*>, fieldName: String): Short {
//        try {
//            return findField(clazz, fieldName).getShort(null)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        }
//    }
//
//    //#################################################################################################
//    /**
//     * Calls an instance or static method of the given object.
//     * The method is resolved using [.findMethodBestMatch].
//     *
//     * @param obj The object instance. A class reference is not sufficient!
//     * @param methodName The method name.
//     * @param args The arguments for the method call.
//     * @throws NoSuchMethodError In case no suitable method was found.
//     * @throws InvocationTargetError In case an exception was thrown by the invoked method.
//     */
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
//            throw InvocationTargetError(e.cause)
//        }
//    }
//
//    /**
//     * Calls an instance or static method of the given object.
//     * See [.callMethod].
//     *
//     *
//     * This variant allows you to specify parameter types, which can help in case there are multiple
//     * methods with the same name, especially if you call it with `null` parameters.
//     */
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
//            throw InvocationTargetError(e.cause)
//        }
//    }
//
//    /**
//     * Calls a static method of the given class.
//     * The method is resolved using [.findMethodBestMatch].
//     *
//     * @param clazz The class reference.
//     * @param methodName The method name.
//     * @param args The arguments for the method call.
//     * @throws NoSuchMethodError In case no suitable method was found.
//     * @throws InvocationTargetError In case an exception was thrown by the invoked method.
//     */
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
//            throw InvocationTargetError(e.cause)
//        }
//    }
//
//    /**
//     * Calls a static method of the given class.
//     * See [.callStaticMethod].
//     *
//     *
//     * This variant allows you to specify parameter types, which can help in case there are multiple
//     * methods with the same name, especially if you call it with `null` parameters.
//     */
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
//            throw InvocationTargetError(e.cause)
//        }
//    }
//
//    //#################################################################################################
//    /**
//     * Creates a new instance of the given class.
//     * The constructor is resolved using [.findConstructorBestMatch].
//     *
//     * @param clazz The class reference.
//     * @param args The arguments for the constructor call.
//     * @throws NoSuchMethodError In case no suitable constructor was found.
//     * @throws InvocationTargetError In case an exception was thrown by the invoked method.
//     * @throws InstantiationError In case the class cannot be instantiated.
//     */
//    fun newInstance(clazz: Class<*>, vararg args: Any?): Any {
//        try {
//            return findConstructorBestMatch(clazz, *args).newInstance(*args)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        } catch (e: InvocationTargetException) {
//            throw InvocationTargetError(e.cause)
//        } catch (e: InstantiationException) {
//            throw InstantiationError(e.message)
//        }
//    }
//
//    /**
//     * Creates a new instance of the given class.
//     * See [.newInstance].
//     *
//     *
//     * This variant allows you to specify parameter types, which can help in case there are multiple
//     * constructors with the same name, especially if you call it with `null` parameters.
//     */
//    fun newInstance(clazz: Class<*>, parameterTypes: Array<Class<*>?>, vararg args: Any?): Any {
//        try {
//            return findConstructorBestMatch(clazz, parameterTypes, args)
//                .newInstance(*args)
//        } catch (e: IllegalAccessException) {
//            // should not happen
//            XposedBridge.log(e)
//            throw IllegalAccessError(e.message)
//        } catch (e: IllegalArgumentException) {
//            throw e
//        } catch (e: InvocationTargetException) {
//            throw InvocationTargetError(e.cause)
//        } catch (e: InstantiationException) {
//            throw InstantiationError(e.message)
//        }
//    }
//
//    //#################################################################################################
//    /**
//     * Attaches any value to an object instance. This simulates adding an instance field.
//     * The value can be retrieved again with [.getAdditionalInstanceField].
//     *
//     * @param obj The object instance for which the value should be stored.
//     * @param key The key in the value map for this object instance.
//     * @param value The value to store.
//     * @return The previously stored value for this instance/key combination, or `null` if there was none.
//     */
//    fun setAdditionalInstanceField(obj: Any, key: String, value: Any?): Any? {
//        if (obj == null) throw NullPointerException("object must not be null")
//        if (key == null) throw NullPointerException("key must not be null")
//
//        var objectFields: HashMap<String?, Any?>?
//        synchronized(additionalFields) {
//            objectFields = additionalFields.get(obj)
//            if (objectFields == null) {
//                objectFields = HashMap<String?, Any?>()
//                additionalFields.put(obj, objectFields)
//            }
//        }
//
//        synchronized(objectFields!!) {
//            return objectFields.put(key, value)
//        }
//    }
//
//    /**
//     * Returns a value which was stored with [.setAdditionalInstanceField].
//     *
//     * @param obj The object instance for which the value has been stored.
//     * @param key The key in the value map for this object instance.
//     * @return The stored value for this instance/key combination, or `null` if there is none.
//     */
//    fun getAdditionalInstanceField(obj: Any, key: String): Any? {
//        if (obj == null) throw NullPointerException("object must not be null")
//        if (key == null) throw NullPointerException("key must not be null")
//
//        val objectFields: HashMap<String?, Any?>?
//        synchronized(additionalFields) {
//            objectFields = additionalFields.get(obj)
//            if (objectFields == null) return null
//        }
//
//        synchronized(objectFields!!) {
//            return objectFields.get(key)
//        }
//    }
//
//    /**
//     * Removes and returns a value which was stored with [.setAdditionalInstanceField].
//     *
//     * @param obj The object instance for which the value has been stored.
//     * @param key The key in the value map for this object instance.
//     * @return The previously stored value for this instance/key combination, or `null` if there was none.
//     */
//    fun removeAdditionalInstanceField(obj: Any, key: String): Any? {
//        if (obj == null) throw NullPointerException("object must not be null")
//        if (key == null) throw NullPointerException("key must not be null")
//
//        val objectFields: HashMap<String?, Any?>?
//        synchronized(additionalFields) {
//            objectFields = additionalFields.get(obj)
//            if (objectFields == null) return null
//        }
//
//        synchronized(objectFields!!) {
//            return objectFields.remove(key)
//        }
//    }
//
//    /** Like [.setAdditionalInstanceField], but the value is stored for the class of `obj`.  */
//    fun setAdditionalStaticField(obj: Any, key: String, value: Any?): Any? {
//        return setAdditionalInstanceField(obj.javaClass, key, value)
//    }
//
//    /** Like [.getAdditionalInstanceField], but the value is returned for the class of `obj`.  */
//    fun getAdditionalStaticField(obj: Any, key: String): Any? {
//        return getAdditionalInstanceField(obj.javaClass, key)
//    }
//
//    /** Like [.removeAdditionalInstanceField], but the value is removed and returned for the class of `obj`.  */
//    fun removeAdditionalStaticField(obj: Any, key: String): Any? {
//        return removeAdditionalInstanceField(obj.javaClass, key)
//    }
//
//    /** Like [.setAdditionalInstanceField], but the value is stored for `clazz`.  */
//    fun setAdditionalStaticField(clazz: Class<*>, key: String, value: Any?): Any? {
//        return setAdditionalInstanceField(clazz, key, value)
//    }
//
//    /** Like [.setAdditionalInstanceField], but the value is returned for `clazz`.  */
//    fun getAdditionalStaticField(clazz: Class<*>, key: String): Any? {
//        return getAdditionalInstanceField(clazz, key)
//    }
//
//    /** Like [.setAdditionalInstanceField], but the value is removed and returned for `clazz`.  */
//    fun removeAdditionalStaticField(clazz: Class<*>, key: String): Any? {
//        return removeAdditionalInstanceField(clazz, key)
//    }
//
//    //#################################################################################################
//    /**
//     * Loads an asset from a resource object and returns the content as `byte` array.
//     *
//     * @param res The resources from which the asset should be loaded.
//     * @param path The path to the asset, as in [AssetManager.open].
//     * @return The content of the asset.
//     */
//    @Throws(IOException::class)
//    fun assetAsByteArray(res: Resources, path: String): ByteArray {
//        val `is` = res.getAssets().open(path)
//
//        val buf = ByteArrayOutputStream()
//        val temp = ByteArray(1024)
//        var read: Int
//
//        while ((`is`.read(temp).also { read = it }) > 0) {
//            buf.write(temp, 0, read)
//        }
//        `is`.close()
//        return buf.toByteArray()
//    }
//
//    /**
//     * Returns the lowercase hex string representation of a file's MD5 hash sum.
//     */
//    @Throws(IOException::class)
//    fun getMD5Sum(file: String): String {
//        try {
//            val digest = MessageDigest.getInstance("MD5")
//            val `is`: InputStream = FileInputStream(file)
//            val buffer = ByteArray(8192)
//            var read: Int
//            while ((`is`.read(buffer).also { read = it }) > 0) {
//                digest.update(buffer, 0, read)
//            }
//            `is`.close()
//            val md5sum = digest.digest()
//            val bigInt = BigInteger(1, md5sum)
//            return bigInt.toString(16)
//        } catch (e: NoSuchAlgorithmException) {
//            return ""
//        }
//    }
//
//    //#################################################################################################
//    /**
//     * Increments the depth counter for the given method.
//     *
//     *
//     * The intention of the method depth counter is to keep track of the call depth for recursive
//     * methods, e.g. to override parameters only for the outer call. The Xposed framework uses this
//     * to load drawable replacements only once per call, even when multiple
//     * [Resources.getDrawable] variants call each other.
//     *
//     * @param method The method name. Should be prefixed with a unique, module-specific string.
//     * @return The updated depth.
//     */
//    fun incrementMethodDepth(method: String?): Int {
//        return getMethodDepthCounter(method).get().incrementAndGet()
//    }
//
//    /**
//     * Decrements the depth counter for the given method.
//     * See [.incrementMethodDepth] for details.
//     *
//     * @param method The method name. Should be prefixed with a unique, module-specific string.
//     * @return The updated depth.
//     */
//    fun decrementMethodDepth(method: String): Int {
//        return getMethodDepthCounter(method).get().decrementAndGet()
//    }
//
//    /**
//     * Returns the current depth counter for the given method.
//     * See [.incrementMethodDepth] for details.
//     *
//     * @param method The method name. Should be prefixed with a unique, module-specific string.
//     * @return The updated depth.
//     */
//    fun getMethodDepth(method: String): Int? {
//        return getMethodDepthCounter(method).get()?.get()
//    }
//
//    private fun getMethodDepthCounter(method: String): ThreadLocal<AtomicInteger> {
//        synchronized(sMethodDepth) {
//            var counter = sMethodDepth[method]
//            if (counter == null) {
//                counter = object : ThreadLocal<AtomicInteger>() {
//                    override fun initialValue(): AtomicInteger {
//                        return AtomicInteger()
//                    }
//                }
//                sMethodDepth[method] = counter
//            }
//            return counter
//        }
//    } //#################################################################################################
//    // TODO helpers for view traversing
//    /*To make it easier, I will try and implement some more helpers:
//	- add view before/after existing view (I already mentioned that I think)
//	- get index of view in its parent
//	- get next/previous sibling (maybe with an optional argument "type", that might be ImageView.class and gives you the next sibling that is an ImageView)?
//	- get next/previous element (similar to the above, but would also work if the next element has a different parent, it would just go up the hierarchy and then down again until it finds a matching element)
//	- find the first child that is an instance of a specified class
//	- find all (direct or indirect) children of a specified class
//	*/
//
//    /**
//     * Thrown when a class loader is unable to find a class. Unlike [ClassNotFoundException],
//     * callers are not forced to explicitly catch this. If uncaught, the error will be passed to the
//     * next caller in the stack.
//     */
//    class ClassNotFoundError : Error {
//        /** @hide
//         */
//        constructor(cause: Throwable?) : super(cause)
//
//        /** @hide
//         */
//        constructor(detailMessage: String?, cause: Throwable?) : super(detailMessage, cause)
//
//        companion object {
//            private val serialVersionUID = -1070936889459514628L
//        }
//    }
//
//    /**
//     * This class provides a wrapper for an exception thrown by a method invocation.
//     *
//     * @see .callMethod
//     * @see .callStaticMethod
//     * @see .newInstance
//     */
//    class InvocationTargetError
//    /** @hide
//     */
//        (cause: Throwable?) : Error(cause) {
//        companion object {
//            private val serialVersionUID = -1070936889459514628L
//        }
//    }
//}