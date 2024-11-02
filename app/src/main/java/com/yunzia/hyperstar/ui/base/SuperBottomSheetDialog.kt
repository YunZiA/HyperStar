package com.yunzia.hyperstar.ui.base

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.utils.getWindowSize
import top.yukonga.miuix.kmp.utils.squircleshape.CornerSmoothing
import top.yukonga.miuix.kmp.utils.squircleshape.SquircleShape

@Composable
fun SuperBottomSheetDialog(
    title: String? = null,
    titleColor: Color = MiuixTheme.colorScheme.onSurface,
    summary: String? = null,
    summaryColor: Color = MiuixTheme.colorScheme.surfaceVariant,
    show: MutableState<Boolean>,
    onFocus: () -> Unit = {},
    onDismissRequest: () -> Unit,
    insideMargin: DpSize? = null,
    content: @Composable () -> Unit
) {
    @Suppress("NAME_SHADOWING")
    val insideMargin = remember { insideMargin } ?: remember { DpSize(14.dp, 14.dp) }

    val paddingModifier = remember(insideMargin) {
        Modifier.padding(horizontal = insideMargin.width).padding(bottom = insideMargin.height)
    }
    val getWindowSize by rememberUpdatedState(getWindowSize())
    val contentAlignment by remember { derivedStateOf { if (getWindowSize.width > getWindowSize.height) Alignment.Center else Alignment.BottomCenter } }

    BackHandler(enabled = show.value) {
        XPopupUtil.dismissDialog()
        onDismissRequest()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    XPopupUtil.dismissDialog()
                    onDismissRequest()
                })
            }
        //.then(paddingModifier)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures {

                        onFocus()/* Do nothing to consume the click */
                    }
                }
                .align(Alignment.BottomCenter)
                .graphicsLayer(
                    shadowElevation = 12f,
                    shape = SmoothRoundedCornerShape(36.dp,0.8f),
                    clip = false
                )
                .background(
                    color = MiuixTheme.colorScheme.background,
                    shape = SmoothRoundedCornerShape(36.dp,0.8f)
                )
                .padding(24.dp),
        ) {
            title?.let {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    text = it,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = titleColor
                )
            }
            summary?.let {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    text = it,
                    textAlign = TextAlign.Center,
                    color = summaryColor
                )
            }
            content()
        }
    }
}