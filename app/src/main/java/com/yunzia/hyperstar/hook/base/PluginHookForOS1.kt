package com.yunzia.hyperstar.hook.base

import android.content.Context
import android.content.res.XModuleResources
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.hook.app.plugin.os1.DeviceCenterRow
import com.yunzia.hyperstar.hook.app.plugin.os1.PadVolume
import com.yunzia.hyperstar.hook.app.plugin.QSCardTile
import com.yunzia.hyperstar.hook.app.plugin.QSCardTileList
import com.yunzia.hyperstar.hook.app.plugin.QSEditText
import com.yunzia.hyperstar.hook.app.plugin.os1.QSClockAnim
import com.yunzia.hyperstar.hook.app.plugin.os1.QSControlCenterColor
import com.yunzia.hyperstar.hook.app.plugin.os1.QSControlCenterList
import com.yunzia.hyperstar.hook.app.plugin.os1.QSEditButton
import com.yunzia.hyperstar.hook.app.plugin.os1.QSHeaderMessage
import com.yunzia.hyperstar.hook.app.plugin.os1.QSHeaderView
import com.yunzia.hyperstar.hook.app.plugin.os1.QSListView
import com.yunzia.hyperstar.hook.app.plugin.os1.QSMediaCoverBackground
import com.yunzia.hyperstar.hook.app.plugin.QSMediaDefaultApp
import com.yunzia.hyperstar.hook.app.plugin.QSMediaNoPlayTitle
import com.yunzia.hyperstar.hook.app.plugin.QSMiplayDetailVolumeBar
import com.yunzia.hyperstar.hook.app.plugin.os1.QSMediaDeviceName
import com.yunzia.hyperstar.hook.app.plugin.os1.QSMediaView
import com.yunzia.hyperstar.hook.app.plugin.os1.QSMiplayAppIconRadius
import com.yunzia.hyperstar.hook.app.plugin.SuperBlurVolumeManager
import com.yunzia.hyperstar.hook.app.plugin.QSToggleSliderRadius
import com.yunzia.hyperstar.hook.app.plugin.os1.QSVolumeOrBrightnessValue
import com.yunzia.hyperstar.hook.app.plugin.os1.VolumeColumnProgressRadius
import com.yunzia.hyperstar.hook.app.plugin.os1.VolumeView
import com.yunzia.hyperstar.hook.app.plugin.SuperBlurWidgetManager
import com.yunzia.hyperstar.hook.app.plugin.powermenu.PowerMenu
import com.yunzia.hyperstar.hook.tool.starLog
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_InitPackageResources


class PluginHookForOS1() : BaseHooker() {

    private val qsMediaCoverBackground: QSMediaCoverBackground = QSMediaCoverBackground()
    private val padVolume: PadVolume = PadVolume()
    private val qsControlCenterColor: QSControlCenterColor = QSControlCenterColor()
    private val powerMenu: PowerMenu = PowerMenu()
    private val deviceCenterRow: DeviceCenterRow = DeviceCenterRow()


    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        startSystemUIPluginHook()
    }


    override fun doResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
        modRes: XModuleResources?
    ) {
        super.doResources(resparam, modRes)
        if (resparam!!.packageName != "miui.systemui.plugin") return

        resparam.res.setReplacement(
            "miui.systemui.plugin",
            "drawable",
            "ic_header_settings",
            modRes!!.fwd(R.drawable.ic_header_settings)
        )
        resparam.res.setReplacement(
            "miui.systemui.plugin",
            "drawable",
            "ic_controls_edit",
            modRes.fwd(R.drawable.ic_controls_edit)
        )

        doResources(powerMenu)
        doResources(qsMediaCoverBackground)
        doResources(padVolume)
        doResources(qsControlCenterColor)
        doResources(QSMiplayAppIconRadius())
        doResources(deviceCenterRow)
        doResources(QSMediaNoPlayTitle())
        doResources(QSEditText())


    }


    lateinit var mContext: Context;
    var isHooked : Boolean = false;

    private fun startSystemUIPluginHook(){

        hookAllMethods(classLoader, "com.android.systemui.shared.plugins.PluginInstance\$Factory", "create",object :
            MethodHook {
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

                if (pathClassLoader == null) {
                    starLog.log("Failed to load pluginClassLoader: null returned")
                    return
                }
                if (!isHooked) {
                    starLog.log("Loaded pluginClassLoader: $pathClassLoader")
                    doHook(pathClassLoader)
                    isHooked = true
                }else if (secClassLoader != pathClassLoader){
                    starLog.log("pluginClassLoader is changed")
                    isHooked = false

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
        doSecMethods(padVolume)
        doSecMethods(QSClockAnim())
        doSecMethods(QSControlCenterList())
        doSecMethods(VolumeColumnProgressRadius())
        doSecMethods(powerMenu)
        doSecMethods(VolumeView())
        doSecMethods(deviceCenterRow)
        doSecMethods(QSMiplayDetailVolumeBar())
    }


}