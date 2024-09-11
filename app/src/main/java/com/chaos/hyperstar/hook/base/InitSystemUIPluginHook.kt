package com.chaos.hyperstar.hook.base

import android.content.Context
import android.content.res.Resources
import com.chaos.hyperstar.hook.app.plugin.QsCardTileList
import com.chaos.hyperstar.hook.app.plugin.QsHeaderView
import com.chaos.hyperstar.hook.app.plugin.QsListView
import com.chaos.hyperstar.hook.app.plugin.QsMediaCoverBackground
import com.chaos.hyperstar.hook.app.plugin.QsMediaDefaultApp
import com.chaos.hyperstar.hook.app.plugin.QsMediaDeviceName
import com.chaos.hyperstar.hook.app.plugin.QsMediaView
import com.chaos.hyperstar.hook.app.plugin.QsVolumeOrBrightnessValue
import com.chaos.hyperstar.hook.app.plugin.SuperBlurVolumeManager
import com.chaos.hyperstar.hook.app.plugin.SuperBlurWidgetManager
import com.chaos.hyperstar.hook.tool.starLog
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_InitPackageResources


class InitSystemUIPluginHook() : BaseHooker() {

    private val qsMediaCoverBackground: QsMediaCoverBackground

    init {
        qsMediaCoverBackground = QsMediaCoverBackground()
    }

    override fun getLocalRes(res: Resources?) {
        super.getLocalRes(res)
        if (res != null){
            qsMediaCoverBackground.getLocalRes(res)

        }
    }

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        startSystemUIPluginHook(classLoader)
    }

    override fun doRes(resparam: XC_InitPackageResources.InitPackageResourcesParam?) {
        super.doRes(resparam)

    }

    lateinit var mContext: Context;
    var ishHooked : Boolean = false;

    private fun startSystemUIPluginHook(classLoader: ClassLoader?){

        hookAllMethods(classLoader, "com.android.systemui.shared.plugins.PluginInstance\$Factory", "create",object : MethodHook {
                override fun before(param: XC_MethodHook.MethodHookParam) {
                    if (param.args.isNotEmpty() && param.args[0] is Context) {
                        mContext = param.args[0] as Context
                    }
                }

                override fun after(param: XC_MethodHook.MethodHookParam) {

                }
            }
        )
        hookAllMethods(classLoader,
            "com.android.systemui.shared.plugins.PluginInstance\$Factory$\$ExternalSyntheticLambda0",
            "get",object : MethodHook {
            override fun before(param: XC_MethodHook.MethodHookParam) {

            }

            override fun after(param: XC_MethodHook.MethodHookParam) {
                val pathClassLoader = param.getResult() as? ClassLoader // 尝试将结果安全地转换为ClassLoader
                if (!ishHooked && pathClassLoader != null) {
                    starLog.log("Loaded pluginClassLoader: $pathClassLoader") // 直接使用pathClassLoader
                    doHook(pathClassLoader) // 直接传递pathClassLoader给doHook函数
                    ishHooked = true
                } else if (pathClassLoader == null) {
                    // 如果需要，处理pathClassLoader为null的情况
                    starLog.log("Failed to load pluginClassLoader: null returned")
                }
            }
        }
        )

    }

    private fun doHook(classLoader: ClassLoader) {
        SuperBlurWidgetManager().doMethods(classLoader)
        SuperBlurVolumeManager().doMethods(classLoader)
        qsMediaCoverBackground.doMethods(classLoader)
        QsMediaDeviceName().doMethods(classLoader)
        QsMediaDefaultApp().doMethods(classLoader)
        QsMediaView().doMethods(classLoader)
        QsListView().doMethods(classLoader)
        QsVolumeOrBrightnessValue().doMethods(classLoader)
        QsCardTileList().doMethods(classLoader)
        //QsHeaderView().doMethods(classLoader)
    }

}