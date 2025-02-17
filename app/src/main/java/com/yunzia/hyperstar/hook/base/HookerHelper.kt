package com.yunzia.hyperstar.hook.base

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.SeekBar
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import com.yunzia.hyperstar.hook.tool.starLog
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.lang.invoke.MethodHandles
import java.lang.reflect.Method

abstract class HookerHelper {

    fun setColorField(context: Any?, fieldName: String, color: String?) {
        XposedHelpers.setIntField(context, fieldName, Color.parseColor(color))
    }

    fun Resources.getId(name: String, defPackage: String) = this.getIdentifier(name,"id",defPackage)


    fun Resources.getColorBy(name: String, defPackage: String): Int {
        val id = this.getIdentifier(name, "color", defPackage)
        return this.getColor(id, this.newTheme())
    }

    fun Resources.getIntArrayBy(name: String, defPackage: String): IntArray {
        val id = this.getIdentifier(name, "array", defPackage)
        return this.getIntArray(id)
    }

    fun MethodHookParam.callSuperMethod(): Any? {
        val thisObj = this.thisObject
        val parameterTypes = this.args.map { it?.javaClass }.toTypedArray()
        val superClass = this.thisObject.javaClass.superclass
        val superMethod: Method = XposedHelpers.findMethodBestMatch(superClass,this.method.name,*parameterTypes)
        //MethodHandles.privateLookupIn(superClass,MethodHandles.lookup()).findSpecial(superClass,this.method.name,*parameterTypes,this.javaClass)
        val methodHandle = MethodHandles.lookup().unreflectSpecial(superMethod, thisObj.javaClass)
        return methodHandle.invokeWithArguments(thisObj, *this.args)
    }

    fun Any?.setIntField(fieldName: String,value: Int) = XposedHelpers.setIntField(this, fieldName, value)
    fun Any?.getIntField(fieldName: String) = XposedHelpers.getIntField(this, fieldName)

    fun Any?.setFloatField(fieldName: String,value: Float) = XposedHelpers.setFloatField(this, fieldName,value)
    fun Any?.getFloatField(fieldName: String) = XposedHelpers.getFloatField(this, fieldName)

    fun Any?.setLongField(fieldName: String,value: Long) = XposedHelpers.setLongField(this, fieldName, value)
    fun Any?.getLongField(fieldName: String) = XposedHelpers.getLongField(this, fieldName)

    fun Any?.getBooleanField(fieldName: String) = XposedHelpers.getBooleanField(this, fieldName)

    fun Any?.setObjectField(fieldName: String,value: Any) = XposedHelpers.setObjectField(this, fieldName, value)
    fun Any?.getObjectField(fieldName: String): Any? = XposedHelpers.getObjectField(this, fieldName)
    fun <T> Any?.getObjectFieldAs(fieldName: String): T {
        return this.getObjectField(fieldName) as T
    }

    fun Class<*>?.getStaticObjectField(fieldName: String): Any? =  XposedHelpers.getStaticObjectField(this, fieldName)

    fun  Any?.callMethod(methodName: String, vararg args: Any?):Any? {
        return XposedHelpers.callMethod(this, methodName,*args)
    }

    fun <T> Any?.callMethodAs(methodName: String, vararg args: Any?): T {
        return this.callMethod( methodName, *args) as T
    }

    fun  Class<*>?.callStaticMethod(methodName: String, vararg args: Any?):Any? {
        return XposedHelpers.callStaticMethod(this, methodName, *args)
    }

    fun  <T>  Class<*>?.callStaticMethodAs(methodName: String, vararg args: Any?):T {
        return this.callStaticMethod(methodName, *args) as T
    }

    fun SeekBar.percentageProgress(
        progress:Int = this.progress,
        max:Int = this.max
    ) = "${(progress * 100 / max)}%"

    fun  <T : View>  View.findViewByIdNameAs(name: String): T {
        return this.findViewByIdName(name) as T
    }

    fun Class<*>?.findMethodExactIfExists(
        methodName: String,
        vararg parameterTypes: Any?
    ): Method?{
        return XposedHelpers.findMethodExactIfExists(this,methodName,*parameterTypes)
    }

    fun Class<*>?.method(
        methodName: String,
        vararg parameterTypes: Any?
    ){
        require(
            !(parameterTypes.isEmpty() || parameterTypes[parameterTypes.size - 1] !is XC_MethodHook)
        ) { "no callback defined" }

        val callback = parameterTypes[parameterTypes.size - 1] as XC_MethodHook
        val m = XposedHelpers.findMethodExact(
            this,
            methodName,
            XposedHelpers.getParameterTypes(this?.getClassLoader(), *parameterTypes)
        )


    }

