package com.yunzia.hyperstar

import android.annotation.SuppressLint
import com.yunzia.hyperstar.prefs.XSPUtils
import com.yunzia.hyperstar.prefs.loadPref
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedInterface.AfterHookCallback
import io.github.libxposed.api.XposedInterface.BeforeHookCallback
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam
import io.github.libxposed.api.annotations.AfterInvocation
import io.github.libxposed.api.annotations.BeforeInvocation
import io.github.libxposed.api.annotations.XposedHooker
import kotlin.random.Random

private lateinit var module: InitHook

class InitHook(base: XposedInterface, param: ModuleLoadedParam) : XposedModule(base, param) {

    init {
        log("ModuleMain at " + param.processName)
        module = this
    }

    @XposedHooker
    class MyHooker(private val magic: Int) : XposedInterface.Hooker {
        companion object {
            @JvmStatic
            @BeforeInvocation
            fun beforeInvocation(callback: BeforeHookCallback): MyHooker {
                val key = Random.nextInt()
                val appContext = callback.args[0]
                module.log("beforeInvocation: key = $key")
                module.log("app context: $appContext")
                return MyHooker(key)
            }

            @JvmStatic
            @AfterInvocation
            fun afterInvocation(callback: AfterHookCallback, context: MyHooker) {
                module.log("afterInvocation: key = ${context.magic}")
            }
        }
    }

    var c = 0

    @SuppressLint("DiscouragedPrivateApi")
    override fun onPackageLoaded(param: PackageLoadedParam) {
        super.onPackageLoaded(param)
        log("onPackageLoaded: " + param.packageName)
        log("param classloader is " + param.classLoader)
        log("module apk path: " + this.applicationInfo.sourceDir)
        log("----------")

        if (!param.isFirstPackage) return

        loadPref()

        //prefs.unregisterOnSharedPreferenceChangeListener {  }

//        try {
//            val text = openRemoteFile("test.txt").use {
//                FileReader(it.fileDescriptor).readText()
//            }
//            log("remote file content: $text")
//        } catch (e: FileNotFoundException) {
//            log("remote file not found")
//        }
//        this.getRemotePreferences("")
//        val exampleMethod = Application::class.java.getDeclaredMethod("attach", Context::class.java)
//        hook(exampleMethod, MyHooker::class.java)
    }
}