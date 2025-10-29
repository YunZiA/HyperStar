//package com.yunzia.hyperstar.hook.help
//
//import android.content.res.XModuleResources
//import com.yunzia.hyperstar.hook.base.Hooker
//import com.yunzia.hyperstar.hook.base.HookerHelper
//import com.yunzia.hyperstar.hook.base.Init
////import com.yunzia.annotations.Init
//import com.yunzia.hyperstar.hook.tool.starLog
//import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
//import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
//import io.github.libxposed.api.XposedModule
//import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam
//
//abstract class InitHooker(): HookerHelper() {
//
//    val className = lazy { this.javaClass.simpleName }
//    var plugin: String = "miui.systemui.plugin"
//    var resparam: InitPackageResourcesParam? = null
//    var lpparam: LoadPackageParam? = null
//    var modRes: XModuleResources? = null
//    var classLoader: ClassLoader? = null
//    var secClassLoader: ClassLoader? = null
//    val init: Init? = this::class.java.getAnnotation(Init::class.java)
//
//    val mPackageName by lazy { init!!.packageName }
//
//    open fun XposedModule.onPackageLoaded() {}
//
//    open fun XposedModule.onPackageLoaded(param: PackageLoadedParam) {
//
//        this.lpparam = lpparam
//
//        if (lpparam.packageName == init!!.packageName) {
//            starLog.log(className.value,"Loaded app: " + lpparam.packageName)
//            this.classLoader = lpparam.classLoader
//            initHook()
//        }
//    }
//
//    fun initHook(classLoader: ClassLoader?) {
//        this.classLoader = classLoader
//        initHook()
//    }
//
//
//    fun Hooker.initHooker() {
//        try {
//            mPackageName = this@InitHooker.mPackageName
//            initHook(this@InitHooker.classLoader)
//        } catch (e: Exception) {
//            starLog.logE(className.value, e.message)
//        }
//    }
//
//    fun InitHooker.initHooker() {
//        try {
//            initHook(this@InitHooker.classLoader)
//        } catch (e: Exception) {
//            starLog.logE(className.value,e.message)
//        }
//    }
//
//    open fun initSecHook(secClassLoader: ClassLoader?) {
//        this.secClassLoader = secClassLoader
//    }
//
//    fun initSecHooker(hooker: Hooker) {
//        try {
//            hooker.apply {
//                initHook(secClassLoader)
//                mPackageName = this@InitHooker.mPackageName
//            }
//        } catch (e: Exception) {
//            starLog.log(e.message)
//        }
//    }
//
//    fun logD(msg: String){
//        starLog.logD(className.value, mPackageName, msg)
//
//    }
//}