package com.yunzia.hyperstar.hook.util.plugin

import android.view.View
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.callStaticMethod
import com.yunzia.hyperstar.hook.core.helper.getStaticBooleanField
import com.yunzia.hyperstar.hook.core.helper.getStaticObjectField

class CommonUtils(private val classLoader: ClassLoader?) {


    val CommonUtils = findClass("miui.systemui.util.CommonUtils",classLoader)

    val INSTANCE by lazy {
        CommonUtils.getStaticObjectField("INSTANCE")
    }


    val IS_TABLET by lazy {
        CommonUtils.getStaticBooleanField("IS_TABLET")
    }

    fun setLayoutSizeDefault(
        view: View,
        height:Int,
        width:Int,
        boolean: Boolean = false,
        num : Int = 4,
        any: Any? = null
    ) {
        CommonUtils.callStaticMethod(
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
        CommonUtils.callStaticMethod(
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