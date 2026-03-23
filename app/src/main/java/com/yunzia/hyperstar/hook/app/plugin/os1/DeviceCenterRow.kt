package com.yunzia.hyperstar.hook.app.plugin.os1

import android.view.View
import android.view.ViewGroup
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.StarLog.logD
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.hookLayout
import com.yunzia.hyperstar.hook.core.helper.getObjectFieldAs
import com.yunzia.hyperstar.hook.core.helper.replaceHookAllConstructors
import com.yunzia.hyperstar.hook.core.helper.replaceHookMethod
import com.yunzia.hyperstar.hook.core.helper.setObjectField
import com.yunzia.hyperstar.hook.util.android.findViewByIdName
import com.yunzia.hyperstar.prefs.XSPUtils

object DeviceCenterRow: BasePluginHook() {

    val deviceCenterSpanSize = XSPUtils.getFloat("deviceCenter_span_size", 4f).toInt()
    val isDeviceCenterMode = XSPUtils.getInt("is_device_center_mode", 0)

//    override fun init() {
//
//
//
//    }


    override fun init() {

        val a = findClass("h.a.g.a",pluginClassLoader)
        val DeviceCenterCardController = findClass("miui.systemui.controlcenter.panel.main.devicecenter.devices.DeviceCenterCardController",pluginClassLoader)
        val DeviceCenterEntryViewHolderMode = findClass("miui.systemui.controlcenter.panel.main.devicecenter.entry.DeviceCenterEntryViewHolder\$Mode",pluginClassLoader)

        if (deviceCenterSpanSize == 1){
            hookLayout("device_center_empty_item",plugin) {
                this as ViewGroup
                val title = this.findViewByIdName("title")
                val icon = this.findViewByIdName("icon")
                val lp = icon?.layoutParams as ViewGroup.MarginLayoutParams
                lp.marginStart = 0
                icon.layoutParams = lp
                title?.visibility = View.GONE
            }

        }
        //res.setReplacement(plugin,"dimen","device_center_device_item_width",res.getDimensionPixelSize(device_center_item_height))
        if (deviceCenterSpanSize < 4){
            hookLayout("device_center_device_item",plugin) {
                this as ViewGroup
                val lp = layoutParams.apply {
                    width = height
                }
                layoutParams = lp
            }
        }
        if (isDeviceCenterMode != 0 || deviceCenterSpanSize !=4){
            a.replaceHookAllConstructors { args ->
                thisObject.setObjectField("a", args[0])
                val list = args[1] as List<*>

                if (deviceCenterSpanSize == 1 || isDeviceCenterMode == 1){
                    val lists = list.subList(0,0 )
                    thisObject.setObjectField("f", lists)
                    return@replaceHookAllConstructors null
                }
                if (isDeviceCenterMode == 2){
                    val size = deviceCenterSpanSize-1
                    if (list.size <= size){
                        thisObject.setObjectField("f", list)

                    }else{
                        val lists = list.subList(0,size)
                        thisObject.setObjectField("f", lists)

                    }
                    return@replaceHookAllConstructors null
                }
                val size = deviceCenterSpanSize*2-1
                if (list.size <= size){
                    thisObject.setObjectField("f", list)
                }else{
                    val lists = list.subList(0,size )
                    thisObject.setObjectField("f", lists)
                }


                return@replaceHookAllConstructors null

            }
            DeviceCenterCardController.replaceHookMethod("getMode") {
                val deviceItems = thisObject.getObjectFieldAs<ArrayList<*>>("deviceItems")
                val rowMode: Array<out Any> = DeviceCenterEntryViewHolderMode?.getEnumConstants()!!

                if (deviceItems.size == 1 || deviceCenterSpanSize == 1 || isDeviceCenterMode == 1){
                    logD("1")
                    return@replaceHookMethod rowMode[0]
                }else{
                    logD(">1")
                    return@replaceHookMethod if (deviceItems.size > deviceCenterSpanSize){
                        if (isDeviceCenterMode == 2){
                            rowMode[1]
                        }else{
                            rowMode[2]
                        }
                    }else{
                        rowMode[1]
                    }
                }
            }
        }
    }
}