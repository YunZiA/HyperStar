package com.yunzia.hyperstar.hook.init

import android.content.Context
import android.content.ContextWrapper
import com.yunzia.hyperstar.hook.app.plugin.HideVolumeCollpasedFootButton
import com.yunzia.hyperstar.hook.app.plugin.QSCardAutoCollapse
import com.yunzia.hyperstar.hook.app.plugin.QSCardTileList
import com.yunzia.hyperstar.hook.app.plugin.QSControlCenterList
import com.yunzia.hyperstar.hook.app.plugin.QSEditText
import com.yunzia.hyperstar.hook.app.plugin.QSMediaDefaultApp
import com.yunzia.hyperstar.hook.app.plugin.QSMediaNoPlayTitle
import com.yunzia.hyperstar.hook.app.plugin.QSMiplayDetailVolumeBar
import com.yunzia.hyperstar.hook.app.plugin.QSToggleSliderRadius
import com.yunzia.hyperstar.hook.app.plugin.QSVolumeMute
import com.yunzia.hyperstar.hook.app.plugin.SuperBlurWidgetManager
import com.yunzia.hyperstar.hook.app.plugin.VolumeBarLayoutParams
import com.yunzia.hyperstar.hook.app.plugin.os2.DeviceCenterRow
import com.yunzia.hyperstar.hook.app.plugin.os2.LongPressVolumeBarToExpand
import com.yunzia.hyperstar.hook.app.plugin.os2.QSControlCenterColor
import com.yunzia.hyperstar.hook.app.plugin.os2.QSEditButton
import com.yunzia.hyperstar.hook.app.plugin.os2.QSHeaderMessage
import com.yunzia.hyperstar.hook.app.plugin.os2.QSHeaderViewListener
import com.yunzia.hyperstar.hook.app.plugin.os2.QSMediaCoverBackground
import com.yunzia.hyperstar.hook.app.plugin.os2.QSMediaDeviceName
import com.yunzia.hyperstar.hook.app.plugin.os2.QSMiplayAppIconRadius
import com.yunzia.hyperstar.hook.app.plugin.os2.SuperBlurVolumeManager
import com.yunzia.hyperstar.hook.app.plugin.os2.VolumeColumnProgressRadius
import com.yunzia.hyperstar.hook.app.plugin.os2.VolumeOrQSBrightnessValue
import com.yunzia.hyperstar.hook.app.plugin.os3.QSListView
import com.yunzia.hyperstar.hook.app.plugin.os3.QSTileAutoCollapse
import com.yunzia.hyperstar.hook.app.plugin.powermenu.PowerMenuHook
import com.yunzia.hyperstar.hook.base.InitHooker
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.base.replaceHookMethod
import com.yunzia.hyperstar.hook.tool.starLog


class PluginHookForOS3 : InitHooker() {

    private val qsMediaCoverBackground = QSMediaCoverBackground()
    private val qsControlCenterColor = QSControlCenterColor()
    private val powerMenuHook = PowerMenuHook()
    private val deviceCenterRow = DeviceCenterRow()

    override fun initHook() {

        var isHooked = false
        val pluginInstancePluginFactory = findClass("com.android.systemui.shared.plugins.PluginInstance\$PluginFactory",classLoader)

        pluginInstancePluginFactory.afterHookMethod("createPluginContext"){
            val mPluginContext = it.result as ContextWrapper
            val pathClassLoader = mPluginContext.classLoader
            if (mPluginContext.packageName != plugin){
                starLog.logD("检测到非目标应用包名: 当前包名为 " + mPluginContext.packageName + ", 目标插件包名为 " + plugin)
                return@afterHookMethod
            }

            if (pathClassLoader == null) {
                starLog.logE("Failed to load pluginClassLoader: null returned")
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


    override fun initResources() {

        if (resparam!!.packageName != plugin) return
        starLog.log("PluginHookForOS3 initResources")

        //initResource(qsControlCenterColor)
        //initResource(powerMenuHook)
        initResource(qsMediaCoverBackground)
        initResource(deviceCenterRow)
        initResource(QSMediaNoPlayTitle())
        initResource(QSEditText())
        initResource(VolumeOrQSBrightnessValue())



    }



    override fun initSecHook(secClassLoader: ClassLoader?) {
        super.initSecHook(secClassLoader)
        initSecHooker(QSHeaderViewListener())
        initSecHooker(SuperBlurWidgetManager())
        initSecHooker(SuperBlurVolumeManager())
        initSecHooker(qsMediaCoverBackground)
        initSecHooker(QSMediaDeviceName())
        initSecHooker(QSMediaDefaultApp())
        initSecHooker(QSMiplayAppIconRadius())
        //initSecHooker(QSMediaView())
        initSecHooker(QSTileAutoCollapse())
        initSecHooker(qsControlCenterColor)
        initSecHooker(QSListView())
        initSecHooker(VolumeOrQSBrightnessValue())
        initSecHooker(QSCardTileList())
        initSecHooker(QSVolumeMute())
        initSecHooker(QSCardAutoCollapse())
        initSecHooker(QSToggleSliderRadius())
        initSecHooker(QSEditButton())
        initSecHooker(QSControlCenterList())
        initSecHooker(VolumeColumnProgressRadius())
        initSecHooker(powerMenuHook)
        initSecHooker(VolumeBarLayoutParams())
        initSecHooker(LongPressVolumeBarToExpand())
        initSecHooker(HideVolumeCollpasedFootButton())
        initSecHooker(deviceCenterRow)
        initSecHooker(QSMiplayDetailVolumeBar())


    }

    fun flipCard(){
        findClass(
            "miui.systemui.util.CommonUtils",
            secClassLoader
        ).apply {
            replaceHookMethod(
                "isFlipDevice"
            ){
                return@replaceHookMethod true
            }
            replaceHookMethod(
                "isTinyScreen",
                Context::class.java
            ){
                return@replaceHookMethod true
            }
        }

        //折叠屏卡片
        findClass(
            "miui.systemui.controlcenter.panel.main.qs.CompactQSCardController",
            secClassLoader
        ).apply {
            replaceHookMethod(
                "onCreate",
            ) {
                return@replaceHookMethod null
            }
        }
    }




}