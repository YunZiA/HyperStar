package com.yunzia.hyperstar.hook.init

import android.content.ContextWrapper
import com.yunzia.hyperstar.hook.app.plugin.QSCardTile
import com.yunzia.hyperstar.hook.app.plugin.QSCardTileList
import com.yunzia.hyperstar.hook.app.plugin.QSEditText
import com.yunzia.hyperstar.hook.app.plugin.QSMediaDefaultApp
import com.yunzia.hyperstar.hook.app.plugin.QSMediaNoPlayTitle
import com.yunzia.hyperstar.hook.app.plugin.QSMiplayDetailVolumeBar
import com.yunzia.hyperstar.hook.app.plugin.QSToggleSliderRadius
import com.yunzia.hyperstar.hook.app.plugin.SuperBlurVolumeManager
import com.yunzia.hyperstar.hook.app.plugin.SuperBlurWidgetManager
import com.yunzia.hyperstar.hook.app.plugin.QSControlCenterList
import com.yunzia.hyperstar.hook.app.plugin.os2.DeviceCenterRow
import com.yunzia.hyperstar.hook.app.plugin.os2.QSControlCenterColor
import com.yunzia.hyperstar.hook.app.plugin.os2.QSEditButton
import com.yunzia.hyperstar.hook.app.plugin.os2.QSHeaderMessage
import com.yunzia.hyperstar.hook.app.plugin.os2.QSHeaderViewListener
import com.yunzia.hyperstar.hook.app.plugin.os2.QSListView
import com.yunzia.hyperstar.hook.app.plugin.os2.QSMediaCoverBackground
import com.yunzia.hyperstar.hook.app.plugin.os2.QSMediaDeviceName
import com.yunzia.hyperstar.hook.app.plugin.os2.QSMediaView
import com.yunzia.hyperstar.hook.app.plugin.os2.QSMiplayAppIconRadius
import com.yunzia.hyperstar.hook.app.plugin.os2.VolumeOrQSBrightnessValue
import com.yunzia.hyperstar.hook.app.plugin.os2.VolumeColumnProgressRadius
import com.yunzia.hyperstar.hook.app.plugin.os2.VolumeView
import com.yunzia.hyperstar.hook.app.plugin.powermenu.PowerMenu
import com.yunzia.hyperstar.hook.base.InitHooker
import com.yunzia.hyperstar.hook.util.starLog


class PluginHookForOS2 : InitHooker() {

    private val qsMediaCoverBackground = QSMediaCoverBackground()
    private val qsControlCenterColor = QSControlCenterColor()
    private val powerMenu = PowerMenu()
    private val deviceCenterRow = DeviceCenterRow()

    override fun initHook() {
        startSystemUIPluginHook()
    }


    override fun initResources() {

        if (resparam!!.packageName != plugin) return

        initResource(qsControlCenterColor)
        initResource(powerMenu)
        initResource(QSMiplayAppIconRadius())
        initResource(qsMediaCoverBackground)
        initResource(deviceCenterRow)
        initResource(QSMediaNoPlayTitle())
        initResource(QSEditText())
        initResource(VolumeOrQSBrightnessValue())



    }


    var isHooked : Boolean = false;

    private fun startSystemUIPluginHook(){

        val pluginInstancePluginFactory = findClass("com.android.systemui.shared.plugins.PluginInstance\$PluginFactory",classLoader)

        pluginInstancePluginFactory.afterHookMethod("createPluginContext"){
            val mPluginContext = it.result as ContextWrapper
            val pathClassLoader = mPluginContext.classLoader

            if (pathClassLoader == null) {
                starLog.log("Failed to load pluginClassLoader: null returned")
                return@afterHookMethod
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


    override fun initSecHook(secClassLoader: ClassLoader?) {
        super.initSecHook(secClassLoader)
        initSecHooker(QSHeaderViewListener())
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
        initSecHooker(QSToggleSliderRadius())
        initSecHooker(QSHeaderMessage())
        initSecHooker(QSEditButton())
        initSecHooker(QSControlCenterList())
        initSecHooker(VolumeColumnProgressRadius())
        initSecHooker(powerMenu)
        initSecHooker(VolumeView())
        initSecHooker(deviceCenterRow)
        initSecHooker(QSMiplayDetailVolumeBar())
    }


}