package com.yunzia.hyperstar.ui.module.systemui.controlcenter.item

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.base.dialog.MSuperDialog
import yunzia.ui.Card
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.EnableItemDropdown
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.EnableItemSlider
import com.yunzia.hyperstar.utils.SPUtils
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.squircleshape.SquircleShape


@Composable
fun ListItem(
    item: Card,
) {

    val showDialog = remember { mutableStateOf(false) }
    val spanSize =  remember { mutableFloatStateOf(SPUtils.getFloat("list_span_size", 1f)) }
    val span = remember { mutableIntStateOf(1) }

    val enable = remember { mutableStateOf(SPUtils.getBoolean("list_span_size_enable",false)) }

    LaunchedEffect(showDialog.value) {
        if (showDialog.value){
            return@LaunchedEffect
        }
        span.intValue = if (enable.value) spanSize.floatValue.toInt() else 1

    }

    if (showDialog.value){

        MiuixPopupUtil.showDialog() {
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
                        key = "list_land_rightOrLeft"
                    )

                }

                Card{
                    EnableItemSlider(
                        key = "list_span_size",
                        progress = 1f,
                        state = enable,
                        progressState = spanSize
                    )

                }



            }
        }

    }

    LazyVerticalGrid(modifier = Modifier
        .height(170.dp)
        .fillMaxWidth()
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = {
                    showDialog.value = true
                }
            )
        },
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        userScrollEnabled = false
    ) {
        items(
            8/span.intValue,
            span = {
                GridItemSpan(span.intValue)
            }
        ){
            Box(
                modifier = Modifier
                    .height(81.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .size(81.dp)
                        .padding(vertical = 4.dp, horizontal = 4.dp),
                    shape = SquircleShape(18.dp),
                    color = colorScheme.secondary,
                    shadowElevation = 2f,
                    content = {}
                )
            }
        }
    }
}
