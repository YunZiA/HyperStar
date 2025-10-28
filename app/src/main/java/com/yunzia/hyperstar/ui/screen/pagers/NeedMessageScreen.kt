package com.yunzia.hyperstar.ui.screen.pagers

import android.content.ClipData
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
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
import com.yunzia.hyperstar.ui.component.pager.NavPager
import com.yunzia.hyperstar.utils.Helper.isModuleActive
import com.yunzia.hyperstar.utils.OSVersion
import com.yunzia.hyperstar.utils.androidVersion
import com.yunzia.hyperstar.utils.deviceName
import com.yunzia.hyperstar.utils.getHookChannel
import com.yunzia.hyperstar.utils.getSettingChannel
import com.yunzia.hyperstar.utils.marketName
import com.yunzia.hyperstar.utils.systemVersionIncremental
import com.yunzia.hyperstar.utils.getVerName
import com.yunzia.hyperstar.utils.getVersionCode
import com.yunzia.hyperstar.utils.isBetaOS
import com.yunzia.hyperstar.utils.isFold
import com.yunzia.hyperstar.utils.isPad
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.G2RoundedCornerShape

@Composable
fun NeedMessageScreen(
    navController: NavController,
    currentStartDestination: MutableState<String>,
) {
    val context = LocalContext.current
    val debugInfo = "Debug Info of HyperStar\n\n" +
            "ModuleActive = ${isModuleActive()}\n" +
            "HookChannel =  OS${getSettingChannel()}\n" +
            "VersionCode = ${getVersionCode(context)}\n" +
            "VersionName = ${getVerName(context)}\n\n" +
            "MarketName = $marketName\n" +
            "DeviceName = $deviceName\n" +
            "isFold = ${isFold()}\n" +
            "isPad = ${isPad()}\n" +
            "AndroidVersion = $androidVersion\n" +
            "HyperOSVersion = $OSVersion\n" +
            "IsBetaVersion = $isBetaOS\n" +
            "SystemVersion = $systemVersionIncremental"


    val localClipboard = LocalClipboard.current
    val debugInfoString = buildAnnotatedString {
        withStyle(SpanStyle(color = colorScheme.onSurface)) {
            append(debugInfo)
        }
    }

    val clipData by lazy { ClipData.newPlainText("text/plain", debugInfoString) }
    val clipEntry by lazy { ClipEntry(clipData) }
    val needCopy = remember { mutableStateOf(false) }

    val hapticFeedback = LocalHapticFeedback.current

    LaunchedEffect(needCopy.value) {
        if (!needCopy.value) return@LaunchedEffect
        localClipboard.setClipEntry(clipEntry)
        needCopy.value = false

    }

    NavPager(
        activityTitle = stringResource(R.string.debug_message),
        navController = navController,
        parentRoute = currentStartDestination
    ) {

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clip(G2RoundedCornerShape(21.dp))
                    .background(Color(0x2A0D84FF))
                    ,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.debug_message_header_text),
                    modifier = Modifier.padding( 16.dp),
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
                                needCopy.value = true
                            },
                            onDoubleTap = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                needCopy.value = true

                            }
                        )
                    }
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
                insideMargin = PaddingValues(16.dp),
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


    }
}