package com.chaos.hyperstar.ui.module.controlcenter.item

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import chaos.ui.Card
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.MSuperDialog
import com.chaos.hyperstar.ui.base.elevation
import com.chaos.hyperstar.ui.module.controlcenter.EnableItemDropdown
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.showDialog
import top.yukonga.miuix.kmp.utils.squircleshape.SquircleShape

@Composable
fun BrightnessItem(
    item: Card,
) {
    val showDialog = remember { mutableStateOf(false) }
    if (showDialog.value){
        showDialog{
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
                    EnableItemDropdown(
                        key = "brightness_land_rightOrLeft",
                        dfOpt = 1
                    )

                }

            }
        }

    }

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
                backgroundColor = colorScheme.background,
                shadowElevation = 2f
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = item.name,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurface
        )
    }

}