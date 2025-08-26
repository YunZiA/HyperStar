package com.yunzia.hyperstar.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.CapsuleShape
import top.yukonga.miuix.kmp.utils.G2RoundedCornerShape


@Composable
fun MiniTextButton(
    color: Color,
    text: String = "",
    textColor: Color = colorScheme.onBackground,
    fontSize: TextUnit = 12.sp,
    modifier: Modifier = Modifier,
    radius: Dp = 10.dp,
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
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    cornerRadius: Dp = ButtonDefaults.CornerRadius,
    minWidth: Dp = ButtonDefaults.MinWidth,
    minHeight: Dp = ButtonDefaults.MinHeight,
    colors: Color = colorScheme.secondaryVariant,
    insideMargin: PaddingValues = ButtonDefaults.InsideMargin,
    content: @Composable RowScope.() -> Unit
) {

    Surface(
        onClick = {
            onClick()
        },
        enabled = enabled,
        modifier = modifier.semantics { role = Role.Button },
        shape = G2RoundedCornerShape(cornerRadius),
        color = colors
    ) {
        Row(
            Modifier
                .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
                .padding(insideMargin),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}


@Composable
fun TextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    cornerRadius: Dp = ButtonDefaults.CornerRadius,
    minWidth: Dp = ButtonDefaults.MinWidth,
    minHeight: Dp = ButtonDefaults.MinHeight,
    textColor: Color = Color.White,
    colors: Color = colorScheme.secondaryVariant,
    insideMargin: PaddingValues = ButtonDefaults.InsideMargin
) {

    Surface(
        onClick = {
            onClick()
        },
        enabled = enabled,
        modifier = modifier.semantics { role = Role.Button },
        shape = G2RoundedCornerShape(cornerRadius),
        color = colors
    ) {
        Row(
            Modifier
                .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
                .padding(insideMargin),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ){
            Text(
                text,
                modifier = Modifier.padding(horizontal = 12.dp),
                fontSize = 18.sp,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BaseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = colorScheme.primary,
    cornerRadius: Dp = 24.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    val capsuleShape  = G2RoundedCornerShape(cornerRadius)

    val hapticFeedback = LocalHapticFeedback.current

    Surface(
        enabled = enabled,
        modifier = modifier.clip(capsuleShape).semantics { role = Role.Button },
        //interactionSource = interactionSource,
        shape = capsuleShape,
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
    cornerRadius: Dp = 16.dp,
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
        shape = G2RoundedCornerShape(cornerRadius),
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
                modifier = Modifier.fillMaxWidth(),
                color = textColor,
                textAlign = TextAlign.Center,
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