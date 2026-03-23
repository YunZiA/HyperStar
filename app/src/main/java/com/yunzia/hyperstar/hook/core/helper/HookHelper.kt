package com.yunzia.hyperstar.hook.core.helper

import com.yunzia.hyperstar.hook.core.StarLog.log
import com.yunzia.hyperstar.hook.core.StarLog.logE
import com.yunzia.hyperstar.hook.core.StarLog.logW
import com.yunzia.hyperstar.hook.core.XposedCore
import com.yunzia.hyperstar.hook.core.helper.MethodHelper.findMethodExact
import io.github.libxposed.api.XposedInterface
import java.lang.reflect.Executable
import java.lang.reflect.Method


data class HookResult<T>(
    val originalValue: T
) {
    private var _value: T = originalValue
    private var _replaced: Boolean = false

    var value: T
        get() = _value
        set(newValue) {
            _value = newValue
            _replaced = true
        }

    val isReplaced: Boolean
        get() = _replaced

    fun replace(newValue: T) {
        value = newValue
    }
}

@JvmSynthetic
private fun <T : Executable> T.createHook(): XposedInterface.HookBuilder = XposedCore.base.hook(this)

fun XposedInterface.HookBuilder.replace(block: XposedInterface.Chain.(List<Any?>) -> Any?): XposedInterface.HookHandle  = intercept { chain ->
    try {
        return@intercept chain.block(chain.args)
    } catch (t: Throwable) {
        logE("HookError", "Error in after-hook: ${chain.executable.declaringClass.name}#${chain.executable.name}", t)
        return@intercept chain.proceed()
    }
}
fun XposedInterface.HookBuilder.before(block: XposedInterface.Chain.(MutableList<Any?>, HookResult<Any?>) -> Unit): XposedInterface.HookHandle  = intercept { chain ->
    val result: HookResult<Any?> = HookResult(Unit)
    val args: MutableList<Any?> = chain.args.toMutableList()
    try {
        chain.block(args, result)
    } catch (t: Throwable) {
        logE("HookError", "Error in before-hook: ${chain.executable.declaringClass.name}#${chain.executable.name}", t)
    }
    val oldResult = chain.proceed(args.toTypedArray())
    if (result.isReplaced) {
        return@intercept result.value
    }
    return@intercept oldResult
}

fun XposedInterface.HookBuilder.after(block: XposedInterface.Chain.(List<Any?>, HookResult<Any?>) -> Unit): XposedInterface.HookHandle  = intercept { chain ->
    val result = HookResult(chain.proceed())
    try {
        chain.block(chain.args, result)
    } catch (t: Throwable) {
        logE("HookError", "Error in after-hook: ${chain.executable.declaringClass.name}#${chain.executable.name}", t)
    }
    return@intercept result.value
}

fun <T : Executable> T.replaceHook(
    block: XposedInterface.Chain.(List<Any?>) -> Any?
) {
    this.createHook().replace(block)
}
fun <T : Executable> T.beforeHook(
    block: XposedInterface.Chain.(MutableList<Any?>, HookResult<Any?>) -> Unit
) {
    this.createHook().before(block)
}
fun <T : Executable> T.afterHook(
    block: XposedInterface.Chain.(List<Any?>, HookResult<Any?>) -> Unit
) {
    this.createHook().after(block)
}

fun Class<*>?.hookAllMethods(
    methodName: String,
    block: XposedInterface.HookBuilder.() -> Unit
) {
    this?: run {
        logW("hookAllMethod","$methodName in null class")
        return
    }
    for (method in this.getDeclaredMethods()){
        if (method.name == methodName) {
            method.createHook().block()
        }
    }

}

fun Class<*>?.beforeHookAllMethods(
    methodName: String,
    block: XposedInterface.Chain.(MutableList<Any?>, HookResult<Any?>) -> Unit
) {
    this ?: run {
        logW("hookAllMethod", "$methodName in null class")
        return
    }
    for (method in this.getDeclaredMethods()) {
        if (method.name == methodName) {
            method.beforeHook(block)
        }
    }
}

fun Class<*>?.afterHookAllMethods(
    methodName: String,
    block: XposedInterface.Chain.(List<Any?>, HookResult<Any?>) -> Unit
) {
    this?: run {
        logW("hookAllMethod","$methodName in null class")
        return
    }
    for (method in this.getDeclaredMethods()){
        if (method.name == methodName) {
            method.afterHook(block)
        }
    }

}

fun Class<*>?.hookMethod(
    methodName: String,
    vararg paramTypes: Any?,
    block: XposedInterface.HookBuilder.() -> Unit
) {

    findMethodExact(
        this,
        methodName,
        paramTypes
    )?.createHook()

}

fun Class<*>?.beforeHookMethod(
    methodName: String,
    vararg paramTypes: Any?,
    block: XposedInterface.Chain.(MutableList<Any?>, HookResult<Any?>) -> Unit
) {
    findMethodExact(this, methodName, *paramTypes)?.beforeHook(block)
}

fun Class<*>?.afterHookMethod(
    methodName: String,
    vararg paramTypes: Any?,
    block: XposedInterface.Chain.(List<Any?>, HookResult<Any?>) -> Unit
) {
    findMethodExact(this, methodName, *paramTypes)?.afterHook(block)

}

fun Class<*>?.replaceHookMethod(
    methodName: String,
    vararg paramTypes: Any?,
    block: XposedInterface.Chain.(List<Any?>) -> Any?
) {
    findMethodExact(this, methodName, *paramTypes)?.replaceHook(block)
}


fun Class<*>?.hookAllConstructors(
    block: XposedInterface.HookBuilder.() -> Unit
) {
    this?: return

    for (constructor in this.declaredConstructors) {
        constructor.createHook()
    }
}

fun Class<*>?.beforeHookAllConstructors(
    block: XposedInterface.Chain.(MutableList<Any?>, HookResult<Any?>) -> Unit
) {
    this?: return

    for (constructor in this.declaredConstructors) {
        constructor.beforeHook(block)
    }
}

fun Class<*>?.afterHookAllConstructors(
    block: XposedInterface.Chain.(List<Any?>, HookResult<Any?>) -> Unit
) {
    this?: return

    for (constructor in this.declaredConstructors) {
        constructor.afterHook(block)
    }
}
fun Class<*>?.replaceHookAllConstructors(
    block: XposedInterface.Chain.(List<Any?>) -> Any?
) {
    this?: return

    for (constructor in this.declaredConstructors) {
        constructor.replaceHook(block)
    }
}

fun Class<*>?.hookConstructor(
    vararg parameterTypes: Any?,
    block: XposedInterface.HookBuilder.() -> Unit
) {
    findConstructorExact(*parameterTypes)?.createHook()
}

fun Class<*>?.beforeHookConstructor(
    vararg parameterTypes: Any?,
    block: XposedInterface.Chain.(MutableList<Any?>, HookResult<Any?>) -> Unit
) {
    findConstructorExact(*parameterTypes)?.beforeHook(block)
}

fun Class<*>?.afterHookConstructor(
    vararg parameterTypes: Any?,
    block: XposedInterface.Chain.(List<Any?>, HookResult<Any?>) -> Unit
) {
    findConstructorExact(*parameterTypes)?.afterHook(block)
}


