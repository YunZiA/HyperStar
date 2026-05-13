<<<<<<<< HEAD:app/src/main/java/com/yunzia/hyperstar/ui/screen/module/NotDeveloperScreen.kt
package com.yunzia.hyperstar.ui.screen.module
========
package com.yunzia.hyperstar.ui.screen.module.notDeveloper
>>>>>>>> newApi:app/src/main/java/com/yunzia/hyperstar/ui/screen/module/notDeveloper/NotDeveloperScreen.kt

import android.annotation.SuppressLint
import android.provider.Settings
import android.provider.Settings.Global.ADB_ENABLED
import android.provider.Settings.Global.DEVELOPMENT_SETTINGS_ENABLED
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.SuperGroup
import com.yunzia.hyperstar.ui.component.SuperGroupPosition
<<<<<<<< HEAD:app/src/main/java/com/yunzia/hyperstar/ui/screen/module/NotDeveloperScreen.kt
import com.yunzia.hyperstar.ui.component.XSuperSwitch
import com.yunzia.hyperstar.ui.component.itemGroup
import com.yunzia.hyperstar.ui.component.pager.NavPager
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.utils.G2RoundedCornerShape
========
import com.yunzia.hyperstar.ui.component.preference.PreferenceScreen
import com.yunzia.hyperstar.ui.component.preference.core.SwitchPreference
import com.yunzia.hyperstar.ui.component.preference.preferenceGroup
import com.yunzia.hyperstar.prefs.SPUtils
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.ui.navigation.MainRoutes
import SearchRoute
import androidx.activity.compose.LocalActivity
import com.yunzia.hyperstar.MainActivity
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Text
>>>>>>>> newApi:app/src/main/java/com/yunzia/hyperstar/ui/screen/module/notDeveloper/NotDeveloperScreen.kt

@SuppressLint("StringFormatMatches")
@SearchRoute(route = MainRoutes.NotDeveloper::class)
@Composable
<<<<<<<< HEAD:app/src/main/java/com/yunzia/hyperstar/ui/screen/module/NotDeveloperScreen.kt
fun NotDeveloperScreen(
    navController: NavController,
    currentStartDestination: MutableState<String>,
) {
========
fun NotDeveloperScreen() {
>>>>>>>> newApi:app/src/main/java/com/yunzia/hyperstar/ui/screen/module/notDeveloper/NotDeveloperScreen.kt
    val status = stringArrayResource(R.array.status)
    val navController = LocalNavigator.current
    val activity = LocalActivity.current as MainActivity
    val mContext = LocalContext.current
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

    PreferenceScreen(
        title = stringResource(R.string.not_developer),
        navController = navController,
<<<<<<<< HEAD:app/src/main/java/com/yunzia/hyperstar/ui/screen/module/NotDeveloperScreen.kt
        parentRoute = currentStartDestination,
    ) {

        item {
========
        scrollToKey = activity.appViewModel.scrollToKey.value,
        onScrollComplete = { activity.appViewModel.scrollToKey.value = null },
    ) { _, _ ->
        preferenceGroup {
>>>>>>>> newApi:app/src/main/java/com/yunzia/hyperstar/ui/screen/module/notDeveloper/NotDeveloperScreen.kt
            SuperGroup(
                modifier = Modifier.fillMaxWidth(),
                position = SuperGroupPosition.FIRST,
                cardColor = CardDefaults.defaultColors(Color(0x2A0D84FF))
<<<<<<<< HEAD:app/src/main/java/com/yunzia/hyperstar/ui/screen/module/NotDeveloperScreen.kt
            ){

========
            ) {
>>>>>>>> newApi:app/src/main/java/com/yunzia/hyperstar/ui/screen/module/notDeveloper/NotDeveloperScreen.kt
                Text(
                    text = stringResource(R.string.nodevelop_info),
                    modifier = Modifier.padding(16.dp),
                    color = Color(0xFF0D84FF),
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp
                )
            }
        }

<<<<<<<< HEAD:app/src/main/java/com/yunzia/hyperstar/ui/screen/module/NotDeveloperScreen.kt
        itemGroup {
========
        preferenceGroup {
>>>>>>>> newApi:app/src/main/java/com/yunzia/hyperstar/ui/screen/module/notDeveloper/NotDeveloperScreen.kt
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
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
<<<<<<<< HEAD:app/src/main/java/com/yunzia/hyperstar/ui/screen/module/NotDeveloperScreen.kt
        itemGroup {
========
        preferenceGroup {
>>>>>>>> newApi:app/src/main/java/com/yunzia/hyperstar/ui/screen/module/notDeveloper/NotDeveloperScreen.kt
            Text(
                text.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                fontWeight = FontWeight.Medium,
                lineHeight = 1.6.em,
            )
        }
<<<<<<<< HEAD:app/src/main/java/com/yunzia/hyperstar/ui/screen/module/NotDeveloperScreen.kt
        itemGroup(
            position = SuperGroupPosition.LAST
        ) {
            XSuperSwitch(
========
        preferenceGroup {
            var devSettings by remember { mutableStateOf(SPUtils.getBoolean("development_settings_enabled", false)) }
            SwitchPreference(
>>>>>>>> newApi:app/src/main/java/com/yunzia/hyperstar/ui/screen/module/notDeveloper/NotDeveloperScreen.kt
                title = stringResource(R.string.title_development_settings_enabled),
                checked = devSettings,
                onCheckedChange = {
                    devSettings = it
                    SPUtils.putBoolean("development_settings_enabled", it)
                    update.value = !update.value
                }
            )

            var adbEnabled by remember { mutableStateOf(SPUtils.getBoolean("adb_enabled", false)) }
            SwitchPreference(
                title = stringResource(R.string.title_adb_enabled),
                checked = adbEnabled,
                onCheckedChange = {
                    adbEnabled = it
                    SPUtils.putBoolean("adb_enabled", it)
                    update.value = !update.value
                }
            )

            var adbWifi by remember { mutableStateOf(SPUtils.getBoolean("adb_wifi_enabled", false)) }
            SwitchPreference(
                title = stringResource(R.string.title_adb_wifi_enabled),
                checked = adbWifi,
                onCheckedChange = {
                    adbWifi = it
                    SPUtils.putBoolean("adb_wifi_enabled", it)
                    update.value = !update.value
                }
            )
        }
    }
}
