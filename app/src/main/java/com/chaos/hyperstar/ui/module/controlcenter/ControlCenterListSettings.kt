package com.chaos.hyperstar.ui.module.controlcenter

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import chaos.ui.Card
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.BaseActivity
import com.chaos.hyperstar.utils.SPUtils
import com.chaos.hyperstar.utils.XSPUtils

class ControlCenterListSettings : BaseActivity() {
    var cardList : List<Card> = emptyList<Card>()
    val cardMap = mutableMapOf<String, String>()
    var itemLists : List<String> = emptyList<String>()
    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        ControlCenterListPager(this)
    }

    override fun initData() {
        getLists()
        val cardTagList = this.resources.getStringArray(R.array.control_center_item_list)
        val cardNameList = this.resources.getStringArray(R.array.control_center_item_list_name)

        val list = emptyList<Card>().toMutableList()

        cardTagList.forEachIndexed { index, s ->
            cardMap[s] = cardNameList[index]
        }

        itemLists.forEachIndexed { index, value ->
            val tag = value

            var mColumn = 4

            when(tag){
                "cards" ->{
                    mColumn = 4
                }
                "media" ->{
                    mColumn = 2
                }
                "brightness", "volume" ->{
                    mColumn = 1
                }
                "deviceControl"->{
                    mColumn = 4
                }
                "deviceCenter"->{
                    mColumn = 4
                }
                "list"->{
                    mColumn = 4
                }
                "edit"->{
                    mColumn = 4
                }
            }

            list.add(
                Card(index, tag, mColumn, cardMap.getValue(value))
            )
        }

//        for (index in 0 until cardTagList.size) {
//
//
//
//        }
        cardList = list
    }

    private fun getLists() {

        val cardPriority = Pair("cards", SPUtils.getFloat("cards_priority", 30f))
        val mediaPriority = Pair("media", SPUtils.getFloat("media_priority", 31f))
        val brightnessPriority = Pair("brightness", SPUtils.getFloat("brightness_priority", 32f))
        val volumePriority = Pair("volume", SPUtils.getFloat("volume_priority", 33f))
        val deviceControlPriority = Pair("deviceControl", SPUtils.getFloat("deviceControl_priority", 34f))
        val deviceCenterPriority = Pair("deviceCenter", SPUtils.getFloat("deviceCenter_priority", 35f))
        val listPriority = Pair("list", SPUtils.getFloat("list_priority", 36f))
        val editPriority = Pair("edit", SPUtils.getFloat("edit_priority", 37f))

        // 将这些 Pair 放入一个列表中
        val prioritiesList = listOf(cardPriority, mediaPriority, brightnessPriority, volumePriority, deviceControlPriority, deviceCenterPriority, listPriority, editPriority)

        // 按照浮点数值的大小排序
        val sortedPriorities = prioritiesList.sortedBy { it.second }

        val c = emptyList<String>().toMutableList()

        // 打印排序后的结果
        sortedPriorities.forEach {
            c.add(it.first)
        }
        itemLists = c


    }

    fun setLists(list: List<String>){

        list.forEachIndexed { index, s ->
            SPUtils.setFloat(s+"_priority",30f+index)

        }

    }

}