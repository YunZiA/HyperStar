package com.yunzia.hyperstar.hook.core.helper

import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
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
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.hook.base.BaseHookHelper.getId
import com.yunzia.hyperstar.hook.core.Log.logD
import com.yunzia.hyperstar.hook.core.Log.logE
import com.yunzia.hyperstar.hook.core.finder.loadClassBy
import com.yunzia.hyperstar.hook.core.provider.PluginClassLoaderProvider
import com.yunzia.hyperstar.hook.util.base.ResourcesImpl
import java.util.concurrent.ConcurrentHashMap

object ResourcesHelper {
    private val idCache = ConcurrentHashMap<String, Int>()

    private fun getCachedIdBy(
        key: String,
        getIdBlock: () -> Int
    ): Int {
        idCache[key]?.let { cached ->
            return cached
        }
        val id = getIdBlock()
        if (id != 0) idCache.putIfAbsent(key, id)
        return id
    }

    fun colorReplaceById(
        name: String,
        packageName: String,
        @ColorRes newId: Int
    ) {
        val key = "$packageName color:$name"
        Resources::class.java.beforeHookAllMethods("getColor") {
            val currentId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val targetId = getCachedIdBy(key) { mResourcesImpl.getId(name, "color", packageName) }
            if (currentId == targetId) {
                it.args[0] = newId
            }

        }
    }
    
    fun colorReplaceByIdName(
        name: String,
        packageName: String,
        newName: String
    ) {
        val key = "$packageName color:$name"
        val newKey = "$packageName color:$newName"
        Resources::class.java.beforeHookAllMethods("getColor") {
            val currentId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val targetId = getCachedIdBy(key) { mResourcesImpl.getId(name, "color", packageName) }
            if (currentId == targetId) {
                val newId = getCachedIdBy(newKey) { mResourcesImpl.getId(newName, "color", packageName) }
                it.args[0] = newId
            }

        }
    }
    fun colorReplaceByIdName(
        name: String,
        packageName: String,
        newName: String,
        newPackageName: String,
    ) {
        val key = "$packageName color:$name"
        val newKey = "$newPackageName color:$newName"
        Resources::class.java.beforeHookAllMethods("getColor") {
            val currentId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val targetId = getCachedIdBy(key) { mResourcesImpl.getId(name, "color", packageName) }
            if (currentId == targetId) {
                val newId = getCachedIdBy(newKey) { mResourcesImpl.getId(newName, "color", newPackageName) }
                it.args[0] = newId
            }

        }
    }

    fun colorReplaceByValue(
        name: String,
        packageName: String,
        @ColorInt newColor: Int
    ) {
        val key = "$packageName color:$name"
        Resources::class.java.afterHookAllMethods("getColor") {
            val currentId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val targetId = getCachedIdBy(key) { mResourcesImpl.getId(name, "color", packageName) }
            if (currentId == targetId) {
                it.result = newColor
            }
        }
    }

    fun colorReplaceByValue(
        name: String,
        packageName: String,
        newColor: String
    ) {
        val key = "$packageName color:$name"
        Resources::class.java.afterHookAllMethods("getColor") {
            val currentId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val targetId = getCachedIdBy(key) { mResourcesImpl.getId(name, "color", packageName) }
            if (currentId == targetId) {
                it.result = Color.parseColor(newColor)
            }
        }
    }

