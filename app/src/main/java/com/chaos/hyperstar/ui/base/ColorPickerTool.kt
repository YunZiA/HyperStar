package com.chaos.hyperstar.ui.base

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColor
import chaos.colorpicker.ColorPickerDialog
import chaos.colorpicker.colorFromHex
import chaos.colorpicker.toHex
import com.chaos.hyperstar.R
import com.chaos.hyperstar.utils.SPUtils
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.createRipple
import top.yukonga.miuix.kmp.utils.squircleshape.CornerSmoothing
import top.yukonga.miuix.kmp.utils.squircleshape.SquircleShape

@Composable
fun ColorPickerTool(
    title:String,
    dfColor:Color,
    key : String

) {

    val interactionSource =  remember { MutableInteractionSource() }
    val indication = createRipple()
    val showDialog = remember { mutableStateOf(false) }
    val color = remember {  mutableStateOf(SPUtils.getString(key,dfColor.toHex()).colorFromHex()) }
    val insideMargin = remember { DpSize(24.dp, 16.dp) }
    val paddingModifier = remember(insideMargin) {
        Modifier.padding(horizontal = insideMargin.width, vertical = insideMargin.height)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = indication
            ) {
                showDialog.value = true
            }
            .then(paddingModifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurface
        )


        Box(
            modifier = Modifier
                .graphicsLayer(
                    shadowElevation = 12f,
                    shape = RoundedCornerShape(30.dp),
                    clip = false
                )
                .border(3.dp, Color.White, RoundedCornerShape(30.dp))
                .size(30.dp)
                .clip(RoundedCornerShape(30.dp)),
            contentAlignment = Alignment.Center

        ){
            Image(
                modifier = Modifier
                    .size(27.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .border(
                        3.dp,
                        if (color.value == colorScheme.surfaceVariant) colorScheme.secondaryContainer else Color.Transparent,
                        RoundedCornerShape(30.dp)
                    ),
                imageVector = ImageVector.vectorResource(R.drawable.transparent),
                colorFilter = ColorFilter.tint(
                    color.value,
                    BlendMode.SrcOver
                ),
                contentDescription = "ColorImage"
            )
        }


    }

    ColorPickerDialog(
        title = title,
        fColor = color.value,
        showDialog = showDialog
    ) {
        color.value = it
        SPUtils.setString("list_enabled_color",color.value.toHex())

    }
}