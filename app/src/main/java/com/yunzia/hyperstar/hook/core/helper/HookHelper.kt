package com.yunzia.hyperstar.hook.core.helper

import com.yunzia.hyperstar.hook.core.Log.log
import com.yunzia.hyperstar.hook.core.Log.logD
import com.yunzia.hyperstar.hook.core.Log.logE
import com.yunzia.hyperstar.hook.core.Log.logW
import com.yunzia.hyperstar.hook.core.helper.ConstructorHelper.getParameterClasses
import com.yunzia.hyperstar.hook.core.helper.MethodHelper.findMethodExact
import io.github.kyuubiran.ezxhelper.xposed.common.AfterHookParam
import io.github.kyuubiran.ezxhelper.xposed.common.BeforeHookParam
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createAfterHook
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createBeforeHook
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook
import java.lang.reflect.Method

fun Class<*>?.hookAllMethods(
    methodName: String,
    block: HookFactory.() -> Unit
) {
    this?: run {
        logW("hookAllMethod","$methodName in null class")
        return
    }
    try {
        for (method in this.getDeclaredMethods()){
            if (method.name == methodName) {
                method.createHook(block = block)
            }
        }
    }catch (e : Exception){
        logE("${this.simpleName}","${e.cause}")
    }
}

fun Class<*>?.beforeHookAllMethods(
    methodName: String,
    block: Any?.(BeforeHookParam) -> Unit
) {
    this?: run {
        logW("hookAllMethod","$methodName in null class")
        return
    }
    try {
        for (method in this.getDeclaredMethods()){
            if (method.name == methodName) {
                method.createBeforeHook { it.thisObjectOrNull.block(it) }
            }
        }
    }catch (e : Exception){
        logE("${this.simpleName}","${e.cause}")
    }
}

fun Class<*>?.afterHookAllMethods(
    methodName: String,
    block: Any?.(AfterHookParam) -> Unit
) {
    this?: run {
        logW("hookAllMethod","$methodName in null class")
        return
    }
    try {
        for (method in this.getDeclaredMethods()){
            if (method.name == methodName) {
                method.createAfterHook { it.thisObjectOrNull.block(it) }
            }
        }
    }catch (e : Exception){
        logE("${this.simpleName}","${e.cause}")
    }
}

fun Class<*>?.hookMethod(
    methodName: String,
    vararg paramTypes: Any?,
    block: HookFactory.() -> Unit
) {
    try {
        findMethodExact(
            this,
            methodName,
            paramTypes
        )?.createHook(block = block)
    }catch (e : Exception){
        logE("${this?.simpleName}","${e.cause}")
    }
}

fun Class<*>?.beforeHookMethod(
    methodName: String,
    vararg paramTypes: Any?,
    block: Any?.(BeforeHookParam) -> Unit
) {
    try {
        logD("${this?.simpleName}","$methodName")
        findMethodExact(this, methodName, *paramTypes)?.createBeforeHook { it.thisObjectOrNull.block(it) }
        logD("${this?.simpleName}","$methodName")
    }catch (e : Exception){
        logE("${this?.simpleName}","${e.cause}")
    }
}

fun Class<*>?.afterHookMethod(
    methodName: String,
    vararg paramTypes: Any?,
    block: Any?.(AfterHookParam) -> Unit
) {
    try {
        findMethodExact(this, methodName, *paramTypes)?.createAfterHook { it.thisObjectOrNull.block(it) }
    }catch (e : Exception){
        logE("${this?.simpleName}","${e.cause}")
    }
}

fun Class<*>?.replaceHookMethod(
    methodName: String,
    vararg paramTypes: Any?,
    block: Any?.(BeforeHookParam) -> Any?
) {
    try {
        findMethodExact(this, methodName, *paramTypes).replaceHook(block)
    }catch (e : Exception){
        logE("${this?.simpleName}","${e.cause}")
    }
}

fun Method?.replaceHook(
    block: Any?.(BeforeHookParam) -> Any?
) {
    try {
        this?.createHook { replace { it.thisObjectOrNull.block(it) } }
    } catch (e : Exception){
        logE("${this?.name}","${e.cause}")
    }
}


fun Class<*>?.hookAllConstructors(
    block: HookFactory.() -> Unit
) {
    this?: return

    try {
        for (constructor in this.declaredConstructors) {
            constructor.createHook(block = block)
        }
    }catch (e : Exception){
        logE("${this.simpleName}","${e.cause}")
    }
}

fun Class<*>?.beforeHookAllConstructors(
    block: Any?.(BeforeHookParam) -> Unit
) {
    this?: return

    try {
        for (constructor in this.declaredConstructors) {
            constructor.createBeforeHook { it.thisObjectOrNull.block(it) }
        }
    }catch (e : Exception){
        logE("${this.simpleName}","${e.cause}")
    }
}

fun Class<*>?.afterHookAllConstructors(
    block: Any?.(AfterHookParam) -> Unit
) {
    this?: return

    try {
        for (constructor in this.declaredConstructors) {
            constructor.createAfterHook { it.thisObjectOrNull.block(it) }
        }
    }catch (e : Exception){
        logE("${this.simpleName}","${e.cause}")
    }
}
fun Class<*>?.replaceHookAllConstructors(
    block: Any?.(BeforeHookParam) -> Any?
) {
    this?: return

    try {
        for (constructor in this.declaredConstructors) {
            constructor.createHook { replace { it.thisObjectOrNull.block(it) } }
        }
    }catch (e : Exception){
        logE("${this.simpleName}","${e.cause}")
    }
}

fun Class<*>?.hookConstructor(
    vararg parameterTypes: Any?,
    block: HookFactory.() -> Unit
) {
    try {
        findConstructorExact(*parameterTypes)?.createHook(block = block)
    } catch (e : Exception){
        logE("${this?.simpleName}","${e.cause}")
    }
}

fun Class<*>?.beforeHookConstructor(
    vararg parameterTypes: Any?,
    block: Any?.(BeforeHookParam) -> Unit
) {
    try {
        findConstructorExact(*parameterTypes)?.createBeforeHook { it.thisObjectOrNull.block(it) }
    } catch (e : Exception){
        logE("${this?.simpleName}","${e.cause}")
    }
}

fun Class<*>?.afterHookConstructor(
    vararg parameterTypes: Any?,
    block: Any?.(AfterHookParam) -> Unit
) {
    try {
        log("${parameterTypes}")
        findConstructorExact(*parameterTypes)?.createAfterHook { it.thisObjectOrNull.block(it) }
    } catch (e : Exception){
        logE("${this?.simpleName}","${e.cause}")
    }
}


