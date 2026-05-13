package com.yunzia.hyperstar.hook.app.plugin

import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.getObjectFieldAs
import com.yunzia.hyperstar.hook.core.helper.replaceHookMethod
import com.yunzia.hyperstar.prefs.XSPUtils

object QSControlCenterList : BasePluginHook() {

    private val priorityEnable by lazy { XSPUtils.getBoolean("controlCenter_priority_enable",false) }

    private val cardPriority by lazy { XSPUtils.getFloat("cards_priority", 30f).toInt() }
    private val mediaPriority by lazy { XSPUtils.getFloat("media_priority", 31f).toInt() }
    private val brightnessPriority by lazy { XSPUtils.getFloat("brightness_priority", 32f).toInt() }
    private val volumePriority by lazy { XSPUtils.getFloat("volume_priority", 33f).toInt() }
    private val deviceControlPriority by lazy { XSPUtils.getFloat("deviceControl_priority", 34f).toInt() }
    private val deviceCenterPriority by lazy { XSPUtils.getFloat("deviceCenter_priority", 35f).toInt() }
    private val listPriority by lazy { XSPUtils.getFloat("list_priority", 36f).toInt() }
    private val editPriority by lazy { XSPUtils.getFloat("edit_priority", 37f).toInt() }

    private val cardRightOrLeftEnable by lazy { XSPUtils.getBoolean("cards_land_rightOrLeft_enable", false) }
    private val mediaRightOrLeftEnable by lazy { XSPUtils.getBoolean("media_land_rightOrLeft_enable", false) }
    private val brightnessRightOrLeftEnable by lazy { XSPUtils.getBoolean("brightness_land_rightOrLeft_enable", false) }
    private val volumeRightOrLeftEnable by lazy { XSPUtils.getBoolean("volume_land_rightOrLeft_enable", false) }
    private val deviceControlRightOrLeftEnable by lazy { XSPUtils.getBoolean("deviceControl_land_rightOrLeft_enable", false) }
    private val deviceCenterRightOrLeftEnable by lazy { XSPUtils.getBoolean("deviceCenter_land_rightOrLeft_enable", false) }
    private val listRightOrLeftEnable by lazy { XSPUtils.getBoolean("list_land_rightOrLeft_enable", false) }

    private val cardRightOrLeft by lazy { XSPUtils.getInt("cards_land_rightOrLeft", 1) == 1 }
    private val mediaRightOrLeft by lazy { XSPUtils.getInt("media_land_rightOrLeft", 1) == 1 }
    private val brightnessRightOrLeft by lazy { XSPUtils.getInt("brightness_land_rightOrLeft", 1) == 1 }
    private val volumeRightOrLeft by lazy { XSPUtils.getInt("volume_land_rightOrLeft", 1) == 1 }
    private val deviceControlRightOrLeft by lazy { XSPUtils.getInt("deviceControl_land_rightOrLeft", 0) == 1 }
    private val deviceCenterRightOrLeft by lazy { XSPUtils.getInt("deviceCenter_land_rightOrLeft", 0) == 1 }
    private val listRightOrLeft by lazy { XSPUtils.getInt("list_land_rightOrLeft", 0) == 1 }

    private val cardSpanSizeEnable by lazy { XSPUtils.getBoolean("cards_span_size_enable", false) }
    private val deviceControlSpanSizeEnable by lazy { XSPUtils.getBoolean("deviceControl_span_size_enable", false) }
    private val deviceCenterSpanSizeEnable by lazy { XSPUtils.getBoolean("deviceCenter_span_size_enable", false) }
    private val listSpanSizeEnable by lazy { XSPUtils.getBoolean("list_span_size_enable", false) }
    private val editSpanSizeEnable by lazy { XSPUtils.getBoolean("edit_span_size_enable", false) }

    private val cardSpanSize by lazy { XSPUtils.getFloat("cards_span_size", 2f).toInt() }
    private val deviceControlSpanSize by lazy { XSPUtils.getFloat("deviceControl_span_size", 4f).toInt() }
    private val deviceCenterSpanSize by lazy { XSPUtils.getFloat("deviceCenter_span_size", 4f).toInt() }
    private val listSpanSize by lazy { XSPUtils.getFloat("list_span_size", 1f).toInt() }
    private val editSpanSize by lazy { XSPUtils.getFloat("edit_span_size", 4f).toInt() }

    override fun init() {
        startPriorityHook()
        startMethodsHook()
    }

