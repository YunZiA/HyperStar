package com.yunzia.hyperstar.ui.screen.pagers

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.preference.widget.IntentPreference
import com.yunzia.hyperstar.ui.component.preference.widget.PreferenceListPage
import com.yunzia.hyperstar.ui.component.preference.widget.itemGroup
import com.yunzia.hyperstar.ui.navigation.LocalNavigator

@Composable
fun ReferencesScreen() {
    val navController = LocalNavigator.current
    val context = LocalContext.current
    PreferenceListPage(
        title = stringResource(R.string.references_title),
        navController = navController,
    ) {
        itemGroup("Apache-2.0") {
            IntentPreference(
                title = "EzXHelper3.0",
                summary = "KyuubiRan",
                context = context,
                url = "https://github.com/KyuubiRan/EzXHelper/tree/3.x"
            )
            IntentPreference(
                title = "Gson",
                summary = "Android Open Source Project, Google Inc.",
                context = context,
                url = "https://github.com/google/gson"
            )
            IntentPreference(
                title = "Haze",
                summary = "Chris Banes",
                context = context,
                url = "https://github.com/chrisbanes/haze"
            )
            IntentPreference(
                title = "miuix-kotlin-multiplatform",
                summary = "YuKongA",
                context = context,
                url = "https://github.com/miuix-kotlin-multiplatform/miuix"
            )
            IntentPreference(
                title = "OkHttp",
                summary = "squarejesse",
                context = context,
                url = "https://github.com/square/okhttp"
            )
            IntentPreference(
                title = "Xposed",
                summary = "rovo89,Tungstwenty",
                context = context,
                url = "https://github.com/rovo89/XposedBridge"
            )
            IntentPreference(
                title = "libxposed api",
                summary = "libxposed",
                context = context,
                url = "https://github.com/libxposed/api"
            )
            IntentPreference(
                title = "libxposed service",
                summary = "libxposed",
                context = context,
                url = "https://github.com/libxposed/service"
            )
            IntentPreference(
                title = "XposedBridge",
                summary = "rovo89",
                context = context,
                url = "https://github.com/rovo89/XposedBridge"
            )
        }
        itemGroup("AGPL-3.0") {
            IntentPreference(
                title = "HyperCeiler",
                summary = "ReChronoRain",
                context = context,
                url = "https://github.com/ReChronoRain/HyperCeiler"
            )
        }
        itemGroup("No License") {
            IntentPreference(
                title = "IAmNotADeveloper",
                summary = "ReChronoRain",
                context = context,
                url = "https://github.com/xfqwdsj/IAmNotADeveloper"
            )
        }
    }
}
