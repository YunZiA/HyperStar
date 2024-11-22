package com.yunzia.hyperstar.ui.base

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.utils.getWindowSize




@Composable
fun MSuperDialog(
    title: String? = null,
    titleColor: Color = colorScheme.onSurface,
    summary: String? = null,
    summaryColor: Color = colorScheme.surfaceVariant,
    show: MutableState<Boolean>,
    showAction : Boolean = false,
    color: Color = colorScheme.surfaceVariant,
    onFocus: () -> Unit = {},
    onDismissRequest: () -> Unit,
    insideMargin: DpSize? = null,
    content: @Composable () -> Unit
) {

    @Suppress("NAME_SHADOWING")
    val insideMargin = remember { insideMargin } ?: remember { DpSize(14.dp, 12.dp) }
    val view = LocalView.current
    val paddingModifier = remember(insideMargin) {
        Modifier
            .padding(horizontal = insideMargin.width)
            .padding(bottom = insideMargin.height)
    }
    val getWindowSize by rememberUpdatedState(getWindowSize())
    val contentAlignment by remember { derivedStateOf { if (getWindowSize.width > getWindowSize.height) Alignment.Center else Alignment.BottomCenter } }

    BackHandler(enabled = show.value) {
        dismissDialog(show)
        onDismissRequest()
    }

    Box(
        modifier = Modifier
            .navigationBarsPadding()
            .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
            .windowInsetsPadding(WindowInsets.captionBar.only(WindowInsetsSides.Top))
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    onDismissRequest()
                })
            }
            .then(paddingModifier)
    ) {
        Column(
            modifier = Modifier
                .then(
                    if (contentAlignment != Alignment.Center) Modifier.fillMaxWidth() else Modifier.widthIn(
                        max = 400.dp
                    )
                )
                .pointerInput(Unit) {
                    detectTapGestures {

                        onFocus()/* Do nothing to consume the click */
                    }
                }
                .align(contentAlignment)
                .background(
                    color = color,
                    shape = SmoothRoundedCornerShape(25.dp,0.8f)
                    // SquircleShape(45.dp, cornerSmoothing = CornerSmoothing.High)
                )
                .padding(24.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ){
                    if (showAction){

                        IconButton(
                            modifier = Modifier
                                .padding(bottom = 20.dp),
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                dismissDialog(show)
                                onDismissRequest()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "back",
                                tint = colorScheme.onBackground
                            )
                        }

                    }

                    Column(Modifier.fillMaxWidth()){
                        title?.let {
                            Text(
                                modifier = Modifier
                                    .padding(bottom = 20.dp)
                                    .fillMaxWidth(),
                                text = it,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                color = titleColor
                            )
                        }
                        summary?.let {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 20.dp),
                                text = it,
                                textAlign = TextAlign.Center,
                                color = summaryColor
                            )
                        }

                    }

                }


            }


            content()
        }
    }
}





