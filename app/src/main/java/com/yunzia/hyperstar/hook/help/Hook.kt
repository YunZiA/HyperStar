//package com.yunzia.hyperstar.hook.help
//
//import com.yunzia.hyperstar.init.MyBaseHook
//
//
//hufun
//
//fun Class<*>?.afterHookMethod(
//    methodName: String,
//    vararg parameterTypes: Any?,
//    methodHook: Any?.(param:MethodHookParam) -> Unit,
//){
//    XposedHelpers.findAndHookMethod(this,methodName, *parameterTypes, object :XC_MethodHook(){
//        override fun afterHookedMethod(param: MethodHookParam) {
//            param.thisObject.methodHook(param)
//        }
//    })
//
//}
//
//fun Method?.replace(
//    methodHook: Any?.(param:MethodHookParam) -> Any?,
//){
//
//    hook(exampleMethod, MyBaseHook::class.java)
//    this?: return
//    XposedBridge.hookMethod(this, object : XC_MethodReplacement() {
//        override fun replaceHookedMethod(param: MethodHookParam): Any? {
//            return param.thisObject.methodHook(param)
//        }
//    })
//}
//
//fun Class<*>.allMethod(
//    methodName: String,
//): MutableSet<Method> {
//    val unhooks: MutableSet<Method> = HashSet()
//    for (method in thisObject.getDeclaredMethods()){
//        if (method.name == methodName) {
//            unhooks.add(method)
//        }
//    }
//    return unhooks
//}
//
//fun MutableSet<Method>.after(
//    methodHook: Any?.(param:MethodHookParam) -> Unit
//){
//    val unhooks: MutableSet<XC_MethodHook.Unhook> = HashSet()
//    for (method in this){
//        unhooks.add(
//            XposedBridge.hookMethod(method, object :XC_MethodHook(){
//                override fun afterHookedMethod(param: MethodHookParam) {
//                    param.thisObject.methodHook(param)
//                }
//            })
//        )
//    }
//}
//
//
//fun Class<*>?.afterHookAllMethods(
//    methodName: String,
//    methodHook: Any?.(param:MethodHookParam) -> Unit,
//){
//    XposedBridge.hookAllMethods(this, methodName, object :XC_MethodHook(){
//        override fun afterHookedMethod(param: MethodHookParam) {
//            param.thisObject.methodHook(param)
//        }
//    })
//
//}
//
//
//fun Class<*>?.beforeHookAllMethods(
//    methodName: String,
//    methodHook: Any?.(param:MethodHookParam) -> Unit,
//){
//    XposedBridge.hookAllMethods(this, methodName, object :XC_MethodHook(){
//        override fun beforeHookedMethod(param: MethodHookParam) {
//            param.thisObject.methodHook(param)
//
//        }
//
//    })
//
//}
//
//fun Class<*>?.beforeHookMethod(
//    methodName: String,
//    vararg parameterTypes: Any?,
//    methodHook: Any?.(param:MethodHookParam) -> Unit,
//){
//    try {
//        XposedHelpers.findAndHookMethod(this, methodName, *parameterTypes, object :XC_MethodHook(){
//            override fun beforeHookedMethod(param: MethodHookParam) {
//                param.thisObject.methodHook(param)
//            }
//        })
//
//    }catch (e: NoSuchMethodError) {
//        logE("$e")
//    }catch (e: Exception){
//        logE("$e")
//    }
//}
//
//
