package com.yunzia.hyperstar.hook.init

import android.content.Context
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.hook.app.plugin.HideVolumeCollpasedFootButton
import com.yunzia.hyperstar.hook.app.plugin.QSCardTile
import com.yunzia.hyperstar.hook.app.plugin.QSCardTileList
import com.yunzia.hyperstar.hook.app.plugin.QSControlCenterList
import com.yunzia.hyperstar.hook.app.plugin.QSEditText
import com.yunzia.hyperstar.hook.app.plugin.QSMediaDefaultApp
import com.yunzia.hyperstar.hook.app.plugin.QSMediaNoPlayTitle
import com.yunzia.hyperstar.hook.app.plugin.QSMiplayDetailVolumeBar
import com.yunzia.hyperstar.hook.app.plugin.QSToggleSliderRadius
import com.yunzia.hyperstar.hook.app.plugin.QSVolumeMute
import com.yunzia.hyperstar.hook.app.plugin.SuperBlurWidgetManager
import com.yunzia.hyperstar.hook.app.plugin.os1.DeviceCenterRow
import com.yunzia.hyperstar.hook.app.plugin.os1.PadVolume
import com.yunzia.hyperstar.hook.app.plugin.os1.QSClockAnim
import com.yunzia.hyperstar.hook.app.plugin.os1.QSControlCenterColor
import com.yunzia.hyperstar.hook.app.plugin.os1.QSEditButton
import com.yunzia.hyperstar.hook.app.plugin.os1.QSHeaderMessage
import com.yunzia.hyperstar.hook.app.plugin.os1.QSHeaderView
import com.yunzia.hyperstar.hook.app.plugin.os1.QSListView
import com.yunzia.hyperstar.hook.app.plugin.os1.QSMediaCoverBackground
import com.yunzia.hyperstar.hook.app.plugin.os1.QSMediaDeviceName
import com.yunzia.hyperstar.hook.app.plugin.os1.QSMediaView
import com.yunzia.hyperstar.hook.app.plugin.os1.QSMiplayAppIconRadius
import com.yunzia.hyperstar.hook.app.plugin.os1.SuperBlurVolumeManager
import com.yunzia.hyperstar.hook.app.plugin.os1.VolumeColumnProgressRadius
import com.yunzia.hyperstar.hook.app.plugin.os1.VolumeOrQSBrightnessValue
import com.yunzia.hyperstar.hook.app.plugin.os1.VolumeView
import com.yunzia.hyperstar.hook.app.plugin.powermenu.PowerMenuHook
import com.yunzia.hyperstar.hook.base.InitHooker
import com.yunzia.hyperstar.hook.base.MethodHook
import com.yunzia.hyperstar.hook.base.hookAllMethods
import com.yunzia.hyperstar.hook.tool.starLog
import de.robv.android.xposed.XC_MethodHook


class PluginHookForOS1() : InitHooker() {

    private val qsMediaCoverBackground: QSMediaCoverBackground = QSMediaCoverBackground()
    private val padVolume: PadVolume = PadVolume()
    private val qsControlCenterColor: QSControlCenterColor = QSControlCenterColor()
    private val powerMenuHook: PowerMenuHook = PowerMenuHook()
    private val deviceCenterRow: DeviceCenterRow = DeviceCenterRow()


    override fun initHook() {
        startSystemUIPluginHook()
    }

    override fun initResources() {
        if (resparam!!.packageName != "miui.systemui.plugin") return

        resparam!!.res.setReplacement(
            "miui.systemui.plugin",
            "drawable",
            "ic_header_settings",
            modRes!!.fwd(R.drawable.ic_header_settings)
        )
        resparam!!.res.setReplacement(
            "miui.systemui.plugin",
            "drawable",
            "ic_controls_edit",
            modRes!!.fwd(R.drawable.ic_controls_edit)
        )

        initResource(powerMenuHook)
        initResource(qsMediaCoverBackground)
        initResource(padVolume)
        initResource(qsControlCenterColor)
        initResource(QSMiplayAppIconRadius())
        initResource(deviceCenterRow)
        initResource(QSMediaNoPlayTitle())
        initResource(QSEditText())


    }


    lateinit var mContext: Context;
    var isHooked : Boolean = false;

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
        hookAllMethods(
            classLoader,
            "com.android.systemui.shared.plugins.PluginInstance\$Factory$\$ExternalSyntheticLambda0",
            "get",
            object : MethodHook {
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
                    initSecHook(pathClassLoader)
                    isHooked = true
                }else if (secClassLoader != pathClassLoader){
                    starLog.log("pluginClassLoader is changed")
                    isHooked = false

                }

            }
        }
        )

    }


    override fun initSecHook(classLoader: ClassLoader?) {
        super.initSecHook(classLoader)


        initSecHooker(QSClockAnim())
        initSecHooker(SuperBlurWidgetManager())
        initSecHooker(SuperBlurVolumeManager())
        initSecHooker(qsMediaCoverBackground)
        initSecHooker(QSMediaDeviceName())
        initSecHooker(QSMediaDefaultApp())
        initSecHooker(QSMediaView())
        initSecHooker(qsControlCenterColor)
        initSecHooker(QSListView())
        initSecHooker(VolumeOrQSBrightnessValue())
        initSecHooker(QSCardTileList())
        initSecHooker(QSCardTile())
        initSecHooker(QSVolumeMute())
        initSecHooker(QSToggleSliderRadius())
        initSecHooker(QSHeaderMessage())
        initSecHooker(QSHeaderView())
        initSecHooker(QSEditButton())
        initSecHooker(padVolume)
        initSecHooker(QSClockAnim())
        initSecHooker(QSControlCenterList())
        initSecHooker(VolumeColumnProgressRadius())
        initSecHooker(powerMenuHook)
        initSecHooker(VolumeView())
        initSecHooker(HideVolumeCollpasedFootButton())
        initSecHooker(deviceCenterRow)
        initSecHooker(QSMiplayDetailVolumeBar())
    }


}