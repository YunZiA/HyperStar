package com.yunzia.hyperstar.ui.base.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.base.dialog.SuperNotificationUtil.Companion.ShowNotification
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.showDialog
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.utils.getRoundedCorner
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun TopNotification(
    show: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    backgroundColor: Color = SuperDialogDefaults.backgroundColor(),
    onFocus: () -> Unit = {},
    onDismissRequest: (() -> Unit)? = null,
    insideMargin: DpSize = SuperDialogDefaults.insideMargin,
    defaultWindowInsetsPadding: Boolean = true,
    content: @Composable () -> Unit
) {
    if (show.value) {
//        if (!dialogStates.contains(show)) dialogStates.add(show)
//        LaunchedEffect(show.value) {
//            if (show.value) {
//                dialogStates.forEach { state -> if (state != show) state.value = false }
//            }
//        }

        val density = LocalDensity.current
        val getWindowSize by rememberUpdatedState(getWindowSize())
        val windowWidth by rememberUpdatedState(getWindowSize.width.dp / density.density)
        val windowHeight by rememberUpdatedState(getWindowSize.height.dp / density.density)
        val roundedCorner by rememberUpdatedState(getRoundedCorner())//- outsideMargin.width
        val bottomCornerRadius by remember { derivedStateOf { if (roundedCorner != 0.dp) 20.dp  else 32.dp } }
        val maxWidth by remember { derivedStateOf { if (windowHeight >= 480.dp && windowWidth >= 840.dp) 420.dp else  383.2.dp } }

        BackHandler(enabled = show.value) {
            onDismissRequest?.invoke()
        }

        ShowNotification(
            content = {
                Box(
                    modifier = if (defaultWindowInsetsPadding) {
                        Modifier
                            .statusBarsPadding()
                    } else {
                        Modifier
                    }
                        .fillMaxSize()
                        .padding(top = 12.dp)
                        //.then(paddingModifier)
                ) {
                    Column(
                        modifier = modifier
                            .widthIn(max = maxWidth)
                            .pointerInput(Unit) {
                                detectTapGestures {

                                    onFocus()/* Do nothing to consume the click */


                                }
                            }
                            .align(Alignment.TopCenter)
                            .graphicsLayer(
                                shape = SmoothRoundedCornerShape(bottomCornerRadius, 0.5f),
                                clip = true
                            )
                            .background(
                                color = backgroundColor,
                                shape = SmoothRoundedCornerShape(bottomCornerRadius, 0.5f)
                            )
                            //.clip(SmoothRoundedCornerShape(bottomCornerRadius, 0.5f))
//                            .padding(
//                                horizontal = insideMargin.width,
//                                vertical = insideMargin.height
//                            ),
                    ) {

                        content()
                    }
                }
            }
        )
    }
}