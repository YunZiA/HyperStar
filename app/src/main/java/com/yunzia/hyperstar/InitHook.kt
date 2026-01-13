package com.yunzia.hyperstar

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.loader.ResourcesLoader
import android.content.res.loader.ResourcesProvider
import android.os.ParcelFileDescriptor
import android.widget.TextView
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
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import kotlin.random.Random


private lateinit var module: InitHook
private lateinit var modulePath: String
private lateinit var moduleResources: Resources

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
            fun before(callback: BeforeHookCallback): MyHooker {
                val key = Random.nextInt()
                val thisObj = callback.thisObject as Application

                val superContext = callback.args[0] as Context
                val superRes = superContext.resources
                val assetManager = AssetManager::class.java.newInstance();
                val addAssetPath = AssetManager::class.java.getMethod("addAssetPath", String::class.java)
                addAssetPath.invoke(assetManager, module.applicationInfo.sourceDir)

                moduleResources = Resources(assetManager, superRes.displayMetrics, superRes.configuration)
                module.log("app context: $superContext")
                module.log("Application: ${callback.thisObject!!::class.java.name}")
                superContext.resources.assets.apply {
                    this::class.java.getMethod("addAssetPath", String::class.java).invoke(
                        this,module.applicationInfo.sourceDir
                    )
                }

                val cc = superRes.getString(R.string.systemui)
                module.log("cc ${cc}")
                callback.args[0] = superContext
                return MyHooker(key)
            }

            @JvmStatic
            @AfterInvocation
            fun after(callback: AfterHookCallback, context: MyHooker) {
                module.log("afterInvocation: key = ${context.magic}")
            }
        }
    }

    @XposedHooker
    class StringHooker() : XposedInterface.Hooker {
        companion object {
            @JvmStatic
            @BeforeInvocation
            fun before(callback: BeforeHookCallback): MyHooker {
                val key = Random.nextInt()
                val nowId = callback.args[0] as Int
                val resources = callback.thisObject as Resources
                module.log("resourcesr: $resources")
                try {
                    // Step 1: 获取 mResourcesImpl

                    val nowName = resources.getResourceEntryName(nowId)
                    module.log("nowName: $nowName")
                    if (nowName == "qs_customize_entry_button_text"){

                        //resources.getIdentifier("qs_customize_entry_button_text", "string", "miui.systemui.plugin")

                        val rpText = moduleResources.getText(R.string.systemui)

                        module.log("Resource text from plugin's AssetManager: $rpText")

                        // 即使 rpText 为 null，也跳过（可能导致显示空白）
                        callback.returnAndSkip(rpText)
                    }

                } catch (e: Throwable) {
                    module.log("Error during resource injection", e)
                }

                return MyHooker(key)
            }

            @JvmStatic
            @AfterInvocation
            fun after(callback: AfterHookCallback) {

            }
        }
    }

    @SuppressLint("DiscouragedPrivateApi")
    override fun onPackageLoaded(param: PackageLoadedParam) {
        super.onPackageLoaded(param)
        log("onPackageLoaded: " + param.packageName)
        log("param classloader is " + param.classLoader)
        log("module apk path: " + this.applicationInfo.sourceDir)
        log("----------")
        if (!param.isFirstPackage) return
        loadPref()

        try {
            val text = openRemoteFile("test.txt").use {
                FileReader(it.fileDescriptor).readText()
            }
            log("remote file content: $text")
        } catch (e: FileNotFoundException) {
            log("remote file not found")
        }
        Application::class.java.apply {
            val exampleMethod = this.getDeclaredMethod("attach", Context::class.java)
            hook(exampleMethod, MyHooker::class.java)
        }

        if (param.packageName != "com.android.systemui") {

            val setText = TextView::class.java.getDeclaredMethod("setText", CharSequence::class.java)
            setText.isAccessible = true
            module.hook(setText,textHooker::class.java)
            return
        }


        val pluginInstancePluginFactory = Class.forName(
            "com.android.systemui.shared.plugins.PluginInstance\$PluginFactory",
            false,
            param.classLoader
        )

        pluginInstancePluginFactory.getDeclaredMethod("createPluginContext").apply {
            this.isAccessible = true
            module.hook(this,pgHooker::class.java)
        }

    }

    @XposedHooker
    class pgHooker() : XposedInterface.Hooker {
        companion object {
            var isHooked = false
            var secClassLoader: ClassLoader? = null
            @JvmStatic
            @BeforeInvocation
            fun before(callback: BeforeHookCallback): MyHooker {
                val key = Random.nextInt()
                return MyHooker(key)
            }

            @JvmStatic
            @AfterInvocation
            fun after(callback: AfterHookCallback) {
                val mPluginContext = callback.result as ContextWrapper
                val pathClassLoader = mPluginContext.classLoader
                if (mPluginContext.packageName != "miui.systemui.plugin"){
                    module.log("检测到非目标应用包名: 当前包名为 " + mPluginContext.packageName + ", 目标插件包名为 " + "miui.systemui.plugin")
                    return
                }

                if (pathClassLoader == null) {
                    module.log("Failed to load pluginClassLoader: null returned")
                    return
                }
                if (!isHooked) {
                    module.log("Loaded pluginClassLoader: $pathClassLoader")
                    secClassLoader = pathClassLoader

                    val getText3  = Resources::class.java.getDeclaredMethod("getText",Int::class.java)
                    module.hook(getText3, StringHooker::class.java)
//
//                    module.hook(getText, StringHooker::class.java)
//                    module.hook(getText1, StringHooker::class.java)
//                    module.hook(getString, StringHooker::class.java)
                    val id = R.string.systemui
                    val cc = moduleResources.getString(id)
                    val VV = moduleResources.getText(id)
                    val VV1 = moduleResources.getText(id,"")
                    module.log("qs_customize_entry_button_text: $cc $VV $VV1")


                    val TextViewS = Class.forName(
                        "android.widget.TextView",
                        false,
                        secClassLoader
                    )

                    val setText = TextView::class.java.getDeclaredMethod("setText", CharSequence::class.java)
                    setText.isAccessible = true
                    module.hook(setText,textHooker::class.java)
//                    val setTextC = TextView.getDeclaredMethod("setText", CharSequence::class.java,BufferType::class.java)
//                    setText.isAccessible = true
//                    module.hook(setTextC,textHooker::class.java)
                    isHooked = true
                }else if (secClassLoader != pathClassLoader){
                    module.log("pluginClassLoader is changed")
                    isHooked = false
                }
                val resources = Class.forName(
                    "android.content.res.Resources",
                    false,
                    secClassLoader
                )

                val getText = resources.getDeclaredMethod(
                    "getText",
                    Int::class.java
                ).apply {
                    isAccessible = true
                }

                val getText1 = resources.getDeclaredMethod(
                    "getText",
                    Int::class.java,
                    CharSequence::class.java
                ).apply {
                    isAccessible = true
                }
                val getString = resources.getDeclaredMethod(
                    "getString",
                    Int::class.java
                ).apply {
                    isAccessible = true
                }

                callback.result = mPluginContext
            }
        }
    }
    @XposedHooker
    class textHooker() : XposedInterface.Hooker {
        companion object {

            @JvmStatic
            @BeforeInvocation
            fun before(callback: BeforeHookCallback): MyHooker {
                val key = Random.nextInt()
                callback.args[0] = moduleResources.getText(R.string.xposed_desc)
                return MyHooker(key)
            }

            @JvmStatic
            @AfterInvocation
            fun after(callback: AfterHookCallback) {
                val ts = callback.thisObject as TextView
                module.log("TextVie wbaseContext.resources: ${(ts.context.resources)::class.java.name}")
                module.log("TextVie setText is ${callback.args[0]}")
            }
        }
    }
}