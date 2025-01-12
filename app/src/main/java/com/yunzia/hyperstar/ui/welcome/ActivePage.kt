package com.yunzia.hyperstar.ui.welcome

import android.content.Intent
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.Button
import com.yunzia.hyperstar.ui.pagers.dialog.checkApplication
import com.yunzia.hyperstar.utils.Helper.isModuleActive
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
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
fun ActivePage(

) {

    val view = LocalView.current
    val isActive = isModuleActive()
    val mContext = LocalContext.current as MainActivity

    val packageName = "org.lsposed.manager"
    val className = "org.lsposed.manager.ui.activity.MainActivity"

    val go = checkApplication(mContext,packageName)

    val intent = Intent().apply {
        setClassName(packageName,className)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    val clipboardManager = LocalClipboardManager.current
    val hapticFeedback = LocalHapticFeedback.current
    val debugInfo = "Debug Info of HyperStar\n\n" +
            "ModuleActive = ${isModuleActive()}\n" +
            "HookChannel = ${if (isOS2Settings()) "OS2" else "OS1"}\n" +
            "VersionCode = ${getVersionCode(mContext)}\n" +
            "VersionName = ${getVerName(mContext)}\n\n" +
            "MarketName = ${getMarketName()}\n" +
            "DeviceName = ${getDeviceName()}\n" +
            "isFold = ${isFold()}\n" +
            "isPad = ${isPad()}\n" +
            "AndroidVersion = ${getAndroidVersion()}\n" +
            "HyperOSVersion = ${getOSVersion()}\n" +
            "IsBetaVersion = ${isBetaOs()}\n" +
            "SystemVersion = ${getSystemVersionIncremental()}"
    val debugInfoString = buildAnnotatedString {
        withStyle(SpanStyle(color = colorScheme.onSurface)) {
            append(debugInfo)
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 26.dp)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Box(
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Icon(
                ImageVector.vectorResource(R.drawable.no_active_warning),
                contentDescription = "language",
                modifier = Modifier.size(90.dp),
                tint = Color.Red
//                Color(0xFF3482FF)
            )

        }

        Text(
            stringResource(R.string.not_activated_toast_description),
            modifier = Modifier.padding(top = 20.dp),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            stringResource(R.string.no_active_des_sum),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp).padding(horizontal = 10.dp),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 15.dp, bottom = 6.dp)
                .clip(SmoothRoundedCornerShape(21.dp, 0.5f))
                .background(colorScheme.surface)
            ,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.welcome_debug_info_tip),
                modifier = Modifier.padding( vertical = 16.dp, horizontal = 24.dp),
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp
            )
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 10.dp),
        ) {

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
                        .padding(top = 8.dp),
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




        }
        if (go){
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 28.dp)
                    .padding(bottom = 10.dp),
                //colors = Color(0xFF3482FF),
                onClick = {

                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    mContext.startActivity(intent)

                }
            ) {
                Text(
                    text = stringResource(R.string.open_lsp_manager),
                    modifier = Modifier.padding(horizontal = 12.dp),
                    fontSize = 18.sp,
                    color = colorScheme.onSecondaryVariant,
                    fontWeight = FontWeight.Bold
                )
            }


        }


        Button(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 28.dp),
            colors = Color(0xFF3482FF),
            onClick = {

                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                android.os.Process.killProcess(android.os.Process.myPid())

            }
        ) {
            Text(
                stringResource(R.string.exit),
                modifier = Modifier,
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

    }


}