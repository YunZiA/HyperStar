package com.chaos.hyperstar.ui.module.systemui.controlcenter.item

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import chaos.ui.Card
import com.chaos.hyperstar.ui.base.MSuperDialog
import com.chaos.hyperstar.ui.base.modifier.elevation
import com.chaos.hyperstar.ui.module.systemui.controlcenter.EnableItemDropdown
import com.chaos.hyperstar.ui.module.systemui.controlcenter.EnableItemSlider
import com.chaos.hyperstar.utils.SPUtils
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil
import top.yukonga.miuix.kmp.utils.squircleshape.SquircleShape

@Composable
fun DeviceCenterItem(
    items: MutableState<List<Card>>,
    index: Int,
    item: Card,
) {
    val enable = remember { mutableStateOf(SPUtils.getBoolean("deviceCenter_span_size_enable",false)) }
    val showDialog = remember { mutableStateOf(false) }
    val spanSize =  remember { mutableFloatStateOf(SPUtils.getFloat("deviceCenter_span_size", 4f)) }

    LaunchedEffect(showDialog.value) {
        if (showDialog.value){
            return@LaunchedEffect
        }

        val mutableList = items.value.toMutableList().apply{
            if (enable.value){
                set(index, Card(item.id,item.tag, spanSize.floatValue.toInt(),item.name))
            } else{
                set(index, Card(item.id,item.tag, 4,item.name))
            }

        }

        items.value = mutableList


    }

    if (showDialog.value){

        MiuixPopupUtil.showDialog() {
            MSuperDialog(
                title = item.name,
                show = showDialog,
                showAction = true,
                color = colorScheme.background,
                onDismissRequest = {
                    showDialog.value = false
                }
            ) {

                Card(
                    Modifier.padding(bottom = 10.dp)
                ){
                    EnableItemDropdown(
                        key = "deviceCenter_land_rightOrLeft"
                    )

                }

                Card{
                    EnableItemSlider(
                        key = "deviceCenter_span_size",
                        state = enable,
                        progress = 4f,
                        progressState = spanSize
                    )


                }

            }
        }

    }

    Box(
        modifier = Modifier
            .height(85.dp)
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .elevation(
                shape = SquircleShape(18.dp),
                backgroundColor = colorScheme.secondary,
                shadowElevation = 2f
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        showDialog.value = true
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = item.name,
            fontWeight = FontWeight(550),
            color = colorScheme.onSurfaceVariantSummary
        )
    }
}