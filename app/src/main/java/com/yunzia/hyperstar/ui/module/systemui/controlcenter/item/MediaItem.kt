package com.yunzia.hyperstar.ui.module.systemui.controlcenter.item

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import yunzia.ui.Card
import com.yunzia.hyperstar.ui.base.MSuperDialog
import com.yunzia.hyperstar.ui.base.modifier.elevation
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.EnableItemDropdown
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.showDialog
import top.yukonga.miuix.kmp.utils.squircleshape.SquircleShape

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
                shape = SquircleShape(18.dp),
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

    if (showDialog.value){

        showDialog{
            MSuperDialog(
                title = item.name,
                show = showDialog,
                showAction = true,
                color = colorScheme.background,
                onDismissRequest = {
                    dismissDialog(showDialog)
                }
            ) {

                Card{
                    EnableItemDropdown(
                        key = "media_land_rightOrLeft",
                        dfOpt = 1
                    )

                }

            }
        }

    }
}