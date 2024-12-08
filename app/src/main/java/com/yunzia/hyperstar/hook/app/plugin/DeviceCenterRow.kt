package com.yunzia.hyperstar.hook.app.plugin

import android.R
import android.content.res.XModuleResources
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LayoutInflated


class DeviceCenterRow:BaseHooker() {

    val deviceCenterSpanSize = XSPUtils.getFloat("deviceCenter_span_size", 4f).toInt()
    val isDeviceCenterMode = XSPUtils.getInt("is_device_center_mode", 0)

    override fun doResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
        modRes: XModuleResources?
    ) {
        super.doResources(resparam, modRes)



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
                    starLog.log("root.layoutParams ${root.layoutParams}")
//                for (i in root.childCount) {
//                }
                }

            })

        }

    }


    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)

        if (deviceCenterSpanSize < 4){
            val DetailViewHolder = findClass("miui.systemui.controlcenter.panel.main.devicecenter.devices.DetailViewHolder",classLoader)
            val DeviceItemViewHolder = findClass("miui.systemui.controlcenter.panel.main.devicecenter.devices.DeviceItemViewHolder",classLoader)

            val CommonUtils = findClass("miui.systemui.util.CommonUtils",classLoader)


            XposedHelpers.findAndHookMethod(DetailViewHolder,"onConfigurationChanged",Int::class.java,object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    val thisObj = param?.thisObject

                    val itemView = XposedHelpers.getObjectField(thisObj,"itemView") as View
                    val res = itemView.resources

                    val deviceCenterItemHeight = res.getIdentifier("device_center_item_height","dimen",plugin)
                    val size = res.getDimensionPixelSize(deviceCenterItemHeight)

                    val INSTANCE = XposedHelpers.getStaticObjectField(CommonUtils,"INSTANCE")
                    XposedHelpers.callStaticMethod(
                        CommonUtils,
                        "setLayoutSize\$default",
                        INSTANCE,
                        itemView,
                        size,
                        size,
                        false,
                        4,
                        null
                    )

                }

            })

            XposedHelpers.findAndHookMethod(DeviceItemViewHolder,"onConfigurationChanged",Int::class.java,object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    val thisObj = param?.thisObject

                    val itemView = XposedHelpers.getObjectField(thisObj,"itemView") as View
                    val res = itemView.resources

                    val deviceCenterItemHeight = res.getIdentifier("device_center_item_height","dimen",plugin)
                    val size = res.getDimensionPixelSize(deviceCenterItemHeight)

                    val INSTANCE = XposedHelpers.getStaticObjectField(CommonUtils,"INSTANCE")
                    XposedHelpers.callStaticMethod(
                        CommonUtils,
                        "setLayoutSize\$default",
                        INSTANCE,
                        itemView,
                        size,
                        size,
                        false,
                        4,
                        null
                    )

                }

            })

        }

        val a = findClass("miui.systemui.devicecenter.a",classLoader)

        val DeviceCenterCardController = findClass("miui.systemui.controlcenter.panel.main.devicecenter.devices.DeviceCenterCardController",classLoader)

        val DeviceCenterEntryViewHolderMode = findClass("miui.systemui.controlcenter.panel.main.devicecenter.entry.DeviceCenterEntryViewHolder\$Mode",classLoader)

        if (isDeviceCenterMode != 0 || deviceCenterSpanSize !=4){

            XposedBridge.hookAllConstructors(a,object :XC_MethodReplacement(){
                override fun replaceHookedMethod(param: MethodHookParam?): Any? {
                    val thisObj = param?.thisObject
                    XposedHelpers.setObjectField(thisObj,"a",param?.args?.get(0))
                    val list = param?.args?.get(1) as List<*>

                    if (deviceCenterSpanSize == 1 || isDeviceCenterMode == 1){
                        val lists = list.subList(0,0 )
                        XposedHelpers.setObjectField(thisObj,"b",lists)
                        return null
                    }
                    if (isDeviceCenterMode == 2){
                        starLog.log("isDeviceCenterMode == 2")
                        val size = deviceCenterSpanSize-1
                        if (list.size <= size){
                            starLog.log("list.size <= size")

                            XposedHelpers.setObjectField(thisObj,"b",list)

                        }else{
                            starLog.log("list.size  size")
                            val lists = list.subList(0,size)
                            XposedHelpers.setObjectField(thisObj,"b",lists)

                        }
                        return null
                    }
                    val size = deviceCenterSpanSize*2-1
                    if (list.size <= size){

                        XposedHelpers.setObjectField(thisObj,"b",list)

                    }else{
                        val lists = list.subList(0,size )
                        XposedHelpers.setObjectField(thisObj,"b",lists)

                    }


                    return null

                }

            })

            XposedHelpers.findAndHookMethod(DeviceCenterCardController,"getMode",object :XC_MethodReplacement(){
                override fun replaceHookedMethod(param: MethodHookParam?): Any {
                    val thisObj = param?.thisObject
                    val deviceItems = XposedHelpers.getObjectField(thisObj,"deviceItems") as ArrayList<*>
                    val rowMode: Array<out Any> = DeviceCenterEntryViewHolderMode.getEnumConstants()!!

                    if (deviceItems.size == 1 || deviceCenterSpanSize == 1 || isDeviceCenterMode == 1){
                        starLog.log("1")
                        return rowMode[0]
                    }else{
                        starLog.log(">1")
                        return if (deviceItems.size > deviceCenterSpanSize){
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

            })
        }



    }


}