//package com.yunzia.hyperstar.hook.tool
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.res.AssetManager
//import android.content.res.Resources
//import android.content.res.TypedArray
//import android.content.res.loader.ResourcesLoader
//import android.content.res.loader.ResourcesProvider
//import android.os.Handler
//import android.os.Looper
//import android.os.ParcelFileDescriptor
//import android.util.Pair
//import android.util.TypedValue
//import de.robv.android.xposed.XC_MethodHook
//import java.io.File
//import java.io.IOException
//import java.util.Arrays
//import java.util.Objects
//import java.util.concurrent.ConcurrentHashMap
//import java.util.function.Consumer
//
//class ResInjectTool {
//
//    var TAG: String = "ResInjectTool"
//    var resourcesLoader: ResourcesLoader? = null
//    var mModulePath: String? = null
//    var mHandler: Handler? = null
//
//    /**
//     * 请在 [com.hchen.hooktool.HCInit.initStartupParam] 处调用。
//     *
//     * @param modulePath startupParam.modulePath 即可
//     */
//    fun initResInjectTool(modulePath: String?) {
//        mModulePath = modulePath
//    }
//
//
//    fun getStackTrace(): String {
//        val stringBuilder = StringBuilder()
//        val stackTraceElements = Thread.currentThread().stackTrace
//        Arrays.stream(stackTraceElements).forEach(object : Consumer<StackTraceElement?> {
//            override fun accept(stackTraceElement: StackTraceElement) {
//                val clazz = stackTraceElement.className
//                val method = stackTraceElement.methodName
//                val field = stackTraceElement.fileName
//                val line = stackTraceElement.lineNumber
//                stringBuilder.append("\nat ").append(clazz).append(".")
//                    .append(method).append("(")
//                    .append(field).append(":")
//                    .append(line).append(")")
//            }
//        })
//        return stringBuilder.toString()
//    }
//
//    /**
//     * 把本项目资源注入目标作用域上下文。一般调用本方法即可。<br></br>
//     * 请在项目 app 下的 build.gradle 中添加如下代码：
//     * <pre> `Kotlin Gradle DSL:
//     *
//     * androidResources.additionalParameters("--allow-reserved-package-id", "--package-id", "0x64")
//     *
//     * Groovy:
//     *
//     * aaptOptions.additionalParameters '--allow-reserved-package-id', '--package-id', '0x64'
//     *
//    ` * <br></br>
//     * Tip: `0x64` is the resource id, you can change it to any value you want.(recommended [0x30 to 0x6F])
//    </pre> */
//    fun loadModuleRes(resources: Resources?, doOnMainLooper: Boolean): Resources? {
//        if (resources == null) {
//            starLog.logW(TAG, "Context can't is null!", getStackTrace())
//            return null
//        }
//        if (mModulePath == null) {
//            mModulePath = if (HCData.getModulePath() != null) HCData.getModulePath() else null
//            if (mModulePath == null) {
//                starLog.logW(TAG, "Module path is null, can't load module res!", getStackTrace())
//                return null
//            }
//        }
//        val load = loadResAboveApi30(resources, doOnMainLooper)
//        if (!load) {
//            /*try {
//                return getModuleRes(context);
//            } catch (PackageManager.NameNotFoundException e) {
//                logE(tag(), "failed to load resource! critical error!! scope may crash!!", e);
//            }*/
//        }
//        if (!resourcesArrayList.contains(resources)) resourcesArrayList.add(resources)
//        return resources
//    }
//
//    fun loadModuleRes(resources: Resources?): Resources? {
//        return loadModuleRes(resources, false)
//    }
//
//    fun loadModuleRes(context: Context, doOnMainLooper: Boolean): Resources? {
//        return loadModuleRes(context.resources, doOnMainLooper)
//    }
//
//    fun loadModuleRes(context: Context): Resources? {
//        return loadModuleRes(context, false)
//    }
//
//    /**
//     * 来自 QA 的方法
//     */
//    private fun loadResAboveApi30(resources: Resources, doOnMainLooper: Boolean): Boolean {
//        if (resourcesLoader == null) {
//            try {
//                ParcelFileDescriptor.open(
//                    File(mModulePath),
//                    ParcelFileDescriptor.MODE_READ_ONLY
//                ).use { pfd ->
//                    val provider = ResourcesProvider.loadFromApk(pfd)
//                    val loader = ResourcesLoader()
//                    loader.addProvider(provider)
//                    resourcesLoader = loader
//                }
//            } catch (e: IOException) {
//                starLog.logE(TAG, "Failed to add resource! debug: above api 30.", e)
//                return false
//            }
//        }
//        if (doOnMainLooper) if (Looper.myLooper() == Looper.getMainLooper()) {
//            return addLoaders(resources)
//        } else {
//            if (mHandler == null) {
//                mHandler = Handler(Looper.getMainLooper())
//            }
//            mHandler!!.post { addLoaders(resources) }
//            return true // 此状态下保持返回 true，请观察日志是否有报错来判断是否成功。
//        }
//        else return addLoaders(resources)
//    }
//
//    private fun addLoaders(resources: Resources): Boolean {
//        try {
//            resources.addLoaders(resourcesLoader)
//        } catch (e: IllegalArgumentException) {
//            val expected1 =
//                "Cannot modify resource loaders of ResourcesImpl not registered with ResourcesManager"
//            if (expected1 == e.message) {
//                // fallback to below API 30
//                return loadResBelowApi30(resources)
//            } else {
//                starLog.logE(TAG, "Failed to add loaders!", e)
//                return false
//            }
//        }
//        return true
//    }
//
//    /**
//     * @noinspection JavaReflectionMemberAccess
//     */
//    @SuppressLint("DiscouragedPrivateApi")
//    private fun loadResBelowApi30(resources: Resources): Boolean {
//        try {
//            val assets = resources.assets
//            val addAssetPath =
//                AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
//            addAssetPath.isAccessible = true
//            val cookie = addAssetPath.invoke(assets, mModulePath) as Int
//            if (cookie == null || cookie == 0) {
//                starLog.logW(
//                    TAG,
//                    "Method 'addAssetPath' result 0, maybe load res failed!",
//                    getStackTrace()
//                )
//                return false
//            }
//        } catch (e: Throwable) {
//            starLog.logE(TAG, "Failed to add resource! debug: below api 30.", e)
//            return false
//        }
//        return true
//    }
//
//    private static
//    val resourcesArrayList: MutableList<Resources> = ArrayList()
//    private static
//    val resMap: ConcurrentHashMap<Int, Boolean> = ConcurrentHashMap()
//    private static
//    val unhooks: MutableList<XC_MethodHook.Unhook> = ArrayList()
//    private static
//    val replacements: ConcurrentHashMap<String, Pair<ReplacementType, Any>> = ConcurrentHashMap()
//
//    private static
//    var hooked: Boolean = false
//
//    private fun ResInjectTool() {
//        hooked = false
//        resourcesArrayList.clear()
//        resMap.clear()
//        unhooks.clear()
//        replacements.clear()
//    }
//
//    enum class ReplacementType {
//        ID,
//        DENSITY,
//        OBJECT
//    }
//
//    fun getFakeResId(resName: String): Int {
//        return 0x7e000000 or (resName.hashCode() and 0x00ffffff)
//    }
//
//    fun getFakeResId(res: Resources, id: Int): Int {
//        return getFakeResId(res.getResourceName(id))
//    }
//
//    /**
//     * 设置资源 ID 类型的替换
//     */
//    fun setResReplacement(pkg: String, type: String, name: String, replacementResId: Int) {
//        try {
//            applyHooks()
//            replacements["$pkg:$type/$name"] =
//                Pair(ReplacementType.ID, replacementResId)
//        } catch (t: Throwable) {
//            starLog.logE(TAG, "Failed to set res replacement!", t)
//        }
//    }
//
//    /**
//     * 设置密度类型的资源
//     */
//    fun setDensityReplacement(pkg: String, type: String, name: String, replacementResValue: Float) {
//        try {
//            applyHooks()
//            replacements["$pkg:$type/$name"] = Pair(ReplacementType.DENSITY, replacementResValue)
//        } catch (t: Throwable) {
//            starLog.logE(TAG, "Failed to set density res replacement!", t)
//        }
//    }
//
//    /**
//     * 设置 Object 类型的资源
//     */
//    fun setObjectReplacement(pkg: String, type: String, name: String, replacementResValue: Any) {
//        try {
//            applyHooks()
//            replacements["$pkg:$type/$name"] = Pair(ReplacementType.OBJECT, replacementResValue)
//        } catch (t: Throwable) {
//            starLog.logE(TAG, "Failed to set object res replacement!", t)
//        }
//    }
//
//    private fun applyHooks() {
//        if (hooked) return
//        if (mModulePath == null) {
//            mModulePath = if (HCData.getModulePath() != null) HCData.getModulePath() else null
//            if (mModulePath == null) {
//                unHookRes()
//                throw RuntimeException(createRuntimeExceptionLog("Module path is null, Please init this in initStartupParam()!"))
//            }
//        }
//        val resMethods = Resources::class.java.declaredMethods
//        for (method in resMethods) {
//            val name = method.name
//            when (name) {
//                "getInteger", "getLayout", "getBoolean", "getDimension", "getDimensionPixelOffset", "getDimensionPixelSize", "getText", "getFloat", "getIntArray", "getStringArray", "getTextArray", "getAnimation" -> {
//                    if (method.parameterTypes.size == 1
//                        && method.parameterTypes[0] == Int::class.javaPrimitiveType
//                    ) {
//                        hookResMethod(
//                            method.name,
//                            Int::class.javaPrimitiveType!!, hookResBefore
//                        )
//                    }
//                }
//
//                "getColor" -> {
//                    if (method.parameterTypes.size == 2) {
//                        hookResMethod(
//                            method.name,
//                            Int::class.javaPrimitiveType!!,
//                            Resources.Theme::class.java, Optional.of(hookResBefore)
//                        )
//                    }
//                }
//
//                "getFraction" -> {
//                    if (method.parameterTypes.size == 3) {
//                        hookResMethod(
//                            method.name,
//                            Int::class.javaPrimitiveType!!,
//                            Int::class.javaPrimitiveType!!,
//                            Int::class.javaPrimitiveType!!, Optional.of(hookResBefore)
//                        )
//                    }
//                }
//
//                "getDrawableForDensity" -> {
//                    if (method.parameterTypes.size == 3) {
//                        hookResMethod(
//                            method.name,
//                            Int::class.javaPrimitiveType!!,
//                            Int::class.javaPrimitiveType!!,
//                            Resources.Theme::class.java, Optional.of(hookResBefore)
//                        )
//                    }
//                }
//            }
//        }
//
//        val typedMethod = TypedArray::class.java.declaredMethods
//        for (method in typedMethod) {
//            if (method.name == "getColor") {
//                hookTypedMethod(
//                    method.name,
//                    Int::class.javaPrimitiveType!!,
//                    Int::class.javaPrimitiveType!!, hookTypedBefore
//                )
//            }
//        }
//        hooked = true
//    }
//
//    private fun hookResMethod(name: String, vararg args: Any) {
//        unhooks.add(CoreTool.hookMethod(Resources::class.java, name, args))
//    }
//
//    private fun hookTypedMethod(name: String, vararg args: Any) {
//        unhooks.add(CoreTool.hookMethod(TypedArray::class.java, name, args))
//    }
//
//    fun unHookRes() {
//        if (unhooks.isEmpty()) {
//            hooked = false
//            return
//        }
//        for (unhook in unhooks) {
//            unhook.unhook()
//        }
//        unhooks.clear()
//        hooked = false
//    }
//
//    private static
//    val hookTypedBefore: IHook = object : IHook() {
//        override fun before() {
//            val index = getArgs(0) as Int
//            val mData = CoreTool.getField(thisObject(), "mData") as IntArray
//            val type = mData[index]
//            val id = mData[index + 3]
//
//            if (id != 0 && (type != TypedValue.TYPE_NULL)) {
//                val mResources = CoreTool.getField(thisObject(), "mResources") as Resources
//                val value = getTypedArrayReplacement(mResources, id)
//                if (value != null) {
//                    setResult(value)
//                }
//            }
//        }
//    }
//
//    private static
//    val Optional: IHook? = null.fun of() = new
//    fun IHook() {
//        var before: Unit
//        ()
//        run {
//            if (resourcesArrayList.isEmpty()) {
//                resourcesArrayList.add(loadModuleRes(ContextTool.getContext(FLAG_ALL)))
//            }
//            if (java.lang.Boolean.TRUE == resMap[getArgs(0) as Int]) {
//                return
//            }
//            for (resources in resourcesArrayList) {
//                if (resources == null) return
//                val method: String = mMember.getName()
//                var value: Any?
//                try {
//                    value =
//                        getResourceReplacement(resources, thisObject() as Resources, method, mArgs)
//                } catch (e: Resources.NotFoundException) {
//                    continue
//                }
//                if (value != null) {
//                    if ("getDimensionPixelOffset" == method || "getDimensionPixelSize" == method) {
//                        if (value is Float) value = value.toInt()
//                    }
//                    setResult(value)
//                    break
//                }
//            }
//        }
//    };
//
//    @Throws(Resources.NotFoundException::class)
//    private fun getResourceReplacement(
//        resources: Resources?,
//        res: Resources,
//        method: String?,
//        args: Array<Any>
//    ): Any? {
//        if (resources == null) return null
//        var pkgName: String? = null
//        var resType: String? = null
//        var resName: String? = null
//        try {
//            pkgName = res.getResourcePackageName(args[0] as Int)
//            resType = res.getResourceTypeName(args[0] as Int)
//            resName = res.getResourceEntryName(args[0] as Int)
//        } catch (ignore: Throwable) {
//        }
//        if (pkgName == null || resType == null || resName == null) return null
//
//        val resFullName = "$pkgName:$resType/$resName"
//        val resAnyPkgName = "*:$resType/$resName"
//
//        val value: Any
//        val modResId: Int
//        var replacement: Pair<ReplacementType, Any>? = null
//        if (replacements.containsKey(resFullName)) {
//            replacement = replacements[resFullName]
//        } else if (replacements.containsKey(resAnyPkgName)) {
//            replacement = replacements[resAnyPkgName]
//        }
//        if (replacement != null) {
//            when (replacement.first) {
//                ReplacementType.OBJECT -> {
//                    return replacement.second
//                }
//
//                ReplacementType.DENSITY -> {
//                    return replacement.second as Float * res.displayMetrics.density
//                }
//
//                ReplacementType.ID -> {
//                    modResId = replacement.second as Int
//                    if (modResId == 0) return null
//                    try {
//                        resources.getResourceName(modResId)
//                    } catch (n: Resources.NotFoundException) {
//                        throw n
//                    }
//                    if (method == null) return null
//                    resMap[modResId] = true
//                    value = if ("getDrawable" == method) CoreTool.callMethod(
//                        resources, method, modResId,
//                        args[1]
//                    )
//                    else if ("getDrawableForDensity" == method || "getFraction" == method) CoreTool.callMethod(
//                        resources, method, modResId,
//                        args[1],
//                        args[2]
//                    )
//                    else CoreTool.callMethod(resources, method, modResId)
//                    resMap.remove(modResId)
//                    return value
//                }
//            }
//        }
//        return null
//    }
//
//    private fun getTypedArrayReplacement(resources: Resources, id: Int): Any? {
//        if (id != 0) {
//            var pkgName: String? = null
//            var resType: String? = null
//            var resName: String? = null
//            try {
//                pkgName = resources.getResourcePackageName(id)
//                resType = resources.getResourceTypeName(id)
//                resName = resources.getResourceEntryName(id)
//            } catch (ignore: Throwable) {
//            }
//            if (pkgName == null || resType == null || resName == null) return null
//
//            try {
//                val resFullName = "$pkgName:$resType/$resName"
//                val resAnyPkgName = "*:$resType/$resName"
//
//                var replacement: Pair<ReplacementType, Any>? = null
//                if (replacements.containsKey(resFullName)) {
//                    replacement = replacements[resFullName]
//                } else if (replacements.containsKey(resAnyPkgName)) {
//                    replacement = replacements[resAnyPkgName]
//                }
//                if (replacement != null && (Objects.requireNonNull(replacement.first) == ReplacementType.OBJECT)) {
//                    return replacement.second
//                }
//            } catch (e: Throwable) {
//                starLog.logE(TAG, e)
//            }
//        }
//        return null
//    } // 下面注入方法存在风险，可能导致资源混乱，抛弃。
//    /*public static Context getModuleContext(Context context)
//            throws PackageManager.NameNotFoundException {
//        return getModuleContext(context, null);
//    }
//
//    public static Context getModuleContext(Context context, Configuration config)
//            throws PackageManager.NameNotFoundException {
//        Context mModuleContext;
//        mModuleContext = context.createPackageContext(mProjectPkg, Context.CONTEXT_IGNORE_SECURITY).createDeviceProtectedStorageContext();
//        return config == null ? mModuleContext : mModuleContext.createConfigurationContext(config);
//    }
//
//    public static Resources getModuleRes(Context context)
//            throws PackageManager.NameNotFoundException {
//        Configuration config = context.getResources().getConfiguration();
//        Context moduleContext = getModuleContext(context);
//        return (config == null ? moduleContext.getResources() : moduleContext.createConfigurationContext(config).getResources());
//    }*/
//
//}