package com.chaos.hyperstar.hook.app.plugin

import android.R.attr
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import com.chaos.hyperstar.hook.base.BaseHooker
import com.chaos.hyperstar.hook.tool.starLog.logE
import com.chaos.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.callbacks.XCallback


class QsCardTileList :BaseHooker() {

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        if (XSPUtils.getBoolean("use_card_tile_list",false)){

            startMethodsHook(classLoader)

        }

    }

    private fun startMethodsHook(classLoader: ClassLoader?) {

        val cardStyleTiles = getList()

        if (cardStyleTiles.isEmpty()){
            return
        }

        var idEnable = -1
        var idDisabled = -1

        var idUnavailable = -1

        //var cornerRadiusF = -1f

        findAndHookMethod("miui.systemui.controlcenter.qs.QSController", classLoader,
            "getCardStyleTileSpecs",
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    param.result = cardStyleTiles
                }
            })


        val QSCardItemView  = XposedHelpers.findClass("miui.systemui.controlcenter.qs.tileview.QSCardItemView",classLoader)

        XposedHelpers.findAndHookConstructor(QSCardItemView,
            Context::class.java,
            AttributeSet::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val linearLayout = param.thisObject as LinearLayout
                    idEnable = linearLayout.context.resources.getIdentifier(
                        "qs_card_wifi_background_enabled",
                        "drawable", "miui.systemui.plugin"
                    )
                    idDisabled = linearLayout.context.resources.getIdentifier(
                        "qs_card_wifi_background_disabled",
                        "drawable", "miui.systemui.plugin"
                    )
                    idUnavailable = linearLayout.getContext().getResources().getIdentifier("qs_card_cell_background_unavailable",
                        "drawable", "miui.systemui.plugin");
//                    val cornerRadius = linearLayout.context.resources.getIdentifier(
//                        "control_center_universal_corner_radius", "dimen", "miui.systemui.plugin"
//                    )
//                    cornerRadiusF = linearLayout.context.resources.getDimensionPixelSize(cornerRadius).toFloat()
                }
            }
        )

        findAndHookMethod(QSCardItemView, "updateBackground", object : XC_MethodHook(XCallback.PRIORITY_HIGHEST) {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val state = XposedHelpers.getObjectField(param.thisObject, "state")
                    val cornerRadius = XposedHelpers.getObjectField(param.thisObject, "_cornerRadius")
                    val spec = XposedHelpers.getObjectField(state, "spec") as String
                    val i = XposedHelpers.getIntField(state, "state")
                    when (spec) {
                        "bt", "cell", "flashlight", "wifi", "vowifi1", "vowifi2" -> {}
                        else -> {
                            val linearLayout: LinearLayout = param.thisObject as LinearLayout
                            val id :Int
                            if (i == 0){
                                id = idUnavailable
                            } else if (i == 2) {
                                id = idEnable
                            } else if (i == 1) {
                                id = idDisabled
                            } else{
                                return
                            }
                            if (id == -1 ) {
                                logE("updateBackground", "id is -1!!")
                                return
                            }
                            val disabled: Drawable = linearLayout.getContext().getTheme().getResources().getDrawable(id, linearLayout.getContext().getTheme())
                            linearLayout.setBackground(disabled)
                            XposedHelpers.callMethod(
                                param.thisObject,
                                "setCornerRadius",
                                cornerRadius
                            )
                        }
                    }
                }
            }
        )

    }

    fun getList():ArrayList<String> {

        val mCardStyleTiles = XSPUtils.getString("card_tile_list","")

        if (mCardStyleTiles == null) {
            return ArrayList()
        }

        if (mCardStyleTiles.isEmpty()) {
                // 字符串为空，没有数据可以处理，直接返回
            return ArrayList()
        }

        val listFromString: List<String> = mCardStyleTiles.split("|")

        val cardLists =  emptyList<String>().toMutableList()
        val tileList = ArrayList<String>()
        for (tag in listFromString){
            if (tag.isEmpty()){
                break
            }
            cardLists.add(tag)
            tileList.add(tag)
            Log.d("ggc",tag)

        }
        return tileList

    }

}