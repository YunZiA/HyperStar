package com.chaos.hyperstar.ui.module.systemui.controlcenter.item

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import chaos.ui.Card
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.MSuperDialog
import com.chaos.hyperstar.ui.base.modifier.elevation
import com.chaos.hyperstar.ui.module.systemui.controlcenter.EnableItemDropdown
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
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
                backgroundColor = colorScheme.secondary,
                shadowElevation = 2f
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .background(colorScheme.surface, SquircleShape(8.dp)),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Icon(
                ImageVector.vectorResource(R.drawable.ic_brightness_slider),
                modifier = Modifier
                    .size(30.dp),
                contentDescription = "back",
                tint = colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(15.dp))
        }


//        Text(
//            text = item.name,
//            fontWeight = FontWeight(550),
//            textAlign = TextAlign.Center,
//            color = colorScheme.onSurface
//        )
    }

}