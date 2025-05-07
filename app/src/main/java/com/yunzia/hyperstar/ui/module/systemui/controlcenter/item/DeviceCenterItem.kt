package com.yunzia.hyperstar.ui.module.systemui.controlcenter.item

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.component.dialog.MSuperDialog
import com.yunzia.hyperstar.ui.component.modifier.elevation
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.ControlCenterListViewModel
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.EnableItemDropdown
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.EnableItemSlider
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.ItemState
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtils
import top.yukonga.miuix.kmp.utils.MiuixPopupUtils.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.squircleshape.SquircleShape
import yunzia.ui.Card

@Composable
fun DeviceCenterItem(
    index:Int,
    item: Card,
    viewModel: ControlCenterListViewModel
) {

    val itemStates = viewModel.itemStates.collectAsState()
    val dialogState = itemStates.value[item.tag] ?: ItemState.loadFromSP(item.tag)

    val showDialog = remember { mutableStateOf(false) }
    val spanSize = remember(dialogState) {
        mutableFloatStateOf(dialogState.spanSize)
    }
    val enable = remember(dialogState) {
        mutableStateOf(dialogState.enable)
    }

    // 监听本地状态变化并更新 ViewModel
    LaunchedEffect(spanSize.floatValue, enable.value) {
        viewModel.updateItemSpan(index, item,spanSize.floatValue,enable.value)
        viewModel.updateItemDialogState(
            itemTag = item.tag,
            enable = enable.value,
            spanSize = spanSize.floatValue
        )
    }


    if (showDialog.value) {
        MiuixPopupUtils.showDialog(
            show = showDialog
        ) {
            MSuperDialog(
                title = item.name,
                show = showDialog,
                showAction = true,
                onDismissRequest = {
                    dismissDialog(showDialog)
                }
            ) {

                Card(
                    Modifier.padding(bottom = 10.dp),
                    color = colorScheme.secondaryContainer
                ) {
                    EnableItemDropdown(
                        key = "deviceCenter_land_rightOrLeft"
                    )

                }

                Card(
                    color = colorScheme.secondaryContainer
                ) {
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