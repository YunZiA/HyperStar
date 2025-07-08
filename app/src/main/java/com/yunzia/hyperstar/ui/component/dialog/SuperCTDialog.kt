package com.yunzia.hyperstar.ui.component.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.MiuixPopupUtils.Companion.DialogLayout
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.utils.getRoundedCorner
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun SuperCTDialog(
    show: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    title: String? = null,
    titleColor: Color = SuperCTDialogDefaults.titleColor(),
    summary: String? = null,
    summaryColor: Color = SuperCTDialogDefaults.summaryColor(),
    backgroundColor: Color = SuperCTDialogDefaults.backgroundColor(),
    onFocus: () -> Unit = {},
    onDismissRequest: (() -> Unit)? = null,
    outsideMargin: DpSize = SuperCTDialogDefaults.outsideMargin,
    insideMargin: DpSize = SuperCTDialogDefaults.insideMargin,
    defaultWindowInsetsPadding: Boolean = true,
    content: @Composable () -> Unit
) {

    val density = LocalDensity.current
    val getWindowSize by rememberUpdatedState(getWindowSize())
    val windowWidth by rememberUpdatedState(getWindowSize.width.dp / density.density)
    val windowHeight by rememberUpdatedState(getWindowSize.height.dp / density.density)
    val paddingModifier = remember(outsideMargin) { Modifier.padding(horizontal = outsideMargin.width).padding(bottom = outsideMargin.height) }
    val roundedCorner by rememberUpdatedState(getRoundedCorner())//- outsideMargin.width
    val bottomCornerRadius by remember { derivedStateOf { if (roundedCorner != 0.dp) roundedCorner-5.dp  else 32.dp } }
    val maxWidth by remember { derivedStateOf { if (windowHeight >= 480.dp && windowWidth >= 840.dp) 420.dp else  383.2.dp } }
    val contentAlignment by rememberUpdatedState { derivedStateOf { if (windowHeight >= 480.dp && windowWidth >= 840.dp) Alignment.Center else Alignment.Center } }

    BackHandler(enabled = show.value) {
        onDismissRequest?.invoke()
    }

    DialogLayout(
        visible = show,
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                onDismissRequest?.invoke()
                            }
                        )
                    }
                    .then(paddingModifier)
            ) {
                Column(
                    modifier = modifier
                        .widthIn(max = maxWidth)
                        .pointerInput(Unit) {
                            detectTapGestures {

                                onFocus()/* Do nothing to consume the click */


                            }
                        }
                        .align(contentAlignment.invoke().value)
                        .graphicsLayer(
                            shape = SmoothRoundedCornerShape(bottomCornerRadius,0.5f),
                            clip = false
                        )
                        .background(
                            color = backgroundColor,
                            shape = SmoothRoundedCornerShape(bottomCornerRadius,0.5f)
                        )
                        .padding(
                            horizontal = insideMargin.width,
                            vertical = insideMargin.height
                        ),
                ) {
                    title?.let {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            text = it,
                            fontSize = MiuixTheme.textStyles.title4.fontSize,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            color = titleColor
                        )
                    }
                    summary?.let {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            text = it,
                            fontSize = MiuixTheme.textStyles.body1.fontSize,
                            textAlign = TextAlign.Center,
                            color = summaryColor
                        )
                    }
                    content()
                }
            }
        }
    )
}


object SuperCTDialogDefaults {

    /**
     * The default color of the title.
     */
    @Composable
    fun titleColor() = MiuixTheme.colorScheme.onSurface

    /**
     * The default color of the summary.
     */
    @Composable
    fun summaryColor() = MiuixTheme.colorScheme.onSurfaceSecondary

    /**
     * The default background color of the [SuperDialog].
     */
    @Composable
    fun backgroundColor() = MiuixTheme.colorScheme.surfaceVariant

    /**
     * The default margin outside the [SuperDialog].
     */
    val outsideMargin = DpSize(12.dp, 12.dp)

    /**
     * The default margin inside the [SuperDialog].
     */
    val insideMargin = DpSize(24.dp, 24.dp)
}

/**
 * Only one dialog is allowed to be displayed at a time.
 */
val ctdialogStates = mutableStateListOf<MutableState<Boolean>>()
