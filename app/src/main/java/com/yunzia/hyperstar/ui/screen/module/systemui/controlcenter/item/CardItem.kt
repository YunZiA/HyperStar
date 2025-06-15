package com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.item

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.dialog.MSuperDialog
import com.yunzia.hyperstar.ui.component.modifier.elevation
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.ControlCenterListViewModel
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.EnableItemDropdown
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.EnableItemSlider
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.ItemState
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.squircleshape.SquircleShape
import yunzia.ui.Card

@Composable
fun CardItem(
    index: Int,
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
    val name = stringArrayResource(id = R.array.card_tile_name)

    // 监听本地状态变化并更新 ViewModel
    LaunchedEffect(spanSize.floatValue, enable.value) {

        viewModel.updateCardItemSpan(
            index = index,
            item = item,
            spanSize = spanSize.floatValue,
            enable = enable.value
        )
        viewModel.updateItemDialogState(
            itemTag = item.tag,
            enable = enable.value,
            spanSize = spanSize.floatValue
        )
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
        items(
            2,
            span = {
                GridItemSpan(if (spanSize.floatValue.toInt() != 1) spanSize.floatValue.toInt() else 2)
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
                    text = name[it] + "66666666666666666666",
                    modifier = Modifier.padding(horizontal = 20.dp).basicMarquee(),
                    fontSize = 16.sp,
                    maxLines = 1,
                    fontWeight = FontWeight(550),
                    color =  colorScheme.onSurfaceVariantSummary
                )


            }
        }
    }



    MSuperDialog(
        title = item.name,
        show = showDialog,
        showAction = true,
        onDismissRequest = {
            showDialog.value = false
        }
    ) {

        Card(
            Modifier.padding(bottom = 10.dp),
            color = colorScheme.secondaryContainer
        ) {

            EnableItemDropdown(
                key = "cards_land_rightOrLeft",
                dfOpt = 1
            )
        }
        Card(
            color = colorScheme.secondaryContainer
        ) {
            EnableItemSlider(
                key = "cards_span_size",
                progress = 2f,
                state = enable,
                progressState = spanSize
            )

        }
    }
}




