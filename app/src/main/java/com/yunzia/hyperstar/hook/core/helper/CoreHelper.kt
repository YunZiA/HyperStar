package com.yunzia.hyperstar.hook.core.helper

import com.yunzia.hyperstar.hook.core.finder.findClass

abstract class CoreHelper {
    fun getParameterClasses(
        loader: ClassLoader?,
        params: Array<out Any?>
    ): Array<Class<*>> = params.map { param ->
        when (param) {
            null -> Any::class.java
            is Class<*> -> param
            is String -> findClass(param, loader) ?: Any::class.java
            else -> param.javaClass
        }
    }.toTypedArray()

    fun getParametersString(parameterTypes: Array<out Class<*>>): String = parameterTypes.joinToString(",") { it.name }
}