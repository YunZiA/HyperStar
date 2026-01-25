package com.yunzia.hyperstar.utils

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.res.AssetManager
import android.content.res.Resources
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.hook.core.BaseXposedModule
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedInterface.AfterHookCallback
import io.github.libxposed.api.XposedInterface.BeforeHookCallback
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.annotations.AfterInvocation
import io.github.libxposed.api.annotations.BeforeInvocation
import kotlin.random.Random

object ModuleContext {
    lateinit var module: BaseXposedModule
        private set

    lateinit var applicationInfo: ApplicationInfo
        private set

    lateinit var resources: Resources
        private set

    /**
     * 初始化（只需调用一次）
     */
    fun initPackage(
        module: BaseXposedModule
    ) {

        this.module = module
        Application::class.java.apply {
            val exampleMethod = this.getDeclaredMethod("attach", Context::class.java)
            val onCreate = this.getDeclaredMethod("onCreate")

//            HookHelper.hook(onCreate){
//                before = {
//
//                }
//                after = {
//                    val context = try {
//                        val activityThreadClass = Class.forName("android.app.ActivityThread")
//                        val currentApplicationMethod = activityThreadClass.getDeclaredMethod("currentApplication")
//                         currentApplicationMethod.invoke(null) as? Context
//                    } catch (e: Exception) {
//                        ModuleContext.module.log("Failed to get application context $e")
//                        throw IllegalStateException("Failed to get application context", e)
//                    }
//                    if (context != null) {
//                        val mAddAddAssertPath = AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java).also { it.isAccessible = true }
//                        mAddAddAssertPath.invoke(context.resources.assets, ModuleContext.module.applicationInfo.sourceDir)
//                        val cc = resources.getString(R.string.systemui)
//                        ModuleContext.module.log("cc $cc")
//                    }
//
//                }
//            }

//            HookHelper.hook(exampleMethod){
//                before = {
//                    val key = Random.nextInt()
//                    val superContext = it.args[0] as Context
//                    val superRes = superContext.resources
//                    val assetManager = AssetManager::class.java.newInstance();
//                    val addAssetPath = AssetManager::class.java.getMethod("addAssetPath", String::class.java)
//                    addAssetPath.invoke(assetManager, ModuleContext.module.applicationInfo.sourceDir)
//
//                    resources = Resources(assetManager, superRes.displayMetrics, superRes.configuration)
//
//                    val cc = resources.getString(R.string.systemui)
//                    ModuleContext.module.log("cc $cc")
//                }
//                after = {
//
//                }
//            }
        }
    }

    fun initSystemServer(
        module: BaseXposedModule,
    ) {
        this.module = module
    }

}