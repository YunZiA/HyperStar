package com.yunzia.hyperstar.hook.base

import android.content.ContextWrapper
import android.content.res.XModuleResources
import com.yunzia.hyperstar.hook.app.plugin.SuperBlurVolumeManager
import com.yunzia.hyperstar.hook.app.plugin.SuperBlurWidgetManager
import com.yunzia.hyperstar.hook.app.plugin.os2.DeviceCenterRow
import com.yunzia.hyperstar.hook.app.plugin.os2.QSControlCenterColor
import com.yunzia.hyperstar.hook.app.plugin.os2.QSControlCenterList
import com.yunzia.hyperstar.hook.app.plugin.os2.QSEditButton
import com.yunzia.hyperstar.hook.app.plugin.QSEditText
import com.yunzia.hyperstar.hook.app.plugin.os2.QSHeaderMessage
import com.yunzia.hyperstar.hook.app.plugin.os2.QSHeaderViewListener
import com.yunzia.hyperstar.hook.app.plugin.os2.QSListView
import com.yunzia.hyperstar.hook.app.plugin.os2.QSMediaCoverBackground
import com.yunzia.hyperstar.hook.app.plugin.os2.QSMediaDeviceName
import com.yunzia.hyperstar.hook.app.plugin.QSMediaNoPlayTitle
import com.yunzia.hyperstar.hook.app.plugin.os2.QSMediaView
import com.yunzia.hyperstar.hook.app.plugin.os2.QSMiplayAppIconRadius
import com.yunzia.hyperstar.hook.app.plugin.QSMiplayDetailVolumeBar
import com.yunzia.hyperstar.hook.app.plugin.os2.QSVolumeOrBrightnessValue
import com.yunzia.hyperstar.hook.app.plugin.os2.VolumeColumnProgressRadius
import com.yunzia.hyperstar.hook.app.plugin.os2.VolumeView
import com.yunzia.hyperstar.hook.app.plugin.QSCardTile
import com.yunzia.hyperstar.hook.app.plugin.QSCardTileList
import com.yunzia.hyperstar.hook.app.plugin.QSMediaDefaultApp
import com.yunzia.hyperstar.hook.app.plugin.QSToggleSliderRadius
import com.yunzia.hyperstar.hook.app.plugin.powermenu.PowerMenu
import com.yunzia.hyperstar.hook.tool.starLog
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources


class PluginHookForOS2() : BaseHooker() {

    private val qsMediaCoverBackground = QSMediaCoverBackground()
    private val qsControlCenterColor = QSControlCenterColor()
    private val powerMenu = PowerMenu()
    private val deviceCenterRow = DeviceCenterRow()

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        startSystemUIPluginHook()
    }


    override fun doResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
        modRes: XModuleResources?
    ) {
        super.doResources(resparam, modRes)

        if (resparam!!.packageName != plugin) return

        doResources(qsControlCenterColor)
        doResources(powerMenu)
        doResources(QSMiplayAppIconRadius())
        doResources(qsMediaCoverBackground)
        doResources(deviceCenterRow)
        doResources(QSMediaNoPlayTitle())
        doResources(QSEditText())



    }


    var isHooked : Boolean = false;

    private fun startSystemUIPluginHook(){

        val pluginInstance = findClass("com.android.systemui.shared.plugins.PluginInstance",classLoader)

        XposedHelpers.findAndHookMethod(pluginInstance,"loadPlugin",object : XC_MethodHook(){

            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject
                val mPluginContext = XposedHelpers.getObjectField(thisObj,"mPluginContext") as ContextWrapper
                val pathClassLoader = mPluginContext.classLoader

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
        })


    }


    override fun doHook(secClassLoader: ClassLoader?) {
        super.doHook(secClassLoader)
        doSecMethods(QSHeaderViewListener())
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
        doSecMethods(QSEditButton())
        doSecMethods(QSControlCenterList())
        doSecMethods(VolumeColumnProgressRadius())
        doSecMethods(powerMenu)
        doSecMethods(VolumeView())
        doSecMethods(deviceCenterRow)
        doSecMethods(QSMiplayDetailVolumeBar())
    }


}