    private fun startPriorityHook() {
        if (!priorityEnable) return
        val QSCardsController = findClass("miui.systemui.controlcenter.panel.main.qs.QSCardsController",pluginClassLoader)
        val MediaPlayerController = findClass("miui.systemui.controlcenter.panel.main.media.MediaPlayerController",pluginClassLoader)
        val BrightnessSliderController = findClass("miui.systemui.controlcenter.panel.main.brightness.BrightnessSliderController",pluginClassLoader)
        val VolumeSliderController = findClass("miui.systemui.controlcenter.panel.main.volume.VolumeSliderController",pluginClassLoader)
        val DeviceControlsEntryController = findClass("miui.systemui.controlcenter.panel.main.devicecontrol.DeviceControlsEntryController",pluginClassLoader)
        val DeviceCenterEntryController = findClass("miui.systemui.controlcenter.panel.main.devicecenter.entry.DeviceCenterEntryController",pluginClassLoader)
        val QSListController = findClass("miui.systemui.controlcenter.panel.main.qs.QSListController",pluginClassLoader)
        val EditButtonController = findClass("miui.systemui.controlcenter.panel.main.qs.EditButtonController",pluginClassLoader)

        QSCardsController.replaceHookMethod("getPriority"){
            return@replaceHookMethod cardPriority
        }

        MediaPlayerController.replaceHookMethod("getPriority"){
            return@replaceHookMethod mediaPriority
        }

        BrightnessSliderController.replaceHookMethod("getPriority"){
            return@replaceHookMethod brightnessPriority
        }

        VolumeSliderController.replaceHookMethod("getPriority"){
            return@replaceHookMethod volumePriority
        }

        DeviceControlsEntryController.replaceHookMethod("getPriority"){
            return@replaceHookMethod deviceControlPriority
        }

        DeviceCenterEntryController.replaceHookMethod("getPriority"){
            return@replaceHookMethod deviceCenterPriority
        }

        QSListController.replaceHookMethod("getPriority"){
            return@replaceHookMethod listPriority
        }

        EditButtonController.replaceHookMethod("getPriority"){
            return@replaceHookMethod editPriority
        }
    }

    private fun startMethodsHook() {
        val QSCardsController = findClass("miui.systemui.controlcenter.panel.main.qs.QSCardsController",pluginClassLoader)
        val MediaPlayerController = findClass("miui.systemui.controlcenter.panel.main.media.MediaPlayerController",pluginClassLoader)
        val BrightnessSliderController = findClass("miui.systemui.controlcenter.panel.main.brightness.BrightnessSliderController",pluginClassLoader)
        val VolumeSliderController = findClass("miui.systemui.controlcenter.panel.main.volume.VolumeSliderController",pluginClassLoader)
        val DeviceControlsEntryController = findClass("miui.systemui.controlcenter.panel.main.devicecontrol.DeviceControlsEntryController",pluginClassLoader)
        val DeviceCenterEntryController = findClass("miui.systemui.controlcenter.panel.main.devicecenter.entry.DeviceCenterEntryController",pluginClassLoader)
        val QSListController = findClass("miui.systemui.controlcenter.panel.main.qs.QSListController",pluginClassLoader)
        val EditButtonController = findClass("miui.systemui.controlcenter.panel.main.qs.EditButtonController",pluginClassLoader)

        val QSRecord = findClass("miui.systemui.controlcenter.panel.main.qs.QSRecord",pluginClassLoader)
        if (cardSpanSizeEnable || listSpanSizeEnable){
            QSRecord.replaceHookMethod("getSpanSize"){
                val isCard = thisObject.getObjectFieldAs<Boolean>("isCard")
                val spanSize = thisObject.getObjectFieldAs<Int>("spanSize")
                return@replaceHookMethod if(isCard){
                    if (cardSpanSizeEnable) cardSpanSize else spanSize
                } else{
                    if (listSpanSizeEnable) listSpanSize else spanSize
                }

            }

        }
        if (cardRightOrLeftEnable){
            QSCardsController.replaceHookMethod("getRightOrLeft"){
                return@replaceHookMethod cardRightOrLeft
            }
        }
        if (mediaRightOrLeftEnable){
            MediaPlayerController.replaceHookMethod("getRightOrLeft"){
                return@replaceHookMethod mediaRightOrLeft
            }
        }
        if (brightnessRightOrLeftEnable){
            BrightnessSliderController.replaceHookMethod("getRightOrLeft"){
                return@replaceHookMethod brightnessRightOrLeft
            }
        }
        if (volumeRightOrLeftEnable){
            VolumeSliderController.replaceHookMethod("getRightOrLeft"){
                return@replaceHookMethod volumeRightOrLeft
            }
        }
        DeviceControlsEntryController.apply {
            if (deviceControlRightOrLeftEnable){
                replaceHookMethod("getRightOrLeft"){
                    return@replaceHookMethod deviceControlRightOrLeft
                }
            }
            if (deviceControlSpanSizeEnable){
                replaceHookMethod("getSpanSize"){
                    return@replaceHookMethod deviceControlSpanSize
                }
            }
        }
        DeviceCenterEntryController.apply {
            if (deviceCenterRightOrLeftEnable){
                replaceHookMethod("getRightOrLeft"){
                    return@replaceHookMethod deviceCenterRightOrLeft
                }
            }
            if (deviceCenterSpanSizeEnable){
                replaceHookMethod("getSpanSize"){
                    return@replaceHookMethod deviceCenterSpanSize
                }
            }
        }
        if (listRightOrLeftEnable){
            QSListController.replaceHookMethod("getRightOrLeft"){
                return@replaceHookMethod listRightOrLeft
            }
        }
        if (editSpanSizeEnable){
            EditButtonController.replaceHookMethod("getSpanSize"){
                return@replaceHookMethod editSpanSize
            }
        }
    }

}