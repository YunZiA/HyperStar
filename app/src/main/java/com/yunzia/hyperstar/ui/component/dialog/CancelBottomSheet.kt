package com.yunzia.hyperstar.ui.component.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.extra.WindowBottomSheet
import top.yukonga.miuix.kmp.extra.WindowBottomSheetDefaults
import top.yukonga.miuix.kmp.icon.extended.Close


@Composable
fun CancelBottomSheet(
    show: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    title: String? = null,
    backgroundColor: Color = colorScheme.surface,
    enableWindowDim: Boolean = true,
    cornerRadius: Dp = WindowBottomSheetDefaults.cornerRadius,
    sheetMaxWidth: Dp = WindowBottomSheetDefaults.maxWidth,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    outsideMargin: DpSize = WindowBottomSheetDefaults.outsideMargin,
    insideMargin: DpSize = WindowBottomSheetDefaults.insideMargin,
    defaultWindowInsetsPadding: Boolean = true,
    dragHandleColor: Color = WindowBottomSheetDefaults.dragHandleColor(),
    allowDismiss: Boolean = true,
    enableNestedScroll: Boolean = true,
    content: @Composable () -> Unit
) {
    WindowBottomSheet(
        show = show,
        modifier = modifier,
        title = title,
        startAction = {
            IconButton(
                onClick = { show.value = false },
            ) {
                Icon(
                    imageVector = MiuixIcons.Close,
                    contentDescription = "Cancel",
                    tint = colorScheme.onBackground,
                )
            }
        },
        backgroundColor = backgroundColor,
        enableWindowDim = enableWindowDim,
        cornerRadius = cornerRadius,
        sheetMaxWidth = sheetMaxWidth,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        outsideMargin = outsideMargin,
        insideMargin = insideMargin,
        defaultWindowInsetsPadding = defaultWindowInsetsPadding,
        dragHandleColor = dragHandleColor,
        allowDismiss = allowDismiss,
        enableNestedScroll = enableNestedScroll,
        content = content
    )
}





