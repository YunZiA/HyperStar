package com.yunzia.hyperstar.hook.core.helper

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.TypedArray
import android.content.res.loader.ResourcesLoader
import android.content.res.loader.ResourcesProvider
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ArrayRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.yunzia.hyperstar.hook.base.BaseHookHelper.getResId
import com.yunzia.hyperstar.hook.core.StarLog.logD
import com.yunzia.hyperstar.hook.core.StarLog.logE
import com.yunzia.hyperstar.hook.core.XposedCore
import java.io.File
import java.util.concurrent.ConcurrentHashMap


object ResourcesHelper {

    @JvmStatic
    fun addModuleAssetPath(context: Context) {
        addModuleAssetPath(context.resources)
    }
    @JvmStatic
    fun loadResAboveApi30(context: Context) {
        loadResAboveApi30(context.resources)
    }

    @JvmStatic
    fun loadResAboveApi30(resources: Resources) {
        ParcelFileDescriptor.open(
            File(XposedCore.modulePath),
            ParcelFileDescriptor.MODE_READ_ONLY
        ).use { pfd ->
            val provider = ResourcesProvider.loadFromApk(pfd)
            val loader = ResourcesLoader()
            loader.addProvider(provider)
            resources.addLoaders(loader)
        }
    }

    @JvmStatic
    private val mAddAddAssertPath by lazy {
        AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java).also { it.isAccessible = true }
    }

    @JvmStatic
    fun addModuleAssetPath(resources: Resources) {
        try {
            mAddAddAssertPath.invoke(resources.assets, XposedCore.modulePath)
            logD("idCache ${idCache.size}")
            logD("Module Resources is added")
        } catch (e: Exception){
            logE("Module Resources is not added :$e")
        }

    }
    @JvmStatic
    private val idCache = ConcurrentHashMap<String, Int>()


    @JvmStatic
    fun getCacheSize(): Int {
        return idCache.size
    }

    @JvmStatic
    private inline fun getCachedIdBy(
        key: String,
        getIdBlock: () -> Int
    ): Int {

        val cached = idCache[key]
        if (cached != null) return cached
        val id = getIdBlock()
        if (id != 0) {
            idCache[key] = id
        }
        return id
    }

    @JvmStatic
    fun colorReplaceById(
        name: String,
        packageName: String,
        @ColorRes newId: Int
    ) {
        val key = "$packageName color:$name"
        Resources::class.java.beforeHookAllMethods("getColor") { args, result ->
            thisObject ?: return@beforeHookAllMethods
            val currentId = args[0] as Int
            val targetId = getCachedIdBy(key) { thisObject.getResId(name, "color", packageName) }
            if (currentId == targetId) {
                args[0] = newId
            }

        }
    }

    @JvmStatic
    fun colorReplaceByIdName(
        name: String,
        packageName: String,
        newName: String
    ) {
        val key = "$packageName color:$name"
        val newKey = "$packageName color:$newName"
        Resources::class.java.beforeHookAllMethods("getColor") { args, result ->
            thisObject ?: return@beforeHookAllMethods
            val currentId = args[0] as Int
            val targetId = getCachedIdBy(key) { thisObject.getResId(name, "color", packageName) }
            if (currentId == targetId) {
                val newId = getCachedIdBy(newKey) { thisObject.getResId(newName, "color", packageName) }
                args[0] = newId
            }

        }
    }
    @JvmStatic
    fun colorReplaceByIdName(
        name: String,
        packageName: String,
        newName: String,
        newPackageName: String,
    ) {
        val key = "$packageName color:$name"
        val newKey = "$newPackageName color:$newName"
        Resources::class.java.beforeHookAllMethods("getColor") { args, result ->
            thisObject ?: return@beforeHookAllMethods
            val currentId = args[0] as Int
            val targetId = getCachedIdBy(key) { thisObject.getResId(name, "color", packageName) }
            if (currentId == targetId) {
                val newId = getCachedIdBy(newKey) { thisObject.getResId(newName, "color", newPackageName) }
                args[0] = newId
            }

        }
    }

    @JvmStatic
    fun colorReplaceByValue(
        name: String,
        packageName: String,
        @ColorInt newColor: Int
    ) {
        val key = "$packageName color:$name"
        Resources::class.java.beforeHookAllMethods("getColor") { args, result ->
            thisObject ?: return@beforeHookAllMethods
            val currentId = args[0] as Int

            val targetId = getCachedIdBy(key) { thisObject.getResId(name, "color", packageName) }
            if (currentId == targetId) {
                result.replace(newColor)
            }
        }
    }

    @JvmStatic
    fun colorReplaceByValue(
        name: String,
        packageName: String,
        newColor: String
    ) {
        val key = "$packageName color:$name"
        Resources::class.java.beforeHookAllMethods("getColor") { args, result ->
            thisObject ?: return@beforeHookAllMethods
            val currentId = args[0] as Int
            val targetId = getCachedIdBy(key) { thisObject.getResId(name, "color", packageName) }
            if (currentId == targetId) {
                result.replace(Color.parseColor(newColor))
            }
        }
    }

    @JvmStatic
    fun dimenReplaceById(
        packageName: String,
        name: String,
        @DimenRes newId: Int
    ) {
        val key = "$packageName dimen:$name"
        // hook Resources 的 dimension 获取方法
        fun hookDimensionMethod(methodName: String) {
            Resources::class.java.beforeHookMethod(methodName, Int::class.java) { args, result ->
                thisObject ?: return@beforeHookMethod
                val currentId = args[0] as Int
                val targetId = getCachedIdBy(key) { thisObject.getResId(name, "dimen", packageName) }
                if (currentId == targetId) {
                    args[0] = newId
                }
            }
        }
        // hook TypedArray 的 dimension 获取方法
        fun hookTADimensionMethod(methodName: String) {
            TypedArray::class.java.beforeHookAllMethods(methodName) { args, result ->
                (thisObject as TypedArray).apply {
                    val index = args[0] as Int
                    val currentId = getResourceId(index, 0)
                    if (currentId == 0) return@beforeHookAllMethods
                    val mResources = thisObject.getObjectFieldAs<Resources>("mResources")
                    val targetId = getCachedIdBy(key) { thisObject.getResId(name, "dimen", packageName) }
                    if (currentId == targetId) {
                        when (this@beforeHookAllMethods.executable.name) {
                            "getDimension" -> result.replace(mResources.getDimension(newId))
                            "getDimensionPixelSize" -> result.replace(mResources.getDimensionPixelSize(newId))
                            "getDimensionPixelOffset" -> result.replace(mResources.getDimensionPixelOffset(newId))
                        }
                    }

                }
            }
        }
        // 注册所有相关 hook
        listOf(
            "getDimensionPixelOffset" ,
            "getDimensionPixelSize",
            "getDimension"
        ).forEach { methodName ->
            hookDimensionMethod(methodName)
            hookTADimensionMethod(methodName)
        }
    }

    @JvmStatic
    fun dimenReplaceByValue(
        name: String,
        packageName: String,
        replace: Resources.() -> Float?
    ) {
        val key = "$packageName dimen:$name"
        // hook Resources 的 dimension 获取方法
        fun hookDimensionMethod(methodName: String, resultSetter: (Float) -> Any) {
            Resources::class.java.beforeHookMethod(methodName, Int::class.java) { args, result ->
                val currentId = args[0] as Int
                val resources = thisObject as Resources
                val targetId = getCachedIdBy(key) { resources.getResId(name, "dimen", packageName) }
                if (currentId == targetId) {
                    resources.replace()?.let { value ->
                        result.replace(resultSetter(value))
                    }
                }
            }
        }

        // hook TypedArray 的 dimension 获取方法
        fun hookTADimensionMethod(methodName: String, resultSetter: (Float) -> Any) {
            TypedArray::class.java.beforeHookAllMethods(methodName) {  args, result ->
                (thisObject as TypedArray).apply {
                    val index = args[0] as Int
                    val currentId = getResourceId(index, 0)
                    if (currentId == 0) return@beforeHookAllMethods
                    val resources = thisObject.getObjectFieldAs<Resources>("mResources")
                    val targetId = getCachedIdBy(key) { resources.getResId(name, "dimen", packageName) }
                    if (currentId == targetId) {
                        resources.replace()?.let { value ->
                            logD(methodName, "replace $name -> $value")
                            result.replace(resultSetter(value))
                        }
                    }

                }
            }
        }

        // 注册所有相关 hook
        listOf(
            "getDimensionPixelOffset" to { v: Float -> v.toInt() },
            "getDimensionPixelSize" to { v: Float -> v.toInt() },
            "getDimension" to { v: Float -> v }
        ).forEach { (methodName, setter) ->
            hookDimensionMethod(methodName, setter)
            hookTADimensionMethod(methodName, setter)
        }
    }

    @JvmStatic
    fun drawableReplaceById(
        packageName: String,
        name: String,
        @DrawableRes newId: Int
    ){
        val key = "$packageName drawable: $name"
        Resources::class.java.beforeHookAllMethods("getDrawableForDensity") { args, result ->
            thisObject ?: return@beforeHookAllMethods
            val currentId = args[0] as Int
            val targetId = getCachedIdBy(key) { thisObject.getResId(name, "drawable", packageName) }
            if (currentId == targetId && targetId != 0) {
                args[0] = newId
            }
        }
    }

    @JvmStatic
    fun drawableReplaceByValue(
        packageName: String,
        name: String,
        newDrawable: Drawable.() -> Unit
    ){
        val key = "$packageName drawable: $name"
        Resources::class.java.beforeHookMethod("getDrawableForDensity") { args, result ->
            thisObject ?: return@beforeHookMethod
            val currentId = args[0] as Int
            val targetId = getCachedIdBy(key) { thisObject.getResId(name, "drawable", packageName) }
            if (currentId == targetId && targetId != 0) {
                (result.value as Drawable).apply{
                    result.replace(newDrawable())
                }
            }
        }
    }

    @JvmStatic
    fun integerReplaceById(
        packageName: String,
        name: String,
        @IntegerRes newId: Int
    ) {
        val key = "$packageName integer: $name"
        Resources::class.java.beforeHookAllMethods("getInteger") { args, result ->
            thisObject ?: return@beforeHookAllMethods
            val currentId = args[0] as Int
            val targetId = getCachedIdBy(key) { thisObject.getResId(name, "integer", packageName) }
            if (currentId == targetId && targetId != 0) {
                args[0] = newId
            }
        }
    }

    @JvmStatic
    fun layoutReplaceById(
        packageName: String,
        name: String,
        @LayoutRes newId: Int
    ) {
        val key = "$packageName layout: $name"
        Resources::class.java.beforeHookMethod("getLayout", Int::class.java) { args, result ->
            thisObject ?: return@beforeHookMethod
            val currentId = args[0] as Int
            val targetId = getCachedIdBy(key) { thisObject.getResId(name, "layout", packageName) }
            if (currentId == targetId && targetId != 0) {
                args[0] = newId
            }
        }
    }

    @JvmStatic
    fun integerReplaceByValue(
        name: String,
        packageName: String,
        value: Int
    ) {
        val key = "$packageName integer: $name"
        Resources::class.java.beforeHookAllMethods("getInteger") { args, result ->
            thisObject ?: return@beforeHookAllMethods
            val currentId = args[0] as Int
            val targetId = getCachedIdBy(key) { thisObject.getResId(name, "integer", packageName) }
            if (currentId == targetId && targetId != 0) {
                result.replace(value)
            }
        }
    }

    @JvmStatic
    fun hookLayout(
        name: String,
        packageName: String,
        block: View.() -> Unit
    ){
        val key = "$packageName layout: $name"
        LayoutInflater::class.java.beforeHookMethod(
            "inflate", Int::class.java, ViewGroup::class.java, Boolean::class.java
        ) { args, result ->
            (thisObject as LayoutInflater).apply {
                val currentId = args[0] as Int
                val resources = this.context.resources
                val targetId = getCachedIdBy(key) { resources.getResId(name, "layout", packageName) }
                if (currentId == targetId && targetId != 0) {
                    val inflatedView: View = result.value as? View ?: return@beforeHookMethod
                    inflatedView.block()
                }
            }

        }
    }

    @JvmStatic
    fun stringReplaceById(
        packageName: String,
        name: String,
        @StringRes newId: Int
    ) {
        val key = "$packageName string: $name"
        Resources::class.java.beforeHookMethod("getText", Int::class.java) { args, result ->
            thisObject ?: return@beforeHookMethod
            val currentId = args[0] as Int
            val targetId = getCachedIdBy(key) { thisObject.getResId(name, "string", packageName) }
            if (currentId == targetId && targetId != 0) {
                args[0] = newId
            }
        }
        listOf( "getText", "getString").forEach { methodName ->
            TypedArray::class.java.beforeHookAllMethods(methodName) { args, result ->
                (thisObject as TypedArray).apply {
                    val index = args[0] as Int
                    val currentId = getResourceId(index, 0)
                    if (currentId == 0) return@beforeHookAllMethods
                    val mResources = thisObject.getObjectFieldAs<Resources>("mResources")
                    val targetId = getCachedIdBy(key) { mResources.getResId(name, "string", packageName) }
                    if (currentId == targetId) {
                        when(this@beforeHookAllMethods.executable.name){
                            "getText" -> result.replace(mResources.getText(newId))
                            "getString" -> result.replace(mResources.getString(newId))
                        }
                    }

                }
            }
        }
    }

    @JvmStatic
    fun stringReplaceByValue(
        name: String,
        packageName: String,
        value: CharSequence
    ) {
        val key = "$packageName string: $name"
        Resources::class.java.beforeHookMethod("getText", Int::class.java) { args, result ->
            thisObject ?: return@beforeHookMethod
            val currentId = args[0] as Int
            val targetId = getCachedIdBy(key) { thisObject.getResId(name, "string", packageName) }
            if (currentId == targetId && targetId != 0) {
                logD("stringReplaceByValue ${targetId} $currentId")
                result.replace(value)
            }
        }
        listOf("getText", "getString").forEach { methodName ->
            TypedArray::class.java.beforeHookAllMethods(methodName) { args, result ->
                (thisObject as TypedArray).apply {
                    val index = args[0] as Int
                    val currentId = getResourceId(index, 0)
                    if (currentId == 0) return@beforeHookAllMethods
                    val mResources = thisObject.getObjectFieldAs<Resources>("mResources")
                    val targetId = getCachedIdBy(key) { mResources.getResId(name, "string", packageName) }
                    if (currentId == targetId) {
                        result.replace(value)
                    }

                }
            }
        }
    }

    @JvmStatic
    fun intArrayReplaceById(
        name: String,
        packageName: String,
        @ArrayRes newId: Int
    ){
        val key = "$packageName intArray: $name"
        Resources::class.java.beforeHookMethod("getIntArray", Int::class.java) { args, result ->
            thisObject ?: return@beforeHookMethod
            val currentId = args[0] as Int
            val targetId = getCachedIdBy(key) { thisObject.getResId(name, "array", packageName) }
            if (currentId == targetId && targetId != 0) {
                args[0] = newId
            }
        }
    }

    @JvmStatic
    fun intArrayReplaceByIdName(
        name: String,
        packageName: String,
        newName: String
    ) {
        val key = "$packageName intArray:$name"
        Resources::class.java.beforeHookMethod("getIntArray", Int::class.java) { args, result ->
            thisObject ?: return@beforeHookMethod
            val currentId = args[0] as Int
            val targetId = getCachedIdBy(key) { thisObject.getResId(name, "array", packageName) }
            if (currentId == targetId) {
                val newId = getCachedIdBy(key) { thisObject.getResId(newName, "array", packageName) }
                args[0] = newId
            }

        }
    }

    @JvmStatic
    fun intArrayReplaceByIdName(
        name: String,
        packageName: String,
        newName: String,
        newPackageName: String,
    ) {
        val key = "$packageName intArray:$name"
        val newKey = "$newPackageName intArray:$newName"
        Resources::class.java.beforeHookMethod("getIntArray", Int::class.java) { args, result ->
            thisObject ?: return@beforeHookMethod
            val currentId = args[0] as Int
            val targetId = getCachedIdBy(key) { thisObject.getResId(name, "array", packageName) }
            if (currentId == targetId) {
                val newId = getCachedIdBy(newKey) { thisObject.getResId(newName, "array", newPackageName) }
                args[0] = newId
            }

        }
    }

    @JvmStatic
    fun intArrayReplaceByValue(
        name: String,
        packageName: String,
        value: Array<Int>
    ){
        val key = "$packageName intArray: $name"
        Resources::class.java.beforeHookMethod("getIntArray", Int::class.java) { args, result ->
            thisObject ?: return@beforeHookMethod
            val currentId = args[0] as Int
            val targetId = getCachedIdBy(key) { thisObject.getResId(name, "array", packageName) }
            if (currentId == targetId && targetId != 0) {
                result.replace(value)
            }
        }
    }
    @JvmStatic
    fun intArrayReplaceByValue(
        name: String,
        packageName: String,
        value: Array<Int>.()-> Unit
    ){
        val key = "$packageName intArray: $name"
        Resources::class.java.beforeHookMethod("getIntArray", Int::class.java) { args, result ->
            thisObject ?: return@beforeHookMethod
            val currentId = args[0] as Int
            val targetId = getCachedIdBy(key) { thisObject.getResId(name, "array", packageName) }
            if (currentId == targetId && targetId != 0) {
                (result.value as Array<Int>).apply { result.replace(value()) }
            }
        }
    }


    @JvmStatic
    fun textArrayReplaceById(
        name: String,
        packageName: String,
        @ArrayRes newId: Int
    ){
        val key = "$packageName textArray: $name"
        Resources::class.java.beforeHookMethod("getTextArray", Int::class.java) { args, result ->
            thisObject ?: return@beforeHookMethod
            val currentId = args[0] as Int
            val targetId = getCachedIdBy(key) { thisObject.getResId(name, "array", packageName) }
            if (currentId == targetId && targetId != 0) {
                args[0] = newId
            }
        }
    }

    @JvmStatic
    fun textArrayReplaceByValue(
        name: String,
        packageName: String,
        value: Array<CharSequence>
    ){
        val key = "$packageName textArray: $name"
        Resources::class.java.beforeHookMethod("getTextArray", Int::class.java) { args, result ->
            thisObject ?: return@beforeHookMethod
            val currentId = args[0] as Int
            val targetId =
                getCachedIdBy(key) { thisObject.getResId(name, "array", packageName) }
            if (currentId == targetId && targetId != 0) {
                result.replace(value)
            }
        }
    }

    @JvmStatic
    fun stringArrayReplaceById(
        name: String,
        packageName: String,
        @ArrayRes newId: Int
    ){
        val key = "$packageName stringArray: $name"
        Resources::class.java.beforeHookMethod("getStringArray", Int::class.java) { args, result ->
            thisObject ?: return@beforeHookMethod
            val currentId = args[0] as Int
            val targetId = getCachedIdBy(key) { thisObject.getResId(name, "array", packageName) }
            if (currentId == targetId && targetId != 0) {
                args[0] = newId
            }
        }

    }

    @JvmStatic
    fun stringArrayReplaceByValue(
        name: String,
        packageName: String,
        value: Array<String>
    ){
        val key = "$packageName stringArray: $name"
        Resources::class.java.beforeHookMethod("getStringArray", Int::class.java) { args, result ->
            thisObject ?: return@beforeHookMethod
            val currentId = args[0] as Int
            val targetId = getCachedIdBy(key) { thisObject.getResId(name, "array", packageName) }
            if (currentId == targetId && targetId != 0) {
                result.replace(value)
            }
        }
    }

}