package com.yunzia.hyperstar.hook.base

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.SeekBar
import androidx.core.graphics.toColorInt
import com.yunzia.hyperstar.hook.core.StarLog.logD
import com.yunzia.hyperstar.hook.core.StarLog.logE
import com.yunzia.hyperstar.hook.core.helper.FieldHelper
import com.yunzia.hyperstar.hook.core.helper.MethodHelper
import com.yunzia.hyperstar.hook.core.helper.callMethod
import com.yunzia.hyperstar.hook.util.android.findViewByIdName
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

object BaseHookHelper {

    fun setColorField(context: Any?, fieldName: String, color: String?) {
        context?.let {
            FieldHelper.setIntField(it, fieldName, Color.parseColor(color))
        }
    }

    fun Resources.getId(name: String, defPackage: String) = this.getIdentifier(name, "id", defPackage)
    fun Resources.getResId(name: String, type: String, defPackage: String) = this.getIdentifier(name, type, defPackage)

    fun Any.getResId(name: String, type: String, defPackage: String) = this.callMethod("getIdentifier", name, type, defPackage) as Int


    fun Resources.getColorBy(name: String, defPackage: String): Int {
        val id = this.getIdentifier(name, "color", defPackage)
        return this.getColor(id, this.newTheme())
    }

    fun Resources.getIntArrayBy(name: String, defPackage: String): IntArray {
        val id = this.getIdentifier(name, "array", defPackage)
        return this.getIntArray(id)
    }




    fun Array<Method?>.onlyInvoke(
        o: Any?,
        vararg objects: Any?
    ): Any? {
        for (method in this) {
            if (method != null) {
                return method.invoke(o, *objects)
            }
        }
        return null
    }

    @Throws(
        IllegalAccessException::class,
        java.lang.IllegalArgumentException::class,
        InvocationTargetException::class
    )
    external fun invoke(o: Any?, vararg objects: Any?): Any?


    fun Class<*>?.findMethodBestMatch(
        methodNames: Array<String>,
        parameterTypes: Array<Class<*>?>,
        vararg args: Any?
    ): Method? {
        var lastError: Throwable? = null
        for (methodName in methodNames) {
            try {
                MethodHelper.findMethodBestMatch(this, methodName, parameterTypes, args)?.let { return it }
            } catch (e: IllegalAccessException) {
                logE(e.toString())
                throw IllegalAccessError(e.message)
            } catch (e: IllegalArgumentException) {
                throw e
            } catch (e: InvocationTargetException) {
                throw InvocationTargetError(e.cause)
            } catch (e: Throwable) {
                lastError = e
            }
        }
        lastError?.let { logE(it.toString()) }
        return null
    }


    fun Class<*>?.findMethodsBestMatch(
        methodsName: Array<String>,
        vararg args: Class<*>?
    ): Method? {
        var lastError: Throwable? = null
        for (methodName in methodsName) {
            try {
                MethodHelper.findMethodBestMatch(this, methodName, *args)?.let { return it }
            } catch (e: IllegalAccessException) {
                logE(e.toString())
                throw IllegalAccessError(e.message)
            } catch (e: IllegalArgumentException) {
                throw e
            } catch (e: InvocationTargetException) {
                throw InvocationTargetError(e.cause)
            } catch (e: Throwable) {
                lastError = e
            }
        }
        lastError?.let { logE(it.toString()) }
        return null
    }

    fun Class<*>?.findMethodBestMatchIfExist(
        methodName: String,
        vararg args: Class<*>?
    ): Method? {
        try {
            val method = MethodHelper.findMethodBestMatch(this, methodName, *args)
            return method
        } catch (e: NoSuchMethodError) {
            return null
        } catch (e: java.lang.Exception) {
            return null
        }

    }

    fun Class<*>?.findMethodBestMatch(
        methodName: String,
        vararg args: Class<*>?
    ): Method? {
        try {
            val method = MethodHelper.findMethodBestMatch(this, methodName, *args)
            return method
        } catch (e: NoSuchMethodError) {
            logE(e.toString())
            return null
        } catch (e: java.lang.Exception) {
            logE(e.toString())
            return null
        }
    }

