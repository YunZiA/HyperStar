package com.yunzia.hyperstar.hook.app.plugin

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class QSCardTile : BaseHooker() {



    private val clickClose = XSPUtils.getBoolean("card_tile_click_close",false)

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)

        startMethodsHook()
    }

    fun collapseStatusBar(context: Context) {
        try {
            val systemService = context.getSystemService("statusbar")
            systemService.javaClass.getMethod("collapsePanels", *arrayOfNulls(0)).invoke(systemService, *arrayOfNulls(0))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startMethodsHook() {

        if (!clickClose){
            return
        }

        val onFinishInflate  = XposedHelpers.findClassIfExists("miui.systemui.controlcenter.qs.tileview.QSCardItemView\$onFinishInflate\$1",classLoader)


        XposedHelpers.findAndHookMethod(onFinishInflate,"invoke",View::class.java,object : XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject


                val qsCardItemView = XposedHelpers.getObjectField(thisObj,"this$0") as LinearLayout

                val clickAction = XposedHelpers.getObjectField(qsCardItemView,"clickAction") ?: return

                collapseStatusBar(qsCardItemView.context)

            }
        })



    }

}