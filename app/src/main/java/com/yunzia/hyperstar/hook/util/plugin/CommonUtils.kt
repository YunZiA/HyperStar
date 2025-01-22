package com.yunzia.hyperstar.hook.util.plugin

import android.view.View
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.findClass

class CommonUtils(classLoader: ClassLoader?) {


    val CommonUtils = findClass("miui.systemui.util.CommonUtils",classLoader)

    val INSTANCE = XposedHelpers.getStaticObjectField(CommonUtils,"INSTANCE")


    val IS_TABLET = XposedHelpers.getStaticBooleanField(CommonUtils,"IS_TABLET")

    fun setLayoutSizeDefault(
        view: View,
        height:Int,
        width:Int,
        boolean: Boolean = false,
        num : Int = 4,
        any: Any? = null
    ) {
        XposedHelpers.callStaticMethod(
        CommonUtils,
        "setLayoutSize\$default",
        INSTANCE,
        view,
        height,
        width,
        boolean,
        num,
        any
    )

    }

    fun setLayoutHeightDefault(
        view: View,
        height:Int,
        boolean: Boolean = false,
        num : Int = 4,
        any: Any? = null
    ){
        XposedHelpers.callStaticMethod(
            CommonUtils,
            "setLayoutHeight\$default",
            INSTANCE,
            view,
            height,
            boolean,
            num,
            any
        )
    }




}