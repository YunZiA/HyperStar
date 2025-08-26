package com.yunzia.hyperstar.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendModeColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.component.modifier.bounceAnim
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.basic.ArrowRight
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
fun ContentFolder(
    title: String,
    content: @Composable (() -> Unit)
) {

    val showContent = remember { mutableStateOf(false) }
    val insideMargin = remember { BasicComponentDefaults.InsideMargin }
    val paddingModifier = remember(insideMargin) {
        Modifier.padding(insideMargin)
    }
    val rotating = animateFloatAsState(if (showContent.value ) 90f else -90f, label = "")
    Row(
        modifier = Modifier.bounceAnim()
            .fillMaxWidth()
            .clickable{
                showContent.value = !showContent.value
            }
            .then(paddingModifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(end = 6.dp),
            text = title,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurface
        )
        Box(
            modifier = Modifier.size(30.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier
                    .size(15.dp)
                    .rotate(rotating.value),
                imageVector = MiuixIcons.Basic.ArrowRight,
                contentDescription = null,
                colorFilter = BlendModeColorFilter(colorScheme.onSurfaceVariantActions, BlendMode.SrcIn),
            )
        }



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