    class InvocationTargetError(cause: Throwable?) : Error(cause) {
        companion object {
            private const val serialVersionUID = -1070936889459514628L
        }
    }



//    fun <T> Class<*>?.callStaticMethodAs(methodName: String, vararg args: Any?):T {
//        return this.callStaticMethod(methodName, *args) as T
//    }



    fun Class<*>?.findMethodExactIfExists(
        methodName: String,
        vararg parameterTypes: Any?
    ): Method? {
        return MethodHelper.findMethodExactIfExists(this, methodName, *parameterTypes)
    }


    fun Class<*>?.findMethodExt(
        methodName: String,
        ext: Method.()->Boolean = { true }
    ): Method? {
        this ?: return null

        for (method in declaredMethods) {
            if (
                method.name == methodName && method.ext()
            ) {
                return method
            }
        }
        logE("找不到符合条件的方法：$methodName, ext = $ext")
        return null
    }



    fun Class<*>.allMethod(
        methodName: String,
    ): MutableSet<Method> {
        val unhooks: MutableSet<Method> = HashSet()
        for (method in this.getDeclaredMethods()){
            if (method.name == methodName) {
                unhooks.add(method)
            }
        }
        return unhooks
    }

//    fun MutableSet<Method>.after(
//        methodHook: Any?.(param:MethodHookParam) -> Unit
//    ){
//        val unhooks: MutableSet<XC_MethodHook.Unhook> = HashSet()
//        for (method in this){
//            unhooks.add(
//                XposedBridge.hookMethod(method, object :XC_MethodHook(){
//                    override fun afterHookedMethod(param: MethodHookParam) {
//                        param.thisObject.methodHook(param)
//                    }
//                })
//            )
//        }
//    }





}

fun SeekBar.percentageProgress(
    progress:Int = this.progress,
    max:Int = this.max
) = "${(progress * 100 / max)}%"

inline fun <T, R> T.runCatchingOrNull(func: T.() -> R?) = try {
    func()
} catch (e: Throwable) {
    logD(e.toString())
    null
}




fun getDrawable(res: Resources, name: String, defPackage: String): Drawable {
    val id = res.getIdentifier(name, "drawable", defPackage)
    return res.getDrawable(id, res.newTheme())
}

fun getColor(res: Resources, name: String, defPackage: String): Int {
    val id = res.getIdentifier(name, "color", defPackage)
    return res.getColor(id, res.newTheme())
}

fun getColor(res: Resources, name: String, defPackage: String, defColor: String): Int {
    try {
        val id = res.getIdentifier(name, "color", defPackage)
        return res.getColor(id, res.newTheme())
    } catch (e: Resources.NotFoundException) {
        logE("color $name is not found!")
        return defColor.toColorInt()
    }
}

fun getDimension(res: Resources, name: String, defPackage: String): Float {
    val id = res.getIdentifier(name, "dimen", defPackage)
    return res.getDimension(id)
}

fun getDimensionPixelOffset(res: Resources, name: String, defPackage: String): Int {
    val id = res.getIdentifier(name, "dimen", defPackage)
    return res.getDimensionPixelOffset(id)
}

fun getDimensionPixelSize(res: Resources, name: String, defPackage: String): Int {
    val id = res.getIdentifier(name, "dimen", defPackage)
    return res.getDimensionPixelSize(id)
}



fun <T> View.findViewByIdNameAs(name: String): T{
    return this.findViewByIdName(name) as T
}

//    private fun getParameterClasses(
//        classLoader: ClassLoader,
//        parameterTypesAndCallback: Array<Any>
//    ): Array<Class<*>> {
//        var parameterClasses: Array<Class<*>>? = null
//        val parameterTypes = parameterTypesAndCallback.map { it.javaClass }.toTypedArray()
//
//        return parameterTypes
//    }

