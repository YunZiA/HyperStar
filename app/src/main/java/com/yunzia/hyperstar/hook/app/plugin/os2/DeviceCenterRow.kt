package com.yunzia.hyperstar.hook.app.plugin.os2

import android.content.res.XModuleResources
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.util.plugin.CommonUtils
import com.yunzia.hyperstar.hook.util.starLog
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LayoutInflated


class DeviceCenterRow: Hooker() {

    val deviceCenterSpanSize = XSPUtils.getFloat("deviceCenter_span_size", 4f).toInt()
    val isDeviceCenterMode = XSPUtils.getInt("is_device_center_mode", 0)

    override fun initResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
        modRes: XModuleResources?
    ) {
        super.initResources(resparam, modRes)



        if (deviceCenterSpanSize == 1){
            resparam?.res?.hookLayout(plugin,"layout","device_center_empty_item",object : XC_LayoutInflated(){
                override fun handleLayoutInflated(liparam: LayoutInflatedParam?) {

                    val root = liparam?.view as ViewGroup
                    val title = root.findViewByIdName("title")
                    val icon = root.findViewByIdName("icon")
                    val lp = icon?.layoutParams as MarginLayoutParams
                    lp.marginStart = 0
                    icon.layoutParams = lp
                    title?.visibility = View.GONE
//                }
                }

            })

        }

        if (deviceCenterSpanSize < 4){
            resparam?.res?.hookLayout(plugin,"layout","device_center_device_item",object : XC_LayoutInflated(){
                override fun handleLayoutInflated(liparam: LayoutInflatedParam?) {

                    val root = liparam?.view as ViewGroup
                    val lp = root.layoutParams
                    lp.width = lp.height
                    root.layoutParams = lp
                }

            })

        }

    }


    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        if (deviceCenterSpanSize < 4){
            val DetailViewHolder = findClass("miui.systemui.controlcenter.panel.main.devicecenter.devices.DetailViewHolder",classLoader)
            val DeviceItemViewHolder = findClass("miui.systemui.controlcenter.panel.main.devicecenter.devices.DeviceItemViewHolder",classLoader)

            val commonUtils = CommonUtils(classLoader)

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

        val a = findClass("miui.systemui.devicecenter.a",classLoader)

        val DeviceCenterCardController = findClass("miui.systemui.controlcenter.panel.main.devicecenter.devices.DeviceCenterCardController",classLoader)

        val DeviceCenterEntryViewHolderMode = findClass("miui.systemui.controlcenter.panel.main.devicecenter.entry.DeviceCenterEntryViewHolder\$Mode",classLoader)

        if (isDeviceCenterMode != 0 || deviceCenterSpanSize !=4){
            a.replaceHookedAllConstructors{
                this.setObjectField("a", it.args[0])
                val list = it.args[1] as List<*>

                if (deviceCenterSpanSize == 1 || isDeviceCenterMode == 1){
                    val lists = list.subList(0,0 )
                    this.setObjectField("b", lists)
                    return@replaceHookedAllConstructors null
                }
                if (isDeviceCenterMode == 2){
                    val size = deviceCenterSpanSize-1
                    if (list.size <= size){
                        starLog.logD("list.size <= size")
                        this.setObjectField("b", list)

                    }else{
                        starLog.logD("list.size  size")
                        val lists = list.subList(0,size)
                        this.setObjectField("b", lists)

                    }
                    return@replaceHookedAllConstructors null
                }
                val size = deviceCenterSpanSize*2-1
                if (list.size <= size){
                    this.setObjectField("b", list)

                }else{
                    val lists = list.subList(0,size )
                    this.setObjectField("b", lists)

                }

                return@replaceHookedAllConstructors null

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