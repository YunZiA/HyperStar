package com.yunzia.hyperstar.ui.pagers

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.NavPager
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.utils.Utils.isModuleActive
import com.yunzia.hyperstar.utils.getAndroidVersion
import com.yunzia.hyperstar.utils.getDeviceName
import com.yunzia.hyperstar.utils.getMarketName
import com.yunzia.hyperstar.utils.getOSVersion
import com.yunzia.hyperstar.utils.getSystemVersionIncremental
import com.yunzia.hyperstar.utils.getVerName
import com.yunzia.hyperstar.utils.getVersionCode
import com.yunzia.hyperstar.utils.isBetaOs
import com.yunzia.hyperstar.utils.isFold
import com.yunzia.hyperstar.utils.isOS2Settings
import com.yunzia.hyperstar.utils.isPad
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
fun NeedMessagePager(
    navController: NavController,
    currentStartDestination: MutableState<String>,
) {
    val context = LocalContext.current
    val debugInfo = "Debug Info of HyperStar\n\n" +
            "ModuleActive = ${isModuleActive()}\n" +
            "HookChannel = ${if (isOS2Settings()) "OS2" else "OS1"}\n" +
            "VersionCode = ${getVersionCode(context)}\n" +
            "VersionName = ${getVerName(context)}\n\n" +
            "MarketName = ${getMarketName()}\n" +
            "DeviceName = ${getDeviceName()}\n" +
            "isFold = ${isFold()}\n" +
            "isPad = ${isPad()}\n" +
            "AndroidVersion = ${getAndroidVersion()}\n" +
            "HyperOSVersion = ${getOSVersion()}\n" +
            "IsBetaVersion = ${isBetaOs()}\n" +
            "SystemVersion = ${getSystemVersionIncremental()}"


    val clipboardManager = LocalClipboardManager.current
    val debugInfoString = buildAnnotatedString {
        withStyle(SpanStyle(color = colorScheme.onSurface)) {
            append(debugInfo)
        }
    }

    val hapticFeedback = LocalHapticFeedback.current

    NavPager(
        activityTitle = stringResource(R.string.debug_message),
        navController = navController,
        parentRoute = currentStartDestination
    ) {

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(SmoothRoundedCornerShape(21.dp, 0.5f))
                    .background(Color(0x2A0D84FF))
                    ,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.debug_message_header_text),
                    modifier = Modifier.padding( vertical = 16.dp, horizontal = 24.dp),
                    color = Color(0xFF0D84FF),
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp
                )
            }
        }

        item {
            Card(
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                clipboardManager.setText(debugInfoString)

                            },
                            onDoubleTap = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                clipboardManager.setText(debugInfoString)

                            }
                        )
                    }
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp),
                insideMargin = PaddingValues(24.dp,24.dp),
                cornerRadius = 21.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ){
                    Text(
                        text = debugInfo,
                        color = colorScheme.onSurface,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp
                    )
                }

            }
        }

        classes {

        }

    }
}