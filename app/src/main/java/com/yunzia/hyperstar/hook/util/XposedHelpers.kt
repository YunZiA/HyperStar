package com.yunzia.hyperstar.hook.util

import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedInterface.MethodUnhooker
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

object XposedHelpers {

    private var mBase: XposedInterface? = null

    fun init(mBase: XposedInterface) {
        this.mBase = mBase
    }


    class InvocationTargetError
        (cause: Throwable?) : Error(cause) {
        companion object {
            private val serialVersionUID = -1070936889459514628L
        }
    }

    // Helper to ensure mBase is initialized
    private fun checkInitialized() {
        if (mBase == null) {
            throw IllegalStateException("XposedHelpers not initialized. Call init() first.")
        }
    }

    fun hook(
        origin: Method,
        hooker: Class<out XposedInterface.Hooker>
    ): MethodUnhooker<Method> {
        checkInitialized()
        return mBase!!.hook(origin, hooker)
    }

    fun <T : Any> hookClassInitializer(
        origin: Class<T>,
        hooker: Class<out XposedInterface.Hooker>
    ): MethodUnhooker<Constructor<T>> {
        checkInitialized()
        return mBase!!.hookClassInitializer(origin, hooker)
    }

    fun <T : Any> hookClassInitializer(
        origin: Class<T>,
        priority: Int,
        hooker: Class<out XposedInterface.Hooker>
    ): MethodUnhooker<Constructor<T>> {
        checkInitialized()
        return mBase!!.hookClassInitializer(origin, priority, hooker)
    }

    fun hook(
        origin: Method,
        priority: Int,
        hooker: Class<out XposedInterface.Hooker>
    ): MethodUnhooker<Method> {
        checkInitialized()
        return mBase!!.hook(origin, priority, hooker)
    }

    fun <T : Any> hook(
        origin: Constructor<T>,
        hooker: Class<out XposedInterface.Hooker>
    ): MethodUnhooker<Constructor<T>> {
        checkInitialized()
        return mBase!!.hook(origin, hooker)
    }

    fun <T : Any> hook(
        origin: Constructor<T>,
        priority: Int,
        hooker: Class<out XposedInterface.Hooker>
    ): MethodUnhooker<Constructor<T>> {
        checkInitialized()
        return mBase!!.hook(origin, priority, hooker)
    }

    @Throws(InvocationTargetException::class, IllegalArgumentException::class, IllegalAccessException::class)
    fun invokeOrigin(method: Method, thisObject: Any?, vararg args: Any?): Any? {
        checkInitialized()
        return mBase!!.invokeOrigin(method, thisObject, *args)
    }

    @Throws(InvocationTargetException::class, IllegalArgumentException::class, IllegalAccessException::class)
    fun <T : Any> invokeOrigin(constructor: Constructor<T>, thisObject: T, vararg args: Any?) {
        checkInitialized()
        mBase!!.invokeOrigin(constructor, thisObject, *args)
    }

    @Throws(InvocationTargetException::class, IllegalArgumentException::class, IllegalAccessException::class)
    fun invokeSpecial(method: Method, thisObject: Any, vararg args: Any?): Any? {
        checkInitialized()
        return mBase!!.invokeSpecial(method, thisObject, *args)
    }

    @Throws(InvocationTargetException::class, IllegalArgumentException::class, IllegalAccessException::class)
    fun <T : Any> invokeSpecial(constructor: Constructor<T>, thisObject: T, vararg args: Any?) {
        checkInitialized()
        mBase!!.invokeSpecial(constructor, thisObject, *args)
    }

    @Throws(InvocationTargetException::class, IllegalArgumentException::class, IllegalAccessException::class, InstantiationException::class)
    fun <T : Any> newInstanceOrigin(constructor: Constructor<T>, vararg args: Any?): T {
        checkInitialized()
        return mBase!!.newInstanceOrigin(constructor, *args)
    }

    @Throws(InvocationTargetException::class, IllegalArgumentException::class, IllegalAccessException::class, InstantiationException::class)
    fun <T : Any, U : Any> newInstanceSpecial(
        constructor: Constructor<T>,
        subClass: Class<U>,
        vararg args: Any?
    ): U {
        checkInitialized()
        return mBase!!.newInstanceSpecial(constructor, subClass, *args)
    }

    fun log(message: String) {
        mBase?.log(message)
    }

    fun log(message: String, throwable: Throwable) {
        mBase?.log(message, throwable)
    }
}