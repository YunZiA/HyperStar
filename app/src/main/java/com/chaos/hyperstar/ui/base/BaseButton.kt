package com.chaos.hyperstar.ui.base

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.squircleshape.CornerSmoothing
import top.yukonga.miuix.kmp.utils.squircleshape.SquircleShape


@Composable
fun MiniTextButton(
    color: Color,
    text: String = "",
    textColor: Color = colorScheme.onBackground,
    fontSize: TextUnit = 12.sp,
    modifier: Modifier = Modifier,
    radius: Dp = 18.dp,
    onClick: () -> Unit,
) {

    BaseButton(
        modifier = modifier,
        color = color,
        cornerRadius = radius,
        onClick = {
            onClick()
        }
    ) {
        Text(
            color = textColor,
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
            text = text
        )
    }
}

@Composable
fun BaseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = colorScheme.primary,
    cornerRadius: Dp = 30.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {

    val hapticFeedback = LocalHapticFeedback.current

    Surface(
        enabled = enabled,
        modifier = modifier.clip(SquircleShape(cornerRadius,CornerSmoothing.Medium)).semantics { role = Role.Button },
        //interactionSource = interactionSource,
        shape = SquircleShape(cornerRadius,CornerSmoothing.Medium),
        color = color,
        onClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
    ) {
        Row(
            //Modifier.padding(16.dp, 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()

        }
    }
}

@Composable
fun BaseButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    submit: Boolean = false,
    cornerRadius: Dp = 30.dp,
    interactionSource: MutableInteractionSource? = null
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val hapticFeedback = LocalHapticFeedback.current
    val color by rememberUpdatedState(getButtonColor(enabled, submit))
    val textColor by rememberUpdatedState(getTextColor(enabled, submit))

    Surface(
        onClick = {
            onClick()
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        enabled = enabled,
        modifier = modifier.semantics { role = Role.Button },
        shape = SquircleShape(cornerRadius,CornerSmoothing.High),
        color = color
    ) {
        Row(
            Modifier
                .defaultMinSize(minWidth = 58.dp, minHeight = 40.dp)
                .padding(16.dp, 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = text,
                color = textColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun getButtonColor(enabled: Boolean, submit: Boolean): Color {
    return if (enabled) {
        if (submit) colorScheme.primary else colorScheme.secondaryVariant
    } else {
        if (submit) colorScheme.disabledPrimaryButton else colorScheme.disabledSecondaryVariant
    }
}

@Composable
private fun getTextColor(enabled: Boolean, submit: Boolean): Color {
    return if (enabled) {
        if (submit) colorScheme.onPrimary else colorScheme.onSecondaryVariant
    } else {
        if (submit) colorScheme.disabledOnPrimaryButton else colorScheme.disabledOnSecondaryVariant
    }
}