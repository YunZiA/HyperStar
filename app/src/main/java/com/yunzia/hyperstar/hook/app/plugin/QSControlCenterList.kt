package com.yunzia.hyperstar.hook.app.plugin

import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

class QSControlCenterList : BaseHooker() {

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

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)

        startPriorityHook()
        startMethodsHook()
    }

    private fun startPriorityHook() {
        if (!priorityEnable) return
        val QSCardsController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.qs.QSCardsController",classLoader)
        val MediaPlayerController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.media.MediaPlayerController",classLoader)
        val BrightnessSliderController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.brightness.BrightnessSliderController",classLoader)
        val VolumeSliderController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.volume.VolumeSliderController",classLoader)
        val DeviceControlsEntryController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.devicecontrol.DeviceControlsEntryController",classLoader)
        val DeviceCenterEntryController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.devicecenter.entry.DeviceCenterEntryController",classLoader)
        val QSListController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.qs.QSListController",classLoader)
        val EditButtonController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.qs.EditButtonController",classLoader)

        XposedHelpers.findAndHookMethod(QSCardsController,"getPriority",object : XC_MethodReplacement(){
            override fun replaceHookedMethod(param: MethodHookParam?): Any {
                return cardPriority
            }
        })
        XposedHelpers.findAndHookMethod(MediaPlayerController,"getPriority",object : XC_MethodReplacement(){
            override fun replaceHookedMethod(param: MethodHookParam?): Any {
                return mediaPriority
            }
        })
        XposedHelpers.findAndHookMethod(BrightnessSliderController,"getPriority",object : XC_MethodReplacement(){
            override fun replaceHookedMethod(param: MethodHookParam?): Any {
                return brightnessPriority
            }
        })
        XposedHelpers.findAndHookMethod(VolumeSliderController,"getPriority",object : XC_MethodReplacement(){
            override fun replaceHookedMethod(param: MethodHookParam?): Any {
                return volumePriority
            }
        })
        XposedHelpers.findAndHookMethod(DeviceControlsEntryController,"getPriority",object : XC_MethodReplacement(){
            override fun replaceHookedMethod(param: MethodHookParam?): Any {
                return deviceControlPriority
            }
        })
        XposedHelpers.findAndHookMethod(DeviceCenterEntryController,"getPriority",object : XC_MethodReplacement(){
            override fun replaceHookedMethod(param: MethodHookParam?): Any {
                return deviceCenterPriority
            }
        })
        XposedHelpers.findAndHookMethod(QSListController,"getPriority",object : XC_MethodReplacement(){
            override fun replaceHookedMethod(param: MethodHookParam?): Any {
                return listPriority
            }
        })
        XposedHelpers.findAndHookMethod(EditButtonController,"getPriority",object : XC_MethodReplacement(){
            override fun replaceHookedMethod(param: MethodHookParam?): Any {
                return editPriority
            }
        })
    }

    private fun startMethodsHook() {
        val QSCardsController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.qs.QSCardsController",classLoader)
        val MediaPlayerController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.media.MediaPlayerController",classLoader)
        val BrightnessSliderController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.brightness.BrightnessSliderController",classLoader)
        val VolumeSliderController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.volume.VolumeSliderController",classLoader)
        val DeviceControlsEntryController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.devicecontrol.DeviceControlsEntryController",classLoader)
        val DeviceCenterEntryController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.devicecenter.entry.DeviceCenterEntryController",classLoader)
        val QSListController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.qs.QSListController",classLoader)
        val EditButtonController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.qs.EditButtonController",classLoader)

        val QSRecord = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.qs.QSRecord",classLoader)


        if (cardSpanSizeEnable || listSpanSizeEnable){
            XposedHelpers.findAndHookMethod(QSRecord,"getSpanSize",object : XC_MethodReplacement(){
                override fun replaceHookedMethod(param: MethodHookParam?): Any {
                    val thisObj = param?.thisObject
                    val isCard = XposedHelpers.getObjectField(thisObj,"isCard") as Boolean
                    val spanSize = XposedHelpers.getObjectField(thisObj,"spanSize") as Int
                    return if(isCard){
                        if (cardSpanSizeEnable) cardSpanSize else spanSize
                    } else{
                        if (listSpanSizeEnable) listSpanSize else spanSize
                    }

                }
            })

        }


        if (cardRightOrLeftEnable){
            XposedHelpers.findAndHookMethod(QSCardsController,"getRightOrLeft",object : XC_MethodReplacement(){
                override fun replaceHookedMethod(param: MethodHookParam?): Any {
                    return cardRightOrLeft
                }
            })


        }

        if (mediaRightOrLeftEnable){

            XposedHelpers.findAndHookMethod(MediaPlayerController,"getRightOrLeft",object : XC_MethodReplacement(){
                override fun replaceHookedMethod(param: MethodHookParam?): Any {
                    return mediaRightOrLeft
                }
            })

        }


        if (brightnessRightOrLeftEnable){
            XposedHelpers.findAndHookMethod(BrightnessSliderController,"getRightOrLeft",object : XC_MethodReplacement(){
                override fun replaceHookedMethod(param: MethodHookParam?): Any {
                    return brightnessRightOrLeft
                }
            })

        }


        if (volumeRightOrLeftEnable){
            XposedHelpers.findAndHookMethod(VolumeSliderController,"getRightOrLeft",object : XC_MethodReplacement(){
                override fun replaceHookedMethod(param: MethodHookParam?): Any {
                    return volumeRightOrLeft
                }
            })

        }


        if (deviceControlRightOrLeftEnable){
            XposedHelpers.findAndHookMethod(DeviceControlsEntryController,"getRightOrLeft",object : XC_MethodReplacement(){
                override fun replaceHookedMethod(param: MethodHookParam?): Any {
                    return deviceControlRightOrLeft
                }
            })

        }

        if (deviceControlSpanSizeEnable){
            XposedHelpers.findAndHookMethod(DeviceControlsEntryController,"getSpanSize",object : XC_MethodReplacement(){
                override fun replaceHookedMethod(param: MethodHookParam?): Any {
                    return deviceControlSpanSize
                }
            })

        }

        if (deviceCenterRightOrLeftEnable){
            XposedHelpers.findAndHookMethod(DeviceCenterEntryController,"getRightOrLeft",object : XC_MethodReplacement(){
                override fun replaceHookedMethod(param: MethodHookParam?): Any {
                    return deviceCenterRightOrLeft
                }
            })

        }


        if (deviceCenterSpanSizeEnable){
            XposedHelpers.findAndHookMethod(DeviceCenterEntryController,"getSpanSize",object : XC_MethodReplacement(){
                override fun replaceHookedMethod(param: MethodHookParam?): Any {
                    return deviceCenterSpanSize
                }
            })

        }

        if (listRightOrLeftEnable){
            XposedHelpers.findAndHookMethod(QSListController,"getRightOrLeft",object : XC_MethodReplacement(){
                override fun replaceHookedMethod(param: MethodHookParam?): Any {
                    return listRightOrLeft
                }
            })

        }


        if (editSpanSizeEnable){
            XposedHelpers.findAndHookMethod(EditButtonController,"getSpanSize",object : XC_MethodReplacement(){
                override fun replaceHookedMethod(param: MethodHookParam?): Any {
                    return editSpanSize
                }
            })

        }



    }

}