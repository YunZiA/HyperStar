package com.yunzia.hyperstar.ui.component.preference.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.BaseButton
import com.yunzia.hyperstar.ui.component.modifier.bounceAnim
import com.yunzia.hyperstar.ui.navigation.Navigator
import com.yunzia.hyperstar.ui.navigation.Route
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.layout.DialogDefaults
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

// =============================================================================
// Arrow-based preference items — standalone @Composable, no PreferenceScope dependency
// =============================================================================

/**
 * A preference that opens external URLs via [Intent.ACTION_VIEW].
 * When [urls] is provided, each URL is tried in order until one succeeds.
 */
@Composable
fun IntentPreference(
    title: String,
    context: Context,
    url: String,
    summary: String? = null,
    icon: Int? = null,
    modifier: Modifier = Modifier,
) = IntentPreference(
    title = title,
    summary = summary,
    icon = icon,
    context = context,
    urls = arrayOf(url),
    modifier = modifier,
)

@Composable
fun IntentPreference(
    title: String,
    context: Context,
    urls: Array<String>,
    summary: String? = null,
    icon: Int? = null,
    modifier: Modifier = Modifier,
) {
    ArrowPreference(
        modifier = modifier.bounceAnim(),
        startAction = icon?.let {
            { Row { Image(painterResource(it), title, modifier = Modifier.size(35.dp)); Spacer(Modifier.width(6.dp)) } }
        },
        title = title,
        summary = summary,
        onClick = {
            urls.forEach { u ->
                try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(u))); return@forEach }
                catch (_: Exception) {}
            }
        },
    )
}

/**
 * A preference that navigates to a [route] via [navController].
 */
@Composable
fun NavigatePreference(
    title: String,
    navController: Navigator,
    route: Route,
    summary: String? = null,
    icon: Int? = null,
    endText: String? = null,
    modifier: Modifier = Modifier,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
) {
    ArrowPreference(
        modifier = modifier.bounceAnim(),
        startAction = icon?.let {
            { Row { Image(painterResource(it), title, modifier = Modifier.size(30.dp)); Spacer(Modifier.width(6.dp)) } }
        },
        title = title,
        summary = summary,
        insideMargin = insideMargin,
        endActions = {
            endText?.let {
                Text(
                    text = it,
                    modifier = Modifier.align(Alignment.CenterVertically).weight(1f, fill = false),
                    fontSize = MiuixTheme.textStyles.body2.fontSize,
                    color = colorScheme.onSurfaceVariantActions,
                    textAlign = TextAlign.End,
                )
            }
        },
        onClick = { navController.navigate(route) },
    )
}

@Composable
fun NavigatePreference(
    title: String,
    navController: Navigator,
    route: Route,
    summary: String?,
    icon: ImageBitmap,
    endText: String? = null,
    modifier: Modifier = Modifier,
) {
    ArrowPreference(
        modifier = modifier.bounceAnim(),
        startAction = {
            Row { Image(bitmap = icon, contentDescription = title, modifier = Modifier.size(30.dp)); Spacer(Modifier.width(6.dp)) }
        },
        title = title,
        summary = summary,
        endActions = {
            endText?.let {
                Text(
                    text = it,
                    modifier = Modifier.align(Alignment.CenterVertically).weight(1f, fill = false),
                    fontSize = MiuixTheme.textStyles.body2.fontSize,
                    color = colorScheme.onSurfaceVariantActions,
                    textAlign = TextAlign.End,
                )
            }
        },
        onClick = { navController.navigate(route) },
    )
}

@Composable
fun NavigatePreference(
    title: String,
    navController: Navigator,
    route: Route,
    summary: String?,
    icon: Painter,
    endText: String? = null,
    modifier: Modifier = Modifier,
) {
    ArrowPreference(
        modifier = modifier.bounceAnim(),
        startAction = {
            Row { Image(painter = icon, contentDescription = title, modifier = Modifier.size(30.dp)); Spacer(Modifier.width(6.dp)) }
        },
        title = title,
        summary = summary,
        endActions = {
            endText?.let {
                Text(
                    text = it,
                    modifier = Modifier.align(Alignment.CenterVertically).weight(1f, fill = false),
                    fontSize = MiuixTheme.textStyles.body2.fontSize,
                    color = colorScheme.onSurfaceVariantActions,
                    textAlign = TextAlign.End,
                )
            }
        },
        onClick = { navController.navigate(route) },
    )
}

/**
 * A preference that launches an Android Activity.
 */
@Composable
fun ActivityPreference(
    title: String,
    context: Context,
    activity: Class<*>,
    summary: String? = null,
    icon: Int? = null,
    modifier: Modifier = Modifier,
) {
    ArrowPreference(
        modifier = modifier.bounceAnim(),
        startAction = icon?.let {
            { Row { Image(painterResource(it), title, modifier = Modifier.size(35.dp)); Spacer(Modifier.width(6.dp)) } }
        },
        title = title,
        summary = summary,
        onClick = { context.startActivity(Intent(context, activity)) },
    )
}

/**
 * A preference that shows a warning dialog before executing [onSure].
 */
@Composable
fun WarnDialogPreference(
    title: String,
    summary: String? = null,
    icon: Int? = null,
    warnTitle: String? = stringResource(R.string.warning),
    warnDes: String = "",
    modifier: Modifier = Modifier,
    onSure: () -> Unit,
) {
    val show = remember { mutableStateOf(false) }

    ArrowPreference(
        modifier = modifier.bounceAnim(),
        startAction = icon?.let {
            { Row { Image(painterResource(it), title, modifier = Modifier.size(35.dp)); Spacer(Modifier.width(6.dp)) } }
        },
        title = title,
        summary = summary,
        onClick = { show.value = true },
    )

    OverlayDialog(
        title = warnTitle,
        show = show.value,
        onDismissRequest = { show.value = false },
    ) {
        Text(
            warnDes,
            Modifier.padding(horizontal = 5.dp).padding(top = 8.dp, bottom = 24.dp),
            color = DialogDefaults.summaryColor(),
            textAlign = TextAlign.Start,
            fontSize = 16.sp,
        )
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            BaseButton(
                text = stringResource(R.string.cancel),
                modifier = Modifier.weight(1f),
                onClick = { show.value = false },
            )
            Spacer(Modifier.width(20.dp))
            BaseButton(
                text = stringResource(R.string.sure),
                modifier = Modifier.weight(1f),
                submit = true,
                onClick = {
                    show.value = false
                    onSure()
                },
            )
        }
    }
}