    fun dimenReplaceById(
        packageName: String,
        name: String,
        @DimenRes newId: Int
    ) {
        val key = "$packageName dimen:$name"
        // hook Resources 的 dimension 获取方法
        fun hookDimensionMethod(methodName: String) {
            Resources::class.java.beforeHookMethod(methodName, Int::class.java) {
                val currentId = it.args[0] as Int
                val mResourcesImpl = ResourcesImpl(this)
                val targetId = getCachedIdBy(key) { mResourcesImpl.getId(name, "dimen", packageName) }
                if (currentId == targetId) {
                    it.args[0] = newId
                }
            }
        }
        // hook TypedArray 的 dimension 获取方法
        fun hookTADimensionMethod(methodName: String) {
            TypedArray::class.java.afterHookAllMethods(methodName) {
                this as TypedArray
                val index = it.args[0] as Int
                val currentId = this.getResourceId(index, 0)
                if (currentId == 0) return@afterHookAllMethods
                val mResources = this.getObjectFieldAs<Resources>("mResources")
                val targetId = getCachedIdBy(key) { mResources.getId(name, "dimen", packageName) }
                if (currentId == targetId) {
                    when (it.member.name) {
                        "getDimension" -> it.result = mResources.getDimension(newId)
                        "getDimensionPixelSize" -> it.result = mResources.getDimensionPixelSize(newId)
                        "getDimensionPixelOffset" -> it.result = mResources.getDimensionPixelOffset(newId)
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

    fun dimenReplaceByValue(
        name: String,
        packageName: String,
        replace: ResourcesImpl.() -> Float?
    ) {
        val key = "$packageName dimen:$name"
        // hook Resources 的 dimension 获取方法
        fun hookDimensionMethod(methodName: String, resultSetter: (Float) -> Any) {
            Resources::class.java.afterHookMethod(methodName, Int::class.java) {
                val currentId = it.args[0] as Int
                val mResourcesImpl = ResourcesImpl(this)
                val targetId = getCachedIdBy(key) { mResourcesImpl.getId(name, "dimen", packageName) }
                if (currentId == targetId) {
                    mResourcesImpl.replace()?.let { value ->
                        it.result = resultSetter(value)
                    }
                }
            }
        }

        // hook TypedArray 的 dimension 获取方法
        fun hookTADimensionMethod(methodName: String, resultSetter: (Float) -> Any) {
            TypedArray::class.java.afterHookAllMethods(methodName) { this as TypedArray
                val index = it.args[0] as Int
                val currentId = this.getResourceId(index, 0)
                if (currentId == 0) return@afterHookAllMethods
                val mResources = this.getObjectFieldAs<Resources>("mResources")
                val mResourcesImpl = ResourcesImpl(mResources)
                val targetId = getCachedIdBy(key) { mResources.getId(name, "dimen", packageName) }
                if (currentId == targetId) {
                    mResourcesImpl.replace()?.let { value ->
                        logD(methodName, "replace $name -> $value")
                        it.result = resultSetter(value)
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

    fun drawableReplaceById(
        packageName: String,
        name: String,
        @DrawableRes newId: Int
    ){
        val key = "$packageName drawable: $name"
        Resources::class.java.beforeHookAllMethods("getDrawableForDensity") {
            this as Resources
            val currentId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val targetId = getCachedIdBy(key) { mResourcesImpl.getId(name, "drawable", packageName) }
            if (currentId == targetId && targetId != 0) {
                it.args[0] = newId
            }
        }
    }

    fun drawableReplaceByValue(
        packageName: String,
        name: String,
        newDrawable: Drawable.() -> Unit
    ){
        val key = "$packageName drawable: $name"
        Resources::class.java.afterHookAllMethods("getDrawableForDensity") {
            this as Resources
            val currentId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val targetId = getCachedIdBy(key) { mResourcesImpl.getId(name, "drawable", packageName) }
            if (currentId == targetId && targetId != 0) {
                it.result = (it.result as Drawable).apply(newDrawable)
            }
        }
    }

    fun integerReplaceById(
        packageName: String,
        name: String,
        @IntegerRes newId: Int
    ) {
        val key = "$packageName integer: $name"
        Resources::class.java.beforeHookAllMethods("getInteger") {
            this as Resources
            val currentId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)

            val targetId = getCachedIdBy(key) { mResourcesImpl.getId(name, "integer", packageName) }
            if (currentId == targetId && targetId != 0) {
                it.args[0] = newId
            }
        }
    }

    fun layoutReplaceById(
        packageName: String,
        name: String,
        @LayoutRes newId: Int
    ) {
        val key = "$packageName layout: $name"
        Resources::class.java.beforeHookMethod("getLayout", Int::class.java) {
            this as Resources
            val currentId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val targetId = getCachedIdBy(key) { mResourcesImpl.getId(name, "layout", packageName) }
            if (currentId == targetId && targetId != 0) {
                it.args[0] = newId
            }
        }
    }

    fun integerReplaceByValue(
        name: String,
        packageName: String,
        value: Int
    ) {
        val key = "$packageName integer: $name"
        Resources::class.java.afterHookAllMethods("getInteger") {
            this as Resources
            val currentId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val targetId = getCachedIdBy(key) { mResourcesImpl.getId(name, "integer", packageName) }
            if (currentId == targetId && targetId != 0) {
                it.result = value
            }
        }
    }

    fun hookLayout(
        name: String,
        packageName: String,
        block: View.() -> Unit
    ){
        val key = "$packageName layout: $name"
        LayoutInflater::class.java.afterHookMethod(
            "inflate", Int::class.java, ViewGroup::class.java, Boolean::class.java
        ) {
            this as LayoutInflater
            val currentId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this.context)
            val targetId = getCachedIdBy(key) { mResourcesImpl.getId(name, "layout", packageName) }
            if (currentId == targetId && targetId != 0) {
                val inflatedView: View = it.result as? View ?: return@afterHookMethod
                inflatedView.block()
            }

        }
    }

    fun stringReplaceById(
        packageName: String,
        name: String,
        @StringRes newId: Int
    ) {
        val key = "$packageName string: $name"
        Resources::class.java.beforeHookMethod("getText", Int::class.java) {
            this as Resources
            val currentId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val targetId = getCachedIdBy(key) { mResourcesImpl.getId(name, "string", packageName) }
            if (currentId == targetId && targetId != 0) {
                it.args[0] = newId
            }
        }
        listOf( "getText", "getString").forEach { methodName ->
            TypedArray::class.java.afterHookAllMethods(methodName) {
                this as TypedArray
                val index = it.args[0] as Int
                val currentId = this.getResourceId(index, 0)
                if (currentId == 0) return@afterHookAllMethods
                val mResources = this.getObjectFieldAs<Resources>("mResources")
                val targetId = getCachedIdBy(key) { mResources.getId(name, "string", packageName) }
                if (currentId == targetId) {
                     when(it.member.name){
                        "getText" -> it.result = mResources.getText(newId)
                        "getString" -> it.result = mResources.getString(newId)
                    }
                }
            }
        }
    }

    fun stringReplaceByValue(
        name: String,
        packageName: String,
        value: CharSequence
    ) {
        val key = "$packageName string: $name"
        Resources::class.java.afterHookMethod("getText", Int::class.java) {
            this as Resources
            val currentId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val targetId =
                getCachedIdBy(key) { mResourcesImpl.getId(name, "string", packageName) }
            if (currentId == targetId && targetId != 0) {
                logE("stringReplaceByValue ${targetId} $currentId")
                it.result = value
            }
        }
        listOf("getText", "getString").forEach { methodName ->
            TypedArray::class.java.afterHookAllMethods(methodName) {
                this as TypedArray
                val index = it.args[0] as Int
                val currentId = this.getResourceId(index, 0)
                if (currentId == 0) return@afterHookAllMethods
                val mResources = this.getObjectFieldAs<Resources>("mResources")
                val targetId = getCachedIdBy(key) { mResources.getId(name, "string", packageName) }
                if (currentId == targetId) {
                    it.result = value
                }
            }
        }
    }

    fun intArrayReplaceById(
        name: String,
        packageName: String,
        @ArrayRes newId: Int
    ){
        val key = "$packageName intArray: $name"
        Resources::class.java.beforeHookMethod("getIntArray", Int::class.java) {
            this as Resources
            val currentId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val targetId = getCachedIdBy(key) { mResourcesImpl.getId(name, "array", packageName) }
            if (currentId == targetId && targetId != 0) {
                it.args[0] = newId
            }
        }
    }

    fun intArrayReplaceByIdName(
        name: String,
        packageName: String,
        newName: String
    ) {
        val key = "$packageName intArray:$name"
        Resources::class.java.afterHookMethod("getIntArray", Int::class.java) {
            val currentId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val targetId = getCachedIdBy(key) { mResourcesImpl.getId(name, "array", packageName) }
            if (currentId == targetId) {
                val newId = getCachedIdBy(key) { mResourcesImpl.getId(newName, "array", packageName) }
                it.args[0] = newId
            }

        }
    }

    fun intArrayReplaceByIdName(
        name: String,
        packageName: String,
        newName: String,
        newPackageName: String,
    ) {
        val key = "$packageName intArray:$name"
        val newKey = "$newPackageName intArray:$newName"
        Resources::class.java.afterHookMethod("getIntArray", Int::class.java) {
            val currentId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val targetId = getCachedIdBy(key) { mResourcesImpl.getId(name, "array", packageName) }
            if (currentId == targetId) {
                val newId = getCachedIdBy(newKey) { mResourcesImpl.getId(newName, "array", newPackageName) }
                it.args[0] = newId
            }

        }
    }
    
    fun intArrayReplaceByValue(
        name: String,
        packageName: String,
        value: Array<Int>
    ){
        val key = "$packageName intArray: $name"
        Resources::class.java.afterHookMethod("getIntArray", Int::class.java) {
            this as Resources
            val currentId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val targetId =
                getCachedIdBy(key) { mResourcesImpl.getId(name, "array", packageName) }
            if (currentId == targetId && targetId != 0) {
                it.result = value
            }
        }
    }
    fun intArrayReplaceByValue(
        name: String,
        packageName: String,
        value: Array<Int>.()-> Unit
    ){
        val key = "$packageName intArray: $name"
        Resources::class.java.afterHookMethod("getIntArray", Int::class.java) {
            this as Resources
            val currentId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val targetId = getCachedIdBy(key) { mResourcesImpl.getId(name, "array", packageName) }
            if (currentId == targetId && targetId != 0) {
                it.result = (it.result as Array<Int>).apply { value() }
            }
        }
    }


    fun textArrayReplaceById(
        name: String,
        packageName: String,
        @ArrayRes newId: Int
    ){
        val key = "$packageName textArray: $name"
        Resources::class.java.beforeHookMethod("getTextArray", Int::class.java) {
            this as Resources
            val currentId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val targetId = getCachedIdBy(key) { mResourcesImpl.getId(name, "array", packageName) }
            if (currentId == targetId && targetId != 0) {
                it.args[0] = newId
            }
        }
    }

    fun textArrayReplaceByValue(
        name: String,
        packageName: String,
        value: Array<CharSequence>
    ){
        val key = "$packageName textArray: $name"
        Resources::class.java.afterHookMethod("getTextArray", Int::class.java) {
            this as Resources
            val currentId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val targetId =
                getCachedIdBy(key) { mResourcesImpl.getId(name, "array", packageName) }
            if (currentId == targetId && targetId != 0) {
                it.result = value
            }
        }
    }

    fun stringArrayReplaceById(
        name: String,
        packageName: String,
        @ArrayRes newId: Int
    ){
        val key = "$packageName stringArray: $name"
        Resources::class.java.beforeHookMethod("getStringArray", Int::class.java) {
            this as Resources
            val currentId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val targetId = getCachedIdBy(key) { mResourcesImpl.getId(name, "array", packageName) }
            if (currentId == targetId && targetId != 0) {
                it.args[0] = newId
            }
        }

    }

    fun stringArrayReplaceByValue(
        name: String,
        packageName: String,
        value: Array<String>
    ){
        val key = "$packageName stringArray: $name"
        Resources::class.java.afterHookMethod("getStringArray", Int::class.java) {
            this as Resources
            val currentId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val targetId =
                getCachedIdBy(key) { mResourcesImpl.getId(name, "array", packageName) }
            if (currentId == targetId && targetId != 0) {
                it.result = value
            }
        }
    }

}