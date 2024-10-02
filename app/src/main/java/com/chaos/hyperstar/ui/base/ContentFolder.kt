package com.chaos.hyperstar.ui.base

import android.icu.text.CaseMap.Title
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MovableContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendModeColorFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.swipeable
import com.chaos.hyperstar.R
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowRight
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.createRipple

@Composable
fun ContentFolder(
    title: String,
    content: @Composable (() -> Unit)
) {


    val view = LocalView.current
    val interactionSource =  remember { MutableInteractionSource() }
    val indication = createRipple()
    val showContent = remember { mutableStateOf(false) }
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
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                showContent.value = !showContent.value
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
        Image(
            modifier = Modifier
                .size(15.dp)
                .rotate(90f)
                .padding(start = 6.dp),
            imageVector = MiuixIcons.ArrowRight,
            contentDescription = null,
            colorFilter = BlendModeColorFilter(colorScheme.onSurfaceVariantActions, BlendMode.SrcIn),
        )

//        Box(
//            modifier = Modifier
//                .graphicsLayer(
//                    shadowElevation = 12f,
//                    shape = RoundedCornerShape(30.dp),
//                    clip = false
//                )
//                .border(3.dp, Color.White, RoundedCornerShape(30.dp))
//                .size(30.dp)
//                .clip(RoundedCornerShape(30.dp)),
//            contentAlignment = Alignment.Center
//
//        ) {
//            Image(
//                modifier = Modifier
//                    .size(27.dp)
//                    .clip(RoundedCornerShape(30.dp))
//                    .border(
//                        3.dp,
//                        if (color.value == colorScheme.surfaceVariant) colorScheme.secondaryContainer else Color.Transparent,
//                        RoundedCornerShape(30.dp)
//                    ),
//                imageVector = ImageVector.vectorResource(R.drawable.transparent),
//                colorFilter = ColorFilter.tint(
//                    color.value,
//                    BlendMode.SrcOver
//                ),
//                contentDescription = "ColorImage"
//            )
//        }
    }

    AnimatedVisibility (
        showContent.value,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column{
            content()

        }
    }
}