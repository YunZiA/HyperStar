package com.yunzia.hyperstar.hook

import android.app.Application
import android.content.Context
import com.yunzia.hyperstar.hook.app.NotDeveloperHook
import com.yunzia.hyperstar.hook.core.BaseXposedModule
import com.yunzia.hyperstar.hook.core.Log.logE
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.hook.core.helper.hookAllMethods
import com.yunzia.hyperstar.hook.init.InitBarrageHook
import com.yunzia.hyperstar.hook.init.InitMMSHook
import com.yunzia.hyperstar.hook.init.InitMiuiHomeHook
import com.yunzia.hyperstar.hook.init.InitMiuiScreenshot
import com.yunzia.hyperstar.hook.init.InitThemeManagerHook
import com.yunzia.hyperstar.hook.init.SystemUIHookForOS1
import com.yunzia.hyperstar.hook.init.SystemUIHookForOS2
import com.yunzia.hyperstar.hook.init.SystemUIHookForOS3
import com.yunzia.hyperstar.utils.getHookChannel
import io.github.kyuubiran.ezxhelper.xposed.EzXposed.initAppContext
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam
import java.io.FileNotFoundException
import java.io.FileReader

class ModuleMain(base: XposedInterface, param: ModuleLoadedParam) : BaseXposedModule(base, param) {


    override fun onPackageLoaded(param: PackageLoadedParam) {
        super.onPackageLoaded(param)

        try {
            val text = openRemoteFile("test.txt").use {
                FileReader(it.fileDescriptor).readText()
            }
            log("remote file content: $text")
        } catch (e: FileNotFoundException) {
            log("remote file not found")
        }
        log("HookChannel is currently configured for OS$currentHookChannel")


        initHooks(
            NotDeveloperHook,
            SystemUIHookForOS1,
            SystemUIHookForOS2,
            SystemUIHookForOS3,
            InitMiuiHomeHook,
            InitMiuiScreenshot,
            InitMMSHook,
            InitBarrageHook,
            InitThemeManagerHook
        )


    }

}