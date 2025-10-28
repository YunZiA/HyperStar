package com.yunzia.hyperstar.hook.app.plugin

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.afterHookConstructor
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.hook.tool.starLog.logE
import com.yunzia.hyperstar.utils.XSPUtils


class QSCardTileList : Hooker() {

    private val mCardStyleTiles = XSPUtils.getString("card_tile_list","wifi|cell|")

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
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

        findClass(
            "miui.systemui.controlcenter.qs.QSController",
            classLoader
        ).beforeHookMethod(
            "getCardStyleTileSpecs"
        ){
            it.result = cardStyleTiles

        }

        findClass(
            "miui.systemui.controlcenter.qs.tileview.QSCardItemView",
            classLoader
        ).apply {
            afterHookConstructor(
                Context::class.java,
                AttributeSet::class.java
            ){
                this as LinearLayout
                idEnable = this.context.resources.getIdentifier(
                    "qs_card_wifi_background_enabled",
                    "drawable", plugin
                )
                idDisabled = this.context.resources.getIdentifier(
                    "qs_card_wifi_background_disabled",
                    "drawable", plugin
                )
                idUnavailable = this.context.resources.getIdentifier(
                    "qs_card_cell_background_unavailable",
                    "drawable", plugin)

            }
            beforeHookMethod("setCornerRadius",Float::class.java){
                this as LinearLayout
                val cornerRadius = this.getFloatField("_cornerRadius")
                it.args[0] = cornerRadius

            }
            beforeHookMethod("updateBackground"){
                this as LinearLayout
                val state = this.getObjectField("state")
                if (state == null){
                    starLog.logE("state == null")
                    return@beforeHookMethod
                }
                val i = state.getIntField("state")
                val spec = state.getObjectField("spec")
                val cornerRadius = this.getObjectField("_cornerRadius")
                if(spec == null){
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
                            return@beforeHookMethod
                        }
                    }
                    if (id == -1 ) {
                        logE("updateBackground", "id is -1!!")
                        return@beforeHookMethod
                    }
                    val background: Drawable = this.context.theme.resources.getDrawable(id, this.context.theme)
                    this.background = background
                    this.callMethod(
                        "setCornerRadius",
                        cornerRadius
                    )
                    return@beforeHookMethod
                }
                when (spec.toString()) {
                    "bt", "cell", "flashlight", "wifi", "vowifi1", "vowifi2" -> {

                    }
                    else -> {
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
                                return@beforeHookMethod
                            }
                        }
                        if (id == -1 ) {
                            logE("updateBackground", "id is -1!!")
                            return@beforeHookMethod
                        }
                        val background: Drawable = this.context.theme.resources.getDrawable(id, this.context.theme)
                        this.background = background

                        this.callMethod(
                            "setCornerRadius",
                            cornerRadius
                        )
                    }
                }
            }


        }

    }

    private fun getList():ArrayList<String> {

        if (mCardStyleTiles.isNullOrEmpty()) return ArrayList()

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