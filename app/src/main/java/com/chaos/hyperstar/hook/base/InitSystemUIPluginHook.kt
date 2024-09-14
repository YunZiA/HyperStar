package com.chaos.hyperstar.hook.base

import android.content.Context
import android.content.res.Resources
import com.chaos.hyperstar.hook.app.plugin.QSCardTile
import com.chaos.hyperstar.hook.app.plugin.QSCardTileList
import com.chaos.hyperstar.hook.app.plugin.QSHeaderMessage
import com.chaos.hyperstar.hook.app.plugin.QSHeaderView
import com.chaos.hyperstar.hook.app.plugin.QSListView
import com.chaos.hyperstar.hook.app.plugin.QSMediaCoverBackground
import com.chaos.hyperstar.hook.app.plugin.QSMediaDefaultApp
import com.chaos.hyperstar.hook.app.plugin.QSMediaDeviceName
import com.chaos.hyperstar.hook.app.plugin.QSMediaView
import com.chaos.hyperstar.hook.app.plugin.QSToggleSliderRadius
import com.chaos.hyperstar.hook.app.plugin.QSVolumeOrBrightnessValue
import com.chaos.hyperstar.hook.app.plugin.SuperBlurVolumeManager
import com.chaos.hyperstar.hook.app.plugin.SuperBlurWidgetManager
import com.chaos.hyperstar.hook.tool.starLog
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_InitPackageResources


class InitSystemUIPluginHook() : BaseHooker() {

    private val qsMediaCoverBackground: QSMediaCoverBackground

    init {
        qsMediaCoverBackground = QSMediaCoverBackground()
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
        QSMediaDeviceName().doMethods(classLoader)
        QSMediaDefaultApp().doMethods(classLoader)
        QSMediaView().doMethods(classLoader)
        QSListView().doMethods(classLoader)
        QSVolumeOrBrightnessValue().doMethods(classLoader)
        QSCardTileList().doMethods(classLoader)
        QSCardTile().doMethods(classLoader)
        QSToggleSliderRadius().doMethods(classLoader)
        QSHeaderMessage().doMethods(classLoader)
        QSHeaderView().doMethods(classLoader)
    }

}