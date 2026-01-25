//package com.yunzia.hyperstar.hook.util
//
//import android.app.MiuiThemeHelper
//import android.content.Context
//import android.content.res.Resources
//import android.util.SparseIntArray
//import io.github.libxposed.api.XposedInterface.BeforeHookCallback
//import io.github.libxposed.api.XposedInterface.AfterHookCallback
//import java.util.concurrent.ConcurrentHashMap
//import name.monwf.customiuizer.mods.utils.BaseHookClassHelper.MethodHook
//import kotlin.collections.get
//
//class ResourceHooks {
//    // 使用Kotlin的sealed class代替Java的enum
//    sealed class ReplacementType {
//        object ID : ReplacementType()
//        object OBJECT : ReplacementType()
//    }
//
//    // 使用data class简化Java的内部类
//    data class ResourceValue(
//        val type: ReplacementType,
//        val value: Any
//    )
//
//    data class ThemeValue(
//        val value: Any,
//        val nightValue: Any = value,
//        var resId: Int = -1
//    )
//
//    private val hookedTypes = mutableSetOf<String>()
//    private var valueUpdated = false
//    private var themeResourcesHooked = false
//
//    private val fakes = SparseIntArray()
//    private val themeValueReplacements = ConcurrentHashMap<String, ThemeValue>()
//    private val resourceIdReplacements = ConcurrentHashMap<Integer, ResourceValue>()
//
//    companion object {
//        fun getFakeResId(resourceName: String): Int =
//            0x7e00f000 or (resourceName.hashCode() and 0x00ffffff)
//    }
//
//    // 使用lambda表达式简化MethodHook
//    private val mReplaceHook = object : MethodHook() {
//        override fun before(param: BeforeHookCallback) {
//            val context = ModuleHelper.findContext() ?: return
//            val method = param.member.name
//
//            getFakeResource(context, method, param.args)?.let {
//                param.returnAndSkip(it)
//                return
//            }
//
//            if (method == "getLayout") return
//
//            getResourceReplacement(context, method, param.args)?.let {
//                param.returnAndSkip(it)
//            }
//        }
//    }
//
//    private fun initThemeHook() {
//        ModuleHelper.findAndHookMethod(
//            miui.content.res.ThemeResources::class.java,
//            "mergeThemeValues",
//            String::class.java,
//            miui.content.res.ThemeValues::class.java,
//            object : MethodHook() {
//                override fun after(param: AfterHookCallback) {
//                    val mPackageName = XposedHelpers.getObjectField(param.thisObject, "mPackageName") as? String ?: return
//                    if (mPackageName == "miui") return
//
//                    if ((mPackageName == ModuleHelper.currentPackageName
//                        || mPackageName == "miui.systemui.plugin"
//                        ) && (
//                        param.args[0] == ModuleHelper.currentPackageName
//                        || param.args[0] == "miui.systemui.plugin"
//                        )
//                    ) {
//                        val themeIntValues = mutableMapOf<Int, Int>()
//                        val themeIntegerArrays = mutableMapOf<Int, IntArray>()
//                        val themeStringArrays = mutableMapOf<Int, Array<String>>()
//
//                        val mThemeResources = param.thisObject
//                        val mResources = XposedHelpers.getObjectField(mThemeResources, "mResources") as Resources
//                        val nightMode = XposedHelpers.getBooleanField(mThemeResources, "mNightMode")
//                        val mThemeValues = param.args[1]
//
//                        @Suppress("UNCHECKED_CAST")
//                        val mIntegers = XposedHelpers.getObjectField(mThemeValues, "mIntegers") as MutableMap<Int, Int>
//                        val mIntegerArrays = XposedHelpers.getObjectField(mThemeValues, "mIntegerArrays") as MutableMap<Int, IntArray>
//                        val mStringArrays = XposedHelpers.getObjectField(mThemeValues, "mStringArrays") as MutableMap<Int, Array<String>>
//
//                        themeValueReplacements.forEach { (resFullName, tv) ->
//                            val resMetas = resFullName.split(":|/")
//                            val themeValueType = resMetas[1]
//
//                            if (tv.resId == -1) {
//                                val resourceType = if (themeValueType in listOf("string-array", "integer-array")) "array" else themeValueType
//                                if (resMetas[0] == mPackageName || resMetas[0] == "android") {
//                                    tv.resId = mResources.getIdentifier(resMetas[2], resourceType, resMetas[0])
//                                }
//                            }
//
//                            if (tv.resId > 0) {
//                                val value = if (nightMode) tv.nightValue else tv.value
//                                when (themeValueType) {
//                                    "string-array" -> themeStringArrays[tv.resId] = value as Array<String>
//                                    "integer-array" -> themeIntegerArrays[tv.resId] = value as IntArray
//                                    else -> themeIntValues[tv.resId] = value as Int
//                                }
//                            }
//                        }
//
//                        mIntegers.putAll(themeIntValues)
//                        mIntegerArrays.putAll(themeIntegerArrays)
//                        mStringArrays.putAll(themeStringArrays)
//                    }
//                }
//            }
//        )
//    }
//
//    private fun initResourceIdHook(pkg: String, type: String, name: String, resourceType: ReplacementType, replaceValue: Any) {
//        val context = ModuleHelper.findContext()
//        val rv = ResourceValue(resourceType, replaceValue)
//
//        context?.let {
//            val resId = it.resources.getIdentifier(name, type, pkg)
//            if (resId > 0) {
//                resourceIdReplacements[resId] = rv
//            } else {
//                XposedHelpers.log("Resource not found: $pkg:$type/$name")
//            }
//        } ?: XposedHelpers.log("Context not found: $pkg:$type/$name")
//    }
//
//    private fun applyHooks(type: String) {
//        if (type in hookedTypes) return
//        hookedTypes.add(type)
//
//        when (type) {
//            "layout" -> ModuleHelper.findAndHookMethod(Resources::class.java, "getLayout", Int::class.java, mReplaceHook)
//            "string" -> {
//                ModuleHelper.findAndHookMethod(Resources::class.java, "getText", Int::class.java, mReplaceHook)
//                ModuleHelper.findAndHookMethod(Resources::class.java, "getString", Int::class.java, mReplaceHook)
//            }
//            "drawable" -> ModuleHelper.findAndHookMethod(
//                Resources::class.java,
//                "getDrawableForDensity",
//                Int::class.java,
//                Int::class.java,
//                Resources.Theme::class.java,
//                mReplaceHook
//            )
//        }
//    }
//
//    /**
//     * 添加假资源，可被模块资源替换
//     */
//    fun addFakeResource(resName: String, resId: Int, type: String): Int {
//        return try {
//            val fakeResId = getFakeResId(resName)
//            fakes.put(fakeResId, resId)
//            applyHooks(type)
//            fakeResId
//        } catch (t: Throwable) {
//            XposedHelpers.log(t)
//            0
//        }
//    }
//
//    /**
//     * 使用模块资源替换包资源
//     */
//    fun setResReplacement(pkg: String, type: String, name: String, replacementResId: Int) {
//        try {
//            initResourceIdHook(pkg, type, name, ReplacementType.ID, replacementResId)
//            applyHooks(type)
//        } catch (t: Throwable) {
//            XposedHelpers.log(t)
//        }
//    }
//
//    /**
//     * 使用替换值替换包资源
//     */
//    fun setObjectReplacement(pkg: String, type: String, name: String, replacementResValue: Any) {
//        try {
//            initResourceIdHook(pkg, type, name, ReplacementType.OBJECT, replacementResValue)
//            applyHooks(type)
//        } catch (t: Throwable) {
//            XposedHelpers.log(t)
//        }
//    }
//
//    fun setThemeValueReplacement(pkg: String, type: String, name: String, resValue: Any) {
//        setThemeValueReplacement(pkg, type, name, resValue, resValue)
//    }
//
//    fun setThemeValueReplacement(pkg: String, type: String, name: String, resValue: Any, nightResValue: Any) {
//        val finalResValue = when (type) {
//            "bool" -> if (resValue as Boolean) 1 else 0
//            "dimen" -> MiuiThemeHelper.parseDimension("${resValue}dp")
//            else -> resValue
//        }
//
//        val finalNightResValue = when (type) {
//            "bool" -> if (nightResValue as Boolean) 1 else 0
//            "dimen" -> MiuiThemeHelper.parseDimension("${nightResValue}dp")
//            else -> nightResValue
//        }
//
//        themeValueReplacements["$pkg:$type/$name"] = ThemeValue(finalResValue, finalNightResValue)
//        valueUpdated = true
//
//        if (!themeResourcesHooked) {
//            themeResourcesHooked = true
//            initThemeHook()
//        }
//    }
//
//    private fun getModuleResValue(modRes: Resources, method: String, modResId: Int, args: Array<Any>): Any? {
//        return when (method) {
//            "getDrawableForDensity" -> XposedHelpers.callMethod(modRes, method, modResId, args[1], args[2])
//            else -> XposedHelpers.callMethod(modRes, method, modResId)
//        }
//    }
//
//    private fun getFakeResource(context: Context, method: String, args: Array<Any>): Any? {
//        return try {
//            val modResId = fakes.get(args[0] as Int)
//            if (modResId == 0) return null
//
//            val modRes = ModuleHelper.getModuleRes(context)
//            getModuleResValue(modRes, method, modResId, args)
//        } catch (t: Throwable) {
//            XposedHelpers.log(t)
//            null
//        }
//    }
//
//    private fun getResourceReplacement(context: Context, method: String, args: Array<Any>): Any? {
//        val resId = args[0] as Int
//        resourceIdReplacements[resId]?.let { replacement ->
//            return when (replacement.type) {
//                ReplacementType.OBJECT -> replacement.value
//                ReplacementType.ID -> try {
//                    val modRes = ModuleHelper.getModuleRes(context)
//                    getModuleResValue(modRes, method, replacement.value as Int, args)
//                } catch (t: Throwable) {
//                    XposedHelpers.log(t)
//                    null
//                }
//            }
//        }
//        return null
//    }
//}