package com.yunzia.hyperstar.hook.app.plugin.os2

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import com.yunzia.hyperstar.hook.core.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.base.getDimensionPixelSize
import com.yunzia.hyperstar.hook.core.helper.replaceHookMethod
import com.yunzia.hyperstar.hook.core.Log
import com.yunzia.hyperstar.hook.core.Log.logD
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.hookLayout
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.hook.core.helper.getObjectFieldAs
import com.yunzia.hyperstar.hook.core.helper.replaceHookAllConstructors
import com.yunzia.hyperstar.hook.core.helper.setObjectField
import com.yunzia.hyperstar.hook.util.plugin.CommonUtils
import com.yunzia.hyperstar.prefs.XSPUtils
import io.github.kyuubiran.ezxhelper.android.util.ViewUtil.findViewByIdName


object DeviceCenterRow: BasePluginHook() {

    val deviceCenterSpanSize = XSPUtils.getFloat("deviceCenter_span_size", 4f).toInt()
    val isDeviceCenterMode = XSPUtils.getInt("is_device_center_mode", 0)

    override fun init() {

        if (deviceCenterSpanSize == 1){
            hookLayout("device_center_empty_item",plugin) {
                this as ViewGroup
                val title = findViewByIdName("title")
                val icon = findViewByIdName("icon")
                val lp = icon?.layoutParams as MarginLayoutParams
                lp.marginStart = 0
                icon.layoutParams = lp
                title?.visibility = View.GONE
            }
        }

        if (deviceCenterSpanSize < 4){
            hookLayout("device_center_device_item",plugin) {
                this as ViewGroup
                val lp = layoutParams
                lp.width = lp.height
                layoutParams = lp
            }

        }


        if (deviceCenterSpanSize < 4){
            val DetailViewHolder = findClass("miui.systemui.controlcenter.panel.main.devicecenter.devices.DetailViewHolder",pluginClassLoader)
            val DeviceItemViewHolder = findClass("miui.systemui.controlcenter.panel.main.devicecenter.devices.DeviceItemViewHolder",pluginClassLoader)

            val commonUtils = CommonUtils(pluginClassLoader)

            DetailViewHolder.afterHookMethod("onConfigurationChanged",Int::class.java){

                val itemView = this.getObjectFieldAs<View>("itemView")
                val res = itemView.resources
                val size = getDimensionPixelSize(res,"device_center_item_height",plugin)

                commonUtils.setLayoutSizeDefault(
                    itemView,
                    size,
                    size,
                    false,
                    4,
                    null
                )
            }

            DeviceItemViewHolder.afterHookMethod("onConfigurationChanged",Int::class.java){

                val itemView =  this.getObjectFieldAs<View>("itemView")
                val res = itemView.resources
                val size = getDimensionPixelSize(res,"device_center_item_height",plugin)

                commonUtils.setLayoutSizeDefault(
                    itemView,
                    size,
                    size,
                    false,
                    4,
                    null
                )
            }


        }

        val a = findClass("miui.systemui.devicecenter.a",pluginClassLoader)

        val DeviceCenterCardController = findClass("miui.systemui.controlcenter.panel.main.devicecenter.devices.DeviceCenterCardController",pluginClassLoader)

        val DeviceCenterEntryViewHolderMode = findClass("miui.systemui.controlcenter.panel.main.devicecenter.entry.DeviceCenterEntryViewHolder\$Mode",pluginClassLoader)

        if (isDeviceCenterMode != 0 || deviceCenterSpanSize !=4){
            a.replaceHookAllConstructors{
                this.setObjectField("a", it.args[0])
                val list = it.args[1] as List<*>

                if (deviceCenterSpanSize == 1 || isDeviceCenterMode == 1){
                    val lists = list.subList(0,0 )
                    this.setObjectField("b", lists)
                    return@replaceHookAllConstructors null
                }
                if (isDeviceCenterMode == 2){
                    val size = deviceCenterSpanSize-1
                    if (list.size <= size){
                        logD("list.size <= size")
                        this.setObjectField("b", list)

                    }else{
                        logD("list.size  size")
                        val lists = list.subList(0,size)
                        this.setObjectField("b", lists)

                    }
                    return@replaceHookAllConstructors null
                }
                val size = deviceCenterSpanSize*2-1
                if (list.size <= size){
                    this.setObjectField("b", list)

                }else{
                    val lists = list.subList(0,size )
                    this.setObjectField("b", lists)

                }

                return@replaceHookAllConstructors null

            }

            DeviceCenterCardController.replaceHookMethod("getMode"){
                val deviceItems = this.getObjectFieldAs<ArrayList<*>>("deviceItems")
                val rowMode: Array<out Any> = DeviceCenterEntryViewHolderMode?.getEnumConstants()!!

                if (deviceItems.size == 1 || deviceCenterSpanSize == 1 || isDeviceCenterMode == 1){
                    return@replaceHookMethod rowMode[0]
                }else{
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