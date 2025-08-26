package com.yunzia.hyperstar.ui.screen.module

import android.annotation.SuppressLint
import android.provider.Settings
import android.provider.Settings.Global.ADB_ENABLED
import android.provider.Settings.Global.DEVELOPMENT_SETTINGS_ENABLED
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.SuperGroup
import com.yunzia.hyperstar.ui.component.SuperGroupPosition
import com.yunzia.hyperstar.ui.component.XSuperSwitch
import com.yunzia.hyperstar.ui.component.itemGroup
import com.yunzia.hyperstar.ui.component.pager.NavPager
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.utils.G2RoundedCornerShape

@SuppressLint("StringFormatMatches")
@Composable
fun NotDeveloperScreen(
    navController: NavController,
    currentStartDestination: MutableState<String>,
) {
    val status = stringArrayResource(R.array.status)
    val mContext = navController.context
    val contentResolver = mContext.contentResolver
    val update = remember { mutableStateOf(false) }
    val result = remember { mutableListOf<String>() }

    val text = remember { mutableStateOf("null") }
    LaunchedEffect(update.value) {
        result.apply {
            clear()
            add(
                status[Settings.Global.getInt(contentResolver, DEVELOPMENT_SETTINGS_ENABLED, 0)]
            )
            add(
                status[Settings.Global.getInt(contentResolver, ADB_ENABLED, 0)]
            )
            add(
                status[Settings.Global.getInt(contentResolver, "adb_wifi_enabled", 0)]
            )

        }
        text.value = mContext.getString(
            R.string.notdevelop_content, *result.toTypedArray()
        )
    }

    NavPager(
        activityTitle = stringResource(R.string.not_developer),
        navController = navController,
        parentRoute = currentStartDestination,
    ) {

        item {
            SuperGroup(
                modifier = Modifier.fillMaxWidth(),
                position = SuperGroupPosition.FIRST,
                cardColor = CardDefaults.defaultColors(Color(0x2A0D84FF))
            ){

                Text(
                    text = stringResource(R.string.nodevelop_info),
                    modifier = Modifier.padding(16.dp),
                    color = Color(0xFF0D84FF),
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp
                )
            }
        }

        itemGroup {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ){
                Image(
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(16.dp),
                    painter = painterResource(R.drawable.warning),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.Red)
                )
                Text(
                    stringResource(R.string.nodevelop_warning),
                    fontSize = 13.sp,
                    color = Color.Red
                )

            }
        }
        itemGroup {
            Text(
                text.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                fontWeight = FontWeight.Medium,
                lineHeight = 1.6.em,
            )
        }
        itemGroup(
            position = SuperGroupPosition.LAST
        ) {
            XSuperSwitch(
                title = stringResource(R.string.title_development_settings_enabled),
                key = "development_settings_enabled",
                onStateChanged = {
                    update.value = !update.value
                }
            )

            XSuperSwitch(
                title = stringResource(R.string.title_adb_enabled),
                key = "adb_enabled",
                onStateChanged = {
                    update.value = !update.value
                }
            )

            XSuperSwitch(
                title = stringResource(R.string.title_adb_wifi_enabled),
                key = "adb_wifi_enabled",
                onStateChanged = {
                    update.value = !update.value

                }
            )
        }
    }
}