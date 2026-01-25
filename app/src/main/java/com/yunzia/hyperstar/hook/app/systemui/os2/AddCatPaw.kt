package com.yunzia.hyperstar.hook.app.systemui.os2

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.yunzia.hyperstar.hook.core.BaseHook
import com.yunzia.hyperstar.hook.base.getDimensionPixelOffset
import com.yunzia.hyperstar.hook.core.Log
import com.yunzia.hyperstar.hook.core.Log.log
import com.yunzia.hyperstar.hook.core.Log.logD
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import io.github.kyuubiran.ezxhelper.android.util.ViewUtil.findViewByIdName
import io.github.kyuubiran.ezxhelper.xposed.EzXposed


object AddCatPaw:BaseHook() {

    private var catPaw :Int = 0

//    override fun initResources(
//        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
//        modRes: XModuleResources?
//    ) {
//        super.initResources(resparam, modRes)
//        catPaw = resparam?.res?.addResource(modRes, R.drawable.cat_paw)!!
//
//    }

    override fun init() {

        val miuiCollapsedStatusBarFragment  = findClass("com.android.systemui.statusbar.phone.MiuiCollapsedStatusBarFragment")

        miuiCollapsedStatusBarFragment.afterHookMethod(
            "onCreateView",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Bundle::class.java
        ){
            val view = it.result as ViewGroup
            val context = view.context
            val clock = view.findViewByIdName("clock")
            val phoneStatusBarLeftContainer = view.findViewByIdName("phone_status_bar_left_container") as LinearLayout
            for (i in 0 until phoneStatusBarLeftContainer.childCount) {
                val child = phoneStatusBarLeftContainer.getChildAt(i)
                log("getChildAt $i is $child")
            }
            val paw = context.resources.getDrawable(catPaw)
            val size = getDimensionPixelOffset(context.resources,"status_bar_clock_size", EzXposed.hookedPackageName).toInt()
            val icon = View(context).apply {
                background = paw
            }
            val lp = LinearLayout.LayoutParams(size,size).apply {
                gravity = Gravity.CENTER_VERTICAL

            }
            phoneStatusBarLeftContainer.addView(icon,0,lp)
            logD("$clock")
            logD("${phoneStatusBarLeftContainer.childCount}")

        }

    }

    


}