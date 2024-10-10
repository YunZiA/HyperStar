package com.chaos.hyperstar.ui.module.controlcenter.card

import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.BaseActivity
import com.chaos.hyperstar.utils.SPUtils

class QsCardListActivity : BaseActivity() {
    val cardMap = mutableMapOf<String, Card>()
    var cardList : List<Card> = emptyList<Card>()
    var cardLists : List<Card> = emptyList<Card>()

    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        QsCardListPager(this)
    }

    override fun initData(savedInstanceState: Bundle?) {

        val cardTagList = this.resources.getStringArray(R.array.card_list)
        val cardNameList = this.resources.getStringArray(R.array.card_tile_name)

        val list = emptyList<Card>().toMutableList()

        for (index in 0 until cardTagList.size) {

            val tag = cardTagList[index]
            cardMap.put(tag, Card(index,tag,cardNameList[index]))
            list.add(Card(index,tag,cardNameList[index]))

        }
        getList()
        list.removeAll(cardList.toMutableList())
        cardLists = list


    }

    fun saveList(items: List<Card>) {
        val builder = StringBuilder()
        for (tile in items) {
            builder.append(tile.tag).append("|")
        }
        val mCardStyleTiles = builder.toString()

        cardList = items

        SPUtils.setString("card_tile_list",mCardStyleTiles)

    }

    fun getList() {

        val mCardStyleTiles = SPUtils.getString("card_tile_list","")

        Log.d("ggc",mCardStyleTiles)
        if (mCardStyleTiles == ""){
            cardList = listOf(cardMap.getValue("wifi"),cardMap.getValue("cell"))
            return
        }
        val listFromString: List<String> = mCardStyleTiles.split("|")

        val cardLists =  emptyList<Card>().toMutableList()
        for (tag in listFromString){
            if (tag.isEmpty()){
                break
            }

            cardLists.add(cardMap.getValue(tag))
        }

        cardList = cardLists


    }


}

data class Card(
    val id : Int,
    val tag: String,
    val name : String,
)

