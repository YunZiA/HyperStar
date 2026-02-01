package com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.item

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.component.dialog.CancelBottomSheet
import com.yunzia.hyperstar.ui.component.modifier.elevation
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.EnableItemDropdown
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.WindowBottomSheetDefaults
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import yunzia.ui.Card

@Composable
fun MediaItem(
    item: Card,
) {

    val showDialog = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .height(160.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        showDialog.value = true
                    }
                )
            }
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .elevation(
                shape = RoundedCornerShape(18.dp),
                backgroundColor = colorScheme.secondary,
                shadowElevation = 2f
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = item.name,
            fontWeight = FontWeight(550),
            color = colorScheme.onSurfaceVariantSummary
        )
    }



    CancelBottomSheet(
        title = item.name,
        show = showDialog,
        onDismissRequest = {
            showDialog.value = false
        }
    ) {

        Card(
            modifier = Modifier.navigationBarsPadding().padding(bottom = WindowBottomSheetDefaults.insideMargin.width),
            colors = CardDefaults.defaultColors(colorScheme.secondaryContainer)
        ) {
            EnableItemDropdown(
                key = "media_land_rightOrLeft",
                dfOpt = 1
            )

        }

    }


}