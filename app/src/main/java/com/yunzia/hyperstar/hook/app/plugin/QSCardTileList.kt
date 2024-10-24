package com.yunzia.hyperstar.hook.app.plugin

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.hook.tool.starLog.logE
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.findAndHookMethod


class QSCardTileList :BaseHooker() {

    private val mCardStyleTiles = XSPUtils.getString("card_tile_list","wifi|cell|")

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        if (XSPUtils.getBoolean("use_card_tile_list",false)){

            startMethodsHook()

        }

    }

    private fun startMethodsHook() {

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
                    idUnavailable = linearLayout.context.resources.getIdentifier("qs_card_cell_background_unavailable",
                        "drawable", "miui.systemui.plugin");
//                    val cornerRadius = linearLayout.context.resources.getIdentifier(
//                        "control_center_universal_corner_radius", "dimen", "miui.systemui.plugin"
//                    )
//                    cornerRadiusF = linearLayout.context.resources.getDimensionPixelSize(cornerRadius).toFloat()
                }
            }
        )
        findAndHookMethod(QSCardItemView,"setCornerRadius",Float::class.java,object : XC_MethodHook(){
            override fun beforeHookedMethod(param: MethodHookParam?) {
                super.beforeHookedMethod(param)
                val linearLayout = param?.thisObject as LinearLayout
                val cornerRadius = XposedHelpers.getFloatField(linearLayout, "_cornerRadius")
                param.args[0] = cornerRadius
            }


        })
//
        findAndHookMethod(QSCardItemView, "updateBackground", object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val linearLayout: LinearLayout = param.thisObject as LinearLayout
                    val state = XposedHelpers.getObjectField(param.thisObject, "state")
                    if (state == null){
                        starLog.log("state == null")
                        return
                    }
                    val i = XposedHelpers.getIntField(state, "state")
                    val spec = XposedHelpers.getObjectField(state, "spec")
                    val cornerRadius = XposedHelpers.getObjectField(param.thisObject, "_cornerRadius")
                    if(spec == null){
                        starLog.log("spec == null")
                        val id :Int = when (i) {
                            0 -> {
                                idUnavailable
                            }
                            2 -> {
                                idEnable
                            }
                            1 -> {
                                idDisabled
                            }
                            else -> {
                                return
                            }
                        }
                        if (id == -1 ) {
                            logE("updateBackground", "id is -1!!")
                            return
                        }
                        val background: Drawable = linearLayout.context.theme.resources.getDrawable(id, linearLayout.context.theme)
//                        if (background is GradientDrawable){
//                            background.setStroke(10, Color.RED)
//                        }
                        linearLayout.background = background
                        XposedHelpers.callMethod(
                            param.thisObject,
                            "setCornerRadius",
                            cornerRadius
                        )
                        return
                    }
                    when (spec.toString()) {
                        "bt", "cell", "flashlight", "wifi", "vowifi1", "vowifi2" -> {
//                            val background = linearLayout.background
//                            if (background is GradientDrawable){
//                                background.shape
//                                background.setStroke(30, Color.parseColor("#40FFFFFF"))
//                            }
//                            linearLayout.background = background
                        }
                        else -> {
                            starLog.log("spec is else $spec")
                            val id :Int = when (i) {
                                0 -> {
                                    idUnavailable
                                }
                                2 -> {
                                    idEnable
                                }
                                1 -> {
                                    idDisabled
                                }
                                else -> {
                                    return
                                }
                            }
                            if (id == -1 ) {
                                logE("updateBackground", "id is -1!!")
                                return
                            }
                            val background: Drawable = linearLayout.context.theme.resources.getDrawable(id, linearLayout.context.theme)
//                            if (background is GradientDrawable){
//                                //background.colo
//                                background.setStroke(20, Color.parseColor("#40FFFFFF"))
//                            }
                            linearLayout.background = background

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