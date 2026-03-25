package com.yunzia.hyperstar.hook.core.helper

import com.yunzia.hyperstar.hook.core.StarLog.log
import com.yunzia.hyperstar.hook.core.StarLog.logE
import com.yunzia.hyperstar.hook.core.StarLog.logW
import com.yunzia.hyperstar.hook.core.XposedCore
import com.yunzia.hyperstar.hook.core.helper.MethodHelper.findMethodExact
import io.github.libxposed.api.XposedInterface
import java.lang.reflect.Executable
import java.lang.reflect.Method

class HookResult<T> private constructor(
    private var _value: T?,
    private var _hasValue: Boolean
) {

    companion object {

        fun <T> noResult(): HookResult<T> {
            return HookResult(null, false)
        }

        fun <T> of(value: T): HookResult<T> {
            return HookResult(value, true)
        }
    }

    private var _replaced = false
    private var _skipOriginal = false

    val hasValue get() = _hasValue

    val isReplaced get() = _replaced

    val isSkipped get() = _skipOriginal

    var value: T
        get() {
            check(_hasValue) {
                "HookResult has no value yet. Accessed before original method execution."
            }
            return _value as T
        }
        set(newValue) {
            _value = newValue
            _hasValue = true
            _replaced = true
        }

    fun replace(newValue: T) {
        value = newValue
    }

    fun skipOriginal() {
        _skipOriginal = true
    }
}


@JvmSynthetic
fun <T : Executable> T.createHook(): XposedInterface.HookBuilder = XposedCore.base.hook(this)

