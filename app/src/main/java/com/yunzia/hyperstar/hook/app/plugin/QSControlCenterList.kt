package com.yunzia.hyperstar.hook.app.plugin

import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.utils.XSPUtils

class QSControlCenterList : Hooker() {

    private val priorityEnable = XSPUtils.getBoolean("controlCenter_priority_enable",false)

    val cardPriority = XSPUtils.getFloat("cards_priority", 30f).toInt()
    val mediaPriority = XSPUtils.getFloat("media_priority", 31f).toInt()
    val brightnessPriority = XSPUtils.getFloat("brightness_priority", 32f).toInt()
    val volumePriority = XSPUtils.getFloat("volume_priority", 33f).toInt()
    val deviceControlPriority =  XSPUtils.getFloat("deviceControl_priority", 34f).toInt()
    val deviceCenterPriority = XSPUtils.getFloat("deviceCenter_priority", 35f).toInt()
    val listPriority = XSPUtils.getFloat("list_priority", 36f).toInt()
    val editPriority = XSPUtils.getFloat("edit_priority", 37f).toInt()

    val cardRightOrLeftEnable = XSPUtils.getBoolean("cards_land_rightOrLeft_enable", false)
    val mediaRightOrLeftEnable = XSPUtils.getBoolean("media_land_rightOrLeft_enable", false)
    val brightnessRightOrLeftEnable = XSPUtils.getBoolean("brightness_land_rightOrLeft_enable", false)
    val volumeRightOrLeftEnable = XSPUtils.getBoolean("volume_land_rightOrLeft_enable", false)
    val deviceControlRightOrLeftEnable =  XSPUtils.getBoolean("deviceControl_land_rightOrLeft_enable", false)
    val deviceCenterRightOrLeftEnable = XSPUtils.getBoolean("deviceCenter_land_rightOrLeft_enable", false)
    val listRightOrLeftEnable = XSPUtils.getBoolean("list_land_rightOrLeft_enable", false)

    val cardRightOrLeft = XSPUtils.getInt("cards_land_rightOrLeft", 1) == 1
    val mediaRightOrLeft = XSPUtils.getInt("media_land_rightOrLeft", 1) == 1
    val brightnessRightOrLeft = XSPUtils.getInt("brightness_land_rightOrLeft", 1) == 1
    val volumeRightOrLeft = XSPUtils.getInt("volume_land_rightOrLeft", 1) == 1
    val deviceControlRightOrLeft =  XSPUtils.getInt("deviceControl_land_rightOrLeft", 0) == 1
    val deviceCenterRightOrLeft = XSPUtils.getInt("deviceCenter_land_rightOrLeft", 0) == 1
    val listRightOrLeft = XSPUtils.getInt("list_land_rightOrLeft", 0) == 1

    val cardSpanSizeEnable = XSPUtils.getBoolean("cards_span_size_enable", false)
    val deviceControlSpanSizeEnable =  XSPUtils.getBoolean("deviceControl_span_size_enable", false)
    val deviceCenterSpanSizeEnable = XSPUtils.getBoolean("deviceCenter_span_size_enable", false)
    val listSpanSizeEnable = XSPUtils.getBoolean("list_span_size_enable", false)
    val editSpanSizeEnable = XSPUtils.getBoolean("edit_span_size_enable", false)

    val cardSpanSize = XSPUtils.getFloat("cards_span_size", 2f).toInt()
    val deviceControlSpanSize =  XSPUtils.getFloat("deviceControl_span_size", 4f).toInt()
    val deviceCenterSpanSize = XSPUtils.getFloat("deviceCenter_span_size", 4f).toInt()
    val listSpanSize = XSPUtils.getFloat("list_span_size", 1f).toInt()
    val editSpanSize = XSPUtils.getFloat("edit_span_size", 4f).toInt()

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        startPriorityHook()
        startMethodsHook()
    }

    private fun startPriorityHook() {
        if (!priorityEnable) return
        val QSCardsController = findClass("miui.systemui.controlcenter.panel.main.qs.QSCardsController",classLoader)
        val MediaPlayerController = findClass("miui.systemui.controlcenter.panel.main.media.MediaPlayerController",classLoader)
        val BrightnessSliderController = findClass("miui.systemui.controlcenter.panel.main.brightness.BrightnessSliderController",classLoader)
        val VolumeSliderController = findClass("miui.systemui.controlcenter.panel.main.volume.VolumeSliderController",classLoader)
        val DeviceControlsEntryController = findClass("miui.systemui.controlcenter.panel.main.devicecontrol.DeviceControlsEntryController",classLoader)
        val DeviceCenterEntryController = findClass("miui.systemui.controlcenter.panel.main.devicecenter.entry.DeviceCenterEntryController",classLoader)
        val QSListController = findClass("miui.systemui.controlcenter.panel.main.qs.QSListController",classLoader)
        val EditButtonController = findClass("miui.systemui.controlcenter.panel.main.qs.EditButtonController",classLoader)

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
        val QSCardsController = findClass("miui.systemui.controlcenter.panel.main.qs.QSCardsController",classLoader)
        val MediaPlayerController = findClass("miui.systemui.controlcenter.panel.main.media.MediaPlayerController",classLoader)
        val BrightnessSliderController = findClass("miui.systemui.controlcenter.panel.main.brightness.BrightnessSliderController",classLoader)
        val VolumeSliderController = findClass("miui.systemui.controlcenter.panel.main.volume.VolumeSliderController",classLoader)
        val DeviceControlsEntryController = findClass("miui.systemui.controlcenter.panel.main.devicecontrol.DeviceControlsEntryController",classLoader)
        val DeviceCenterEntryController = findClass("miui.systemui.controlcenter.panel.main.devicecenter.entry.DeviceCenterEntryController",classLoader)
        val QSListController = findClass("miui.systemui.controlcenter.panel.main.qs.QSListController",classLoader)
        val EditButtonController = findClass("miui.systemui.controlcenter.panel.main.qs.EditButtonController",classLoader)

        val QSRecord = findClass("miui.systemui.controlcenter.panel.main.qs.QSRecord",classLoader)


        if (cardSpanSizeEnable || listSpanSizeEnable){
            QSRecord.replaceHookMethod("getSpanSize"){
                val isCard = this.getObjectFieldAs<Boolean>("isCard")
                val spanSize = this.getObjectFieldAs<Int>("spanSize")
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