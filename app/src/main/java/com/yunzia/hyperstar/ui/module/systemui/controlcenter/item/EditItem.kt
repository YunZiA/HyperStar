package com.yunzia.hyperstar.ui.module.systemui.controlcenter.item

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.sp
import yunzia.ui.Card
import com.yunzia.hyperstar.ui.base.MSuperDialog
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.EnableItemSlider
import com.yunzia.hyperstar.utils.SPUtils
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil
import top.yukonga.miuix.kmp.utils.squircleshape.SquircleShape

@Composable
fun EditItem(
    items: MutableState<List<Card>>,
    index: Int,
    item: Card
) {

    val enable = remember { mutableStateOf(SPUtils.getBoolean("edit_span_size_enable",false)) }
    val showDialog = remember { mutableStateOf(false) }
    val spanSize =  remember { mutableFloatStateOf(SPUtils.getFloat("edit_span_size", 4f)) }

    LaunchedEffect(showDialog.value) {
        if (showDialog.value){
            return@LaunchedEffect
        }
        val mutableList = items.value.toMutableList().apply{
            if (enable.value){
                set(index, Card(item.id,item.tag, spanSize.floatValue.toInt(),item.name))

            }else {
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

                Card{
                    EnableItemSlider(
                        key = "edit_span_size",
                        progress = 4f,
                        state = enable,
                        progressState = spanSize
                    )

                }

            }
        }

    }

    Box(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .width(70.dp)
                .padding(vertical = 4.dp, horizontal = 4.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            showDialog.value = true
                        }
                    )
                },
            shape = SquircleShape(18.dp),
            color = colorScheme.secondary,
            shadowElevation = 2f,
            content = {}
        )
        Text(
            text = item.name,
            fontSize = 12.sp,
            fontWeight = FontWeight(550),
            color = colorScheme.onSurfaceVariantSummary
        )
    }
}