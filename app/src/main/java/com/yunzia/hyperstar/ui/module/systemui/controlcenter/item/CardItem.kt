package com.yunzia.hyperstar.ui.module.systemui.controlcenter.item

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import yunzia.ui.Card
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.MSuperDialog
import com.yunzia.hyperstar.ui.base.modifier.elevation
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.EnableItemDropdown
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.EnableItemSlider
import com.yunzia.hyperstar.utils.SPUtils
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.showDialog
import top.yukonga.miuix.kmp.utils.squircleshape.SquircleShape

@Composable
fun CardItem(
    items: MutableState<List<Card>>,
    index: Int,
    item: Card
) {
//
    val enable = remember { mutableStateOf(SPUtils.getBoolean("cards_span_size_enable",false)) }
    val showDialog = remember { mutableStateOf(false) }
    val spanSize =  remember { mutableFloatStateOf(SPUtils.getFloat("cards_span_size", 2f)) }
    val span = remember { mutableIntStateOf(2) }
    val name = stringArrayResource(id = R.array.card_tile_name)
    LaunchedEffect(showDialog.value) {
        if (showDialog.value){
            return@LaunchedEffect
        }
        val mutableList = items.value.toMutableList().apply{
            val spanIsOne = spanSize.floatValue == 1f && enable.value
            set(index, Card(item.id, item.tag, if (spanIsOne) 2 else 4, item.name))
        }

        span.intValue = if (enable.value) spanSize.floatValue.toInt() else 2
        items.value = mutableList
    }
    LazyVerticalGrid(modifier = Modifier
        .fillMaxWidth()
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = {
                    showDialog.value = true
                }
            )
        }
        .heightIn(85.dp, 170.dp),
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        userScrollEnabled = false
    ) {
        items(2,
            span = {
                GridItemSpan(if (span.intValue != 1) span.intValue else 2)
            }
        ) {
            Box(
                modifier = Modifier
                    .height(85.dp)
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 4.dp)
                    .elevation(
                        shape = SquircleShape(18.dp),
                        backgroundColor = colorScheme.secondary,
                        shadowElevation = 2f
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = name[it],
                    modifier = Modifier.padding(horizontal = 20.dp).basicMarquee(),
                    fontSize = 16.sp,
                    maxLines = 1,
                    fontWeight = FontWeight(550),
                    color =  colorScheme.onSurfaceVariantSummary
                )


            }
        }
    }


    if (showDialog.value){

        showDialog() {
            MSuperDialog(
                title = item.name,
                show = showDialog,
                showAction = true,
                color = colorScheme.background,
                onDismissRequest = {
                    dismissDialog(showDialog)
                }
            ) {

                Card(Modifier.padding(bottom = 10.dp)){

                    EnableItemDropdown(
                        key = "cards_land_rightOrLeft",
                        dfOpt = 1
                    )
                }
                Card{
                    EnableItemSlider(
                        key = "cards_span_size",
                        progress = 2f,
                        state = enable,
                        progressState = spanSize
                    )


                }
            }
        }

    }

}