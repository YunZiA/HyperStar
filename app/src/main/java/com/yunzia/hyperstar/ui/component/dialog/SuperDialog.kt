package com.yunzia.hyperstar.ui.component.dialog

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.shapes.SmoothRoundedCornerShape
import com.yunzia.hyperstar.ui.component.helper.getWindowSize
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.utils.MiuixPopupUtils.Companion.DialogLayout
import top.yukonga.miuix.kmp.utils.getRoundedCorner

/**
 * A dialog with a title, a summary, and other contents.
 *
 * @param show The show state of the [OverlayDialog].
 * @param modifier The modifier to be applied to the [OverlayDialog].
 * @param title The title of the [OverlayDialog].
 * @param titleColor The color of the title.
 * @param summary The summary of the [OverlayDialog].
 * @param summaryColor The color of the summary.
 * @param backgroundColor The background color of the [OverlayDialog].
 * @param onDismissRequest The callback when the [OverlayDialog] is dismissed.
 * @param outsideMargin The margin outside the [OverlayDialog].
 * @param insideMargin The margin inside the [OverlayDialog].
 * @param defaultWindowInsetsPadding Whether to apply default window insets padding to the [OverlayDialog].
 * @param content The [Composable] content of the [OverlayDialog].
 */
@Composable
fun OverlayDialog(
    show: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    title: String? = null,
    titleColor: Color = OverlayDialogDefaults.titleColor(),
    summary: String? = null,
    summaryColor: Color = OverlayDialogDefaults.summaryColor(),
    backgroundColor: Color = OverlayDialogDefaults.backgroundColor(),
    onFocus: () -> Unit = {},
    onDismissRequest: (() -> Unit)? = null,
    outsideMargin: DpSize = OverlayDialogDefaults.outsideMargin,
    insideMargin: DpSize = OverlayDialogDefaults.insideMargin,
    navigationBarsPadding: Boolean = true,
    imePadding:Boolean = true,
    content: @Composable () -> Unit
) {

    val density = LocalDensity.current
    val getWindowSize by rememberUpdatedState(getWindowSize())
    val windowWidth by rememberUpdatedState(getWindowSize.width)
    val windowHeight by rememberUpdatedState(getWindowSize.height)
    val paddingModifier = remember(outsideMargin) { Modifier.padding(horizontal = outsideMargin.width).padding(bottom = outsideMargin.height) }
    val roundedCorner by rememberUpdatedState(getRoundedCorner())//- outsideMargin.width
    val bottomCornerRadius by remember { derivedStateOf { if (roundedCorner != 0.dp) roundedCorner-5.dp  else 32.dp } }
    val maxWidth by remember { derivedStateOf { if (windowHeight >= 480.dp && windowWidth >= 840.dp) 420.dp else  383.2.dp } }
    val contentAlignment by rememberUpdatedState { derivedStateOf { if (windowHeight >= 480.dp && windowWidth >= 840.dp) Alignment.Center else Alignment.BottomCenter } }

    BackHandler(enabled = show.value) {
        onDismissRequest?.invoke()
    }

    DialogLayout(
        visible = show,
    ) {
        Box(
            modifier =
                Modifier.then(if (navigationBarsPadding) Modifier.navigationBarsPadding() else Modifier)
                    .then(if (imePadding) Modifier.imePadding() else Modifier).fillMaxSize()
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
                        shape = SmoothRoundedCornerShape(bottomCornerRadius),
                        clip = false
                    )
                    .background(
                        color = backgroundColor,
                        shape = SmoothRoundedCornerShape(bottomCornerRadius)
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

}

@Composable
fun OverlayDialogs(
    show: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    title: String? = null,
    titleColor: Color = OverlayDialogDefaults.titleColor(),
    summary: String? = null,
    summaryColor: Color = OverlayDialogDefaults.summaryColor(),
    backgroundColor: Color = OverlayDialogDefaults.backgroundColor(),
    onFocus: () -> Unit = {},
    onDismissRequest: (() -> Unit)? = null,
    outsideMargin: DpSize = OverlayDialogDefaults.outsideMargin,
    insideMargin: DpSize = OverlayDialogDefaults.insideMargin,
    defaultWindowInsetsPadding: Boolean = true,
    content: @Composable () -> Unit
) {

    val density = LocalDensity.current
    val getWindowSize by rememberUpdatedState(getWindowSize())
    val windowWidth by rememberUpdatedState(getWindowSize.width)
    val windowHeight by rememberUpdatedState(getWindowSize.height)
    val paddingModifier = remember(outsideMargin) { Modifier.padding(horizontal = outsideMargin.width).padding(bottom = outsideMargin.height) }
    val roundedCorner by rememberUpdatedState(getRoundedCorner())//- outsideMargin.width
    val bottomCornerRadius by remember { derivedStateOf { if (roundedCorner != 0.dp) roundedCorner-5.dp  else 32.dp } }
    val maxWidth by remember { derivedStateOf { if (windowHeight >= 480.dp && windowWidth >= 840.dp) 420.dp else  383.2.dp } }
    val contentAlignment by rememberUpdatedState { derivedStateOf { if (windowHeight >= 480.dp && windowWidth >= 840.dp) Alignment.Center else Alignment.BottomCenter } }

    BackHandler(show.value) {
        onDismissRequest?.invoke()
    }

    DialogLayout(
        visible = show
    ) {
        Box(
            modifier = if (defaultWindowInsetsPadding) {
                Modifier
                    .imePadding()
                    .navigationBarsPadding()
            } else {
                Modifier
            }
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
                        shape = SmoothRoundedCornerShape(bottomCornerRadius),
                        clip = false
                    )
                    .background(
                        color = backgroundColor,
                        shape = SmoothRoundedCornerShape(bottomCornerRadius)
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


}

object OverlayDialogDefaults {

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
     * The default background color of the [OverlayDialog].
     */
    @Composable
    fun backgroundColor() = MiuixTheme.colorScheme.surfaceVariant

    /**
     * The default margin outside the [OverlayDialog].
     */
    val outsideMargin = DpSize(12.dp, 12.dp)

    /**
     * The default margin inside the [OverlayDialog].
     */
    val insideMargin = DpSize(24.dp, 24.dp)
}

/**
 * Only one dialog is allowed to be displayed at a time.
 */
val dialogStates = mutableStateListOf<MutableState<Boolean>>()