inline fun XposedInterface.HookBuilder.replace(
    crossinline block: XposedInterface.Chain.(List<Any?>) -> Any?
): XposedInterface.HookHandle  = intercept { chain ->
    try {
        return@intercept chain.block(chain.args)
    } catch (t: Throwable) {
        logE("HookError", "Error in after-hook: ${chain.executable.declaringClass.name}#${chain.executable.name}", t)
        return@intercept chain.proceed()
    }
}
inline fun XposedInterface.HookBuilder.before(
    crossinline block: XposedInterface.Chain.(MutableList<Any?>, HookResult<Any?>) -> Unit
): XposedInterface.HookHandle  = intercept { chain ->
    val result: HookResult<Any?> = HookResult.noResult()
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

inline fun XposedInterface.HookBuilder.after(
    crossinline block: XposedInterface.Chain.(List<Any?>, HookResult<Any?>) -> Unit
): XposedInterface.HookHandle  = intercept { chain ->
    val result = HookResult.of(chain.proceed())
    try {
        chain.block(chain.args, result)
    } catch (t: Throwable) {
        logE("HookError", "Error in after-hook: ${chain.executable.declaringClass.name}#${chain.executable.name}", t)
    }
    return@intercept result.value
}

inline fun <T : Executable> T.replaceHook(
    crossinline block: XposedInterface.Chain.(List<Any?>) -> Any?
) {
    this.createHook().replace(block)
}
inline fun <T : Executable> T.beforeHook(
    crossinline block: XposedInterface.Chain.(MutableList<Any?>, HookResult<Any?>) -> Unit
) {
    this.createHook().before(block)
}
inline fun <T : Executable> T.afterHook(
    crossinline block: XposedInterface.Chain.(List<Any?>, HookResult<Any?>) -> Unit
) {
    this.createHook().after(block)
}

inline fun Class<*>?.forEachMethod(
    methodName: String,
    block: (Method) -> Unit
) {
    if (this == null) {
        logW("hookAllMethod", "$methodName in null class")
        return
    }

    declaredMethods.forEach {
        if (it.name == methodName) block(it)
    }
}

inline fun Class<*>?.hookAllMethods(
    methodName: String,
    crossinline block: XposedInterface.HookBuilder.() -> Unit
) {
    if (this == null) {
        logW("hookAllMethod","$methodName in null class")
        return
    }
    forEachMethod(methodName){
        it.createHook().block()
    }

}

inline fun Class<*>?.beforeHookAllMethods(
    methodName: String,
    crossinline block: XposedInterface.Chain.(MutableList<Any?>, HookResult<Any?>) -> Unit
) {
    if (this == null) {
        logW("hookAllMethod", "$methodName in null class")
        return
    }
    forEachMethod(methodName){
        it.beforeHook(block)
    }
}

inline fun Class<*>?.afterHookAllMethods(
    methodName: String,
    crossinline block: XposedInterface.Chain.(List<Any?>, HookResult<Any?>) -> Unit
) {
    if (this == null) {
        logW("hookAllMethod","$methodName in null class")
        return
    }
    forEachMethod(methodName){
        it.afterHook(block)
    }

}

inline fun Class<*>?.hookMethod(
    methodName: String,
    vararg paramTypes: Any?,
    crossinline block: XposedInterface.HookBuilder.() -> Unit
) {

    findMethodExact(
        this,
        methodName,
        *paramTypes
    )?.createHook()?.block()

}

inline fun Class<*>?.beforeHookMethod(
    methodName: String,
    vararg paramTypes: Any?,
    crossinline block: XposedInterface.Chain.(MutableList<Any?>, HookResult<Any?>) -> Unit
) {
    findMethodExact(this, methodName, *paramTypes)?.beforeHook(block)
}

inline fun Class<*>?.afterHookMethod(
    methodName: String,
    vararg paramTypes: Any?,
    crossinline block: XposedInterface.Chain.(List<Any?>, HookResult<Any?>) -> Unit
) {
    findMethodExact(this, methodName, *paramTypes)?.afterHook(block)

}

inline fun Class<*>?.replaceHookMethod(
    methodName: String,
    vararg paramTypes: Any?,
    crossinline block: XposedInterface.Chain.(List<Any?>) -> Any?
) {
    findMethodExact(this, methodName, *paramTypes)?.replaceHook(block)
}


inline fun Class<*>?.hookAllConstructors(
    crossinline block: XposedInterface.HookBuilder.() -> Unit
) {
    this?: return

    for (constructor in this.declaredConstructors) {
        constructor.createHook().block()
    }
}

inline fun Class<*>?.beforeHookAllConstructors(
    crossinline block: XposedInterface.Chain.(MutableList<Any?>, HookResult<Any?>) -> Unit
) {
    this?: return

    for (constructor in this.declaredConstructors) {
        constructor.beforeHook(block)
    }
}

inline fun Class<*>?.afterHookAllConstructors(
    crossinline block: XposedInterface.Chain.(List<Any?>, HookResult<Any?>) -> Unit
) {
    this?: return

    for (constructor in this.declaredConstructors) {
        constructor.afterHook(block)
    }
}
inline fun Class<*>?.replaceHookAllConstructors(
    crossinline block: XposedInterface.Chain.(List<Any?>) -> Any?
) {
    this?: return

    for (constructor in this.declaredConstructors) {
        constructor.replaceHook(block)
    }
}

inline fun Class<*>?.hookConstructor(
    vararg parameterTypes: Any?,
    crossinline block: XposedInterface.HookBuilder.() -> Unit
) {
    findConstructorExact(*parameterTypes)?.createHook()?.block()
}

inline fun Class<*>?.beforeHookConstructor(
    vararg parameterTypes: Any?,
    crossinline block: XposedInterface.Chain.(MutableList<Any?>, HookResult<Any?>) -> Unit
) {
    findConstructorExact(*parameterTypes)?.beforeHook(block)
}

inline fun Class<*>?.afterHookConstructor(
    vararg parameterTypes: Any?,
    crossinline block: XposedInterface.Chain.(List<Any?>, HookResult<Any?>) -> Unit
) {
    findConstructorExact(*parameterTypes)?.afterHook(block)
}



