package com.yunzia.hyperstar.hook.base

import android.content.Context
import android.content.res.Resources
import android.content.res.XModuleResources
import com.yunzia.hyperstar.hook.app.plugin.powermenu.PowerMenu
import com.yunzia.hyperstar.hook.app.plugin.QSCardTile
import com.yunzia.hyperstar.hook.app.plugin.QSCardTileList
import com.yunzia.hyperstar.hook.app.plugin.QSClockAnim
import com.yunzia.hyperstar.hook.app.plugin.QSControlCenterColor
import com.yunzia.hyperstar.hook.app.plugin.QSControlCenterList
import com.yunzia.hyperstar.hook.app.plugin.QSEditButton
import com.yunzia.hyperstar.hook.app.plugin.QSHeaderMessage
import com.yunzia.hyperstar.hook.app.plugin.QSHeaderView
import com.yunzia.hyperstar.hook.app.plugin.QSListView
import com.yunzia.hyperstar.hook.app.plugin.QSMediaCoverBackground
import com.yunzia.hyperstar.hook.app.plugin.QSMediaDefaultApp
import com.yunzia.hyperstar.hook.app.plugin.QSMediaDeviceName
import com.yunzia.hyperstar.hook.app.plugin.QSMediaView
import com.yunzia.hyperstar.hook.app.plugin.QSMiplayAppIconRadius
import com.yunzia.hyperstar.hook.app.plugin.QSToggleSliderRadius
import com.yunzia.hyperstar.hook.app.plugin.QSVolumeOrBrightnessValue
import com.yunzia.hyperstar.hook.app.plugin.SuperBlurVolumeManager
import com.yunzia.hyperstar.hook.app.plugin.SuperBlurWidgetManager
import com.yunzia.hyperstar.hook.app.plugin.VolumeColumnProgressRadius
import com.yunzia.hyperstar.hook.app.plugin.VolumeView
import com.yunzia.hyperstar.hook.tool.starLog
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_InitPackageResources


class InitSystemUIPluginHook() : BaseHooker() {

    private val qsMediaCoverBackground: QSMediaCoverBackground
    private val qsControlCenterColor: QSControlCenterColor
    private val powerMenu: PowerMenu

    init {
        qsMediaCoverBackground = QSMediaCoverBackground()
        qsControlCenterColor = QSControlCenterColor()
        powerMenu = PowerMenu()

    }

    override fun getLocalRes(res: Resources?) {
        super.getLocalRes(res)
        if (res != null){
            qsMediaCoverBackground.getLocalRes(res)

        }
    }

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        startSystemUIPluginHook()
    }


    override fun doResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
        modRes: XModuleResources?
    ) {
        super.doResources(resparam, modRes)
        doResources(qsControlCenterColor)
        doResources(powerMenu)
        doResources(QSMiplayAppIconRadius())



    }

    override fun doRes(resparam: XC_InitPackageResources.InitPackageResourcesParam?) {
        super.doRes(resparam)

    }

    lateinit var mContext: Context;
    var ishHooked : Boolean = false;

    private fun startSystemUIPluginHook(){

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


    override fun doHook(classLoader: ClassLoader?) {
        super.doHook(classLoader)
        doSecMethods(QSClockAnim())
        doSecMethods(SuperBlurWidgetManager())
        doSecMethods(SuperBlurVolumeManager())
        doSecMethods(qsMediaCoverBackground)
        doSecMethods(QSMediaDeviceName())
        doSecMethods(QSMediaDefaultApp())
        doSecMethods(QSMediaView())
        doSecMethods(qsControlCenterColor)
        doSecMethods(QSListView())
        doSecMethods(QSVolumeOrBrightnessValue())
        doSecMethods(QSCardTileList())
        doSecMethods(QSCardTile())
        doSecMethods(QSToggleSliderRadius())
        doSecMethods(QSHeaderMessage())
        doSecMethods(QSHeaderView())
        doSecMethods(QSEditButton())
        doSecMethods(QSClockAnim())
        doSecMethods(QSControlCenterList())
        doSecMethods(VolumeColumnProgressRadius())
        doSecMethods(powerMenu)
        doSecMethods(VolumeView())
    }


}