    fun Class<*>?.afterHookMethod(
        methodName: String,
        vararg parameterTypes: Any?,
        methodHook: Any?.(param:MethodHookParam) -> Unit,
    ){
        XposedHelpers.findAndHookMethod(this,methodName, *parameterTypes, object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam) {
                param.thisObject.methodHook(param)
            }
        })

    }

    fun Method.replaceHookMethod(
        methodHook: Any?.(param:MethodHookParam) -> Any?,
    ){
        XposedBridge.hookMethod(this, object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam): Any? {
                return param.thisObject.methodHook(param)
            }
        })
    }


    fun Class<*>?.afterHookAllMethods(
        methodName: String,
        methodHook: Any?.(param:MethodHookParam) -> Unit,
    ){
        XposedBridge.hookAllMethods(this, methodName, object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam) {
                param.thisObject.methodHook(param)
            }
        })

    }


    fun Class<*>?.beforeHookAllMethods(
        methodName: String,
        methodHook: Any?.(param:MethodHookParam) -> Unit,
    ){
        XposedBridge.hookAllMethods(this, methodName, object :XC_MethodHook(){
            override fun beforeHookedMethod(param: MethodHookParam) {
                param.thisObject.methodHook(param)

            }

        })

    }

    fun Class<*>?.beforeHookMethod(
        methodName: String,
        vararg parameterTypes: Any?,
        methodHook: Any?.(param:MethodHookParam) -> Unit,
    ){
        XposedHelpers.findAndHookMethod(this, methodName, *parameterTypes, object :XC_MethodHook(){
            override fun beforeHookedMethod(param: MethodHookParam) {
                param.thisObject.methodHook(param)
            }
        })

    }


    fun Class<*>?.replaceHookMethod(
        methodName: String,
        vararg parameterTypes: Any?,
        methodHook: Any?.(param:MethodHookParam) -> Any?,
    ){
        XposedHelpers.findAndHookMethod(this,methodName, *parameterTypes, object :XC_MethodReplacement(){
            override fun replaceHookedMethod(param: MethodHookParam): Any? {
                return param.thisObject.methodHook(param)
            }
        })

    }


    fun Class<*>?.afterHookConstructor(
        vararg parameterTypes: Any?,
        methodHook: Any?.(param:MethodHookParam) -> Unit
    ){
        XposedHelpers.findAndHookConstructor(this, *parameterTypes, object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam) {
                param.thisObject.methodHook(param)
            }
        })

    }

    fun Class<*>?.beforeHookConstructor(
        vararg parameterTypes: Any?,
        methodHook: Any?.(param:MethodHookParam) -> Unit
    ){
        XposedHelpers.findAndHookConstructor(this, *parameterTypes, object :XC_MethodHook(){
            override fun beforeHookedMethod(param: MethodHookParam) {
                param.thisObject.methodHook(param)
            }
        })

    }

    fun Class<*>?.afterHookAllConstructors(
        methodHook: Any?.(param:MethodHookParam) -> Unit,
    ){
        XposedBridge.hookAllConstructors(this,object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam) {
                param.thisObject.methodHook(param)
            }
        })

    }

    fun Class<*>?.replaceHookedAllConstructors(
        methodHook: Any?.(param:MethodHookParam) -> Any?,
    ){
        XposedBridge.hookAllConstructors(this,object :XC_MethodReplacement(){
            override fun replaceHookedMethod(param: MethodHookParam): Any? {
                return param.thisObject.methodHook(param)
            }
        })

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
            starLog.logE("color $name is not found!")
            return Color.parseColor(defColor)
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

    fun findClass(className: String, classLoader: ClassLoader?): Class<*>? {
        val cc = XposedHelpers.findClassIfExists(className, classLoader)
        if (cc == null) {
            starLog.logE("$className is not find")
        }
        return cc
    }

    fun hookAllMethods(
        classLoader: ClassLoader?,
        className: String,
        methodName: String,
        methodHook: MethodHook
    ) {
        val hookClass = XposedHelpers.findClassIfExists(className, classLoader)
        if (hookClass == null) {
            starLog.logE("$className is not find")
            return
        }
        XposedBridge.hookAllMethods(hookClass, methodName, object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: MethodHookParam) {
                super.beforeHookedMethod(param)
                methodHook.before(param)
            }

            @Throws(Throwable::class)
            override fun afterHookedMethod(param: MethodHookParam) {
                super.afterHookedMethod(param)
                methodHook.after(param)
            }
        })
    }

    fun hookAllMethods(
        hookClass: Class<*>?,
        methodName: String,
        methodHook: MethodHook
    ) {
        if (hookClass == null) {
            starLog.logE("$methodName's class is null")
            return
        }
        XposedBridge.hookAllMethods(hookClass, methodName, object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: MethodHookParam) {
                super.beforeHookedMethod(param)
                methodHook.before(param)
            }

            @Throws(Throwable::class)
            override fun afterHookedMethod(param: MethodHookParam) {
                super.afterHookedMethod(param)
                methodHook.after(param)
            }
        })
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


    interface MethodHook {
        fun before(param: MethodHookParam)
        fun after(param: MethodHookParam)
    }

}