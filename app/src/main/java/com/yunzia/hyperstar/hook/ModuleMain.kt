package com.yunzia.hyperstar.hook

import com.yunzia.hyperstar.hook.app.NotDeveloperHook
import com.yunzia.hyperstar.hook.core.StarLog
import com.yunzia.hyperstar.hook.core.base.BaseXposedModule
import com.yunzia.hyperstar.hook.core.helper.FieldHelper
import com.yunzia.hyperstar.hook.core.helper.MethodHelper
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper
import com.yunzia.hyperstar.hook.init.InitBarrageHook
import com.yunzia.hyperstar.hook.init.InitMMSHook
import com.yunzia.hyperstar.hook.init.InitMiuiHomeHook
import com.yunzia.hyperstar.hook.init.InitMiuiScreenshot
import com.yunzia.hyperstar.hook.init.InitThemeManagerHook
import com.yunzia.hyperstar.hook.init.SystemUIHookForOS1
import com.yunzia.hyperstar.hook.init.SystemUIHookForOS2
import com.yunzia.hyperstar.hook.init.SystemUIHookForOS3
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam
import java.io.FileNotFoundException
import java.io.FileReader

class ModuleMain : BaseXposedModule() {


    override fun onPackageReady(param: PackageReadyParam) {
        super.onPackageLoaded(param)

        try {
            val text = openRemoteFile("test.txt").use {
                FileReader(it.fileDescriptor).readText()
            }
            StarLog.log("remote file content: $text")
        } catch (e: FileNotFoundException) {
            StarLog.log("remote file not found")
        }
        StarLog.log("HookChannel is currently configured for OS$currentHookChannel")


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
        StarLog.log("HyperStar Cache id ${ResourcesHelper.getCacheSize()} method ${MethodHelper.getCacheSize()} field ${FieldHelper.getCacheSize()}")



    }

}