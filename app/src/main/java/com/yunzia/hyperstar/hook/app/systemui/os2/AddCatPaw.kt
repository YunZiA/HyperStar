package com.yunzia.hyperstar.hook.app.systemui.os2

import android.content.res.XModuleResources
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.base.getDimensionPixelOffset
import com.yunzia.hyperstar.hook.tool.starLog
import de.robv.android.xposed.callbacks.XC_InitPackageResources


class AddCatPaw:Hooker() {

    private var catPaw :Int = 0

    override fun initResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
        modRes: XModuleResources?
    ) {
        super.initResources(resparam, modRes)
        catPaw = resparam?.res?.addResource(modRes, R.drawable.cat_paw)!!

    }

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        val miuiCollapsedStatusBarFragment  = findClass("com.android.systemui.statusbar.phone.MiuiCollapsedStatusBarFragment",classLoader)

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
                starLog.log("getChildAt $i is $child")
            }
            val paw = context.resources.getDrawable(catPaw)
            val size = getDimensionPixelOffset(context.resources,"status_bar_clock_size",systemUI).toInt()
            val icon = View(context).apply {
                background = paw
            }
            val lp = LinearLayout.LayoutParams(size,size).apply {
                gravity = Gravity.CENTER_VERTICAL

            }
            phoneStatusBarLeftContainer.addView(icon,0,lp)
            starLog.logD("$clock")
            starLog.logD("${phoneStatusBarLeftContainer.childCount}")

        }

    }

    


}