package com.yunzia.hyperstar.hook.core.helper

import android.content.res.Resources
import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.IntegerRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.yunzia.hyperstar.hook.base.BaseHookHelper.getId
import com.yunzia.hyperstar.hook.core.Log.logD
import com.yunzia.hyperstar.hook.core.Log.logE
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
        @ColorRes replaceId: Int
    ) {
        val key = "$packageName color:$name"
        Resources::class.java.beforeHookAllMethods("getColor") {
            val resourceId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val id = getCachedIdBy(key) { mResourcesImpl.getId(name, "color", packageName) }
            if (resourceId == id) {
                it.args[0] = replaceId
            }

        }
    }

    fun colorReplaceByValue(
        name: String,
        packageName: String,
        color: Int
    ) {
        val key = "$packageName color:$name"
        Resources::class.java.afterHookAllMethods("getColor") {
            val resourceId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val id = getCachedIdBy(key) { mResourcesImpl.getId(name, "color", packageName) }
            if (resourceId == id) {
                it.result = color
            }

        }
    }

    fun dimenReplaceById(
        packageName: String,
        name: String,
        @DimenRes replaceId: Int
    ) {
        val key = "$packageName dimen:$name"
        // hook Resources 的 dimension 获取方法
        fun hookDimensionMethod(methodName: String) {
            Resources::class.java.beforeHookMethod(methodName, Int::class.java) {
                val resourceId = it.args[0] as Int
                val mResourcesImpl = ResourcesImpl(this)
                val id = getCachedIdBy(key) { mResourcesImpl.getId(name, "dimen", packageName) }
                if (resourceId == id) {
                    it.args[0] = replaceId
                }
            }
        }
        // hook TypedArray 的 dimension 获取方法
        fun hookTADimensionMethod(methodName: String) {
            TypedArray::class.java.afterHookAllMethods(methodName) {
                this as TypedArray
                val index = it.args[0] as Int
                val resourceId = this.getResourceId(index, 0)
                if (resourceId == 0) return@afterHookAllMethods
                val mResources = this.getObjectFieldAs<Resources>("mResources")
                val id = getCachedIdBy(key) { mResources.getId(name, "dimen", packageName) }
                if (resourceId == id) {
                    when (it.member.name) {
                        "getDimension" -> it.result = mResources.getDimension(replaceId)
                        "getDimensionPixelSize" -> it.result = mResources.getDimensionPixelSize(replaceId)
                        "getDimensionPixelOffset" -> it.result = mResources.getDimensionPixelOffset(replaceId)
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
                val resourceId = it.args[0] as Int
                val mResourcesImpl = ResourcesImpl(this)
                val id = getCachedIdBy(key) { mResourcesImpl.getId(name, "dimen", packageName) }
                if (resourceId == id) {
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

                val resourceId = this.getResourceId(index, 0)
                if (resourceId == 0) return@afterHookAllMethods

                val mResources = this.getObjectFieldAs<Resources>("mResources")
                val mResourcesImpl = ResourcesImpl(mResources)
                val id = getCachedIdBy(key) { mResources.getId(name, "dimen", packageName) }
                if (resourceId == id) {
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

    fun integerReplaceById(
        packageName: String,
        name: String,
        @IntegerRes replaceId: Int
    ) {
        val key = "$packageName integer: $name"
        Resources::class.java.beforeHookAllMethods("getInteger") {
            this as Resources
            val originalResourceId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)

            val targetResourceId = getCachedIdBy(key) { mResourcesImpl.getId(name, "integer", packageName) }
            if (originalResourceId == targetResourceId && targetResourceId != 0) {
                it.args[0] = replaceId
            }
        }
    }

    fun layoutReplaceById(
        packageName: String,
        name: String,
        @LayoutRes replaceId: Int
    ) {
        val key = "$packageName layout: $name"
        Resources::class.java.beforeHookMethod("getLayout", Int::class.java) {
            this as Resources
            val originalResourceId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)

            val targetResourceId = getCachedIdBy(key) { mResourcesImpl.getId(name, "layout", packageName) }
            if (originalResourceId == targetResourceId && targetResourceId != 0) {
                it.args[0] = replaceId
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
            val originalResourceId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this)
            val targetResourceId = getCachedIdBy(key) { mResourcesImpl.getId(name, "integer", packageName) }
            if (originalResourceId == targetResourceId && targetResourceId != 0) {
                it.result = value
            }
        }
    }

    fun hookLayout(
        name: String,
        packageName: String,
        block: View.()-> Unit
    ){
        val key = "$packageName integer: $name"
        LayoutInflater::class.java.afterHookMethod(
            "inflate", Int::class.java, ViewGroup::class.java, Boolean::class.java
        ) {
            this as LayoutInflater
            val originalResourceId = it.args[0] as Int
            val mResourcesImpl = ResourcesImpl(this.context)
            val targetResourceId = getCachedIdBy(key) { mResourcesImpl.getId(name, "layout", packageName) }
            if (originalResourceId == targetResourceId && targetResourceId != 0) {
                val inflatedView: View = it.result as? View ?: return@afterHookMethod
                inflatedView.block()
            }

        }
    }

    fun stringReplaceById(
        packageName: String,
        name: String,
        @StringRes replaceId: Int
    ) {
        val key = "$packageName string: $name"
        listOf( "getText", "getString").forEach { methodName ->
            Resources::class.java.beforeHookMethod(methodName, Int::class.java) {
                this as Resources
                val originalResourceId = it.args[0] as Int
                val mResourcesImpl = ResourcesImpl(this)
                val targetResourceId = getCachedIdBy(key) { mResourcesImpl.getId(name, "string", packageName) }
                if (originalResourceId == targetResourceId && targetResourceId != 0) {
                    it.args[0] = replaceId
                }
            }
        }
    }

    fun stringReplaceByValue(
        name: String,
        packageName: String,
        value: String
    ) {
        val key = "$packageName string: $name"

        listOf("getText", "getString").forEach { methodName ->
            Resources::class.java.afterHookMethod(methodName, Int::class.java) {
                this as Resources
                val originalResourceId = it.args[0] as Int
                val mResourcesImpl = ResourcesImpl(this)
                val targetResourceId =
                    getCachedIdBy(key) { mResourcesImpl.getId(name, "string", packageName) }
                if (originalResourceId == targetResourceId && targetResourceId != 0) {
                    it.result = value
                }
            }
        }
    }
}