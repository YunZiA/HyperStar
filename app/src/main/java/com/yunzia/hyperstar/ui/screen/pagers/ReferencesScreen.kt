package com.yunzia.hyperstar.ui.screen.pagers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.SuperGroupPosition
import com.yunzia.hyperstar.ui.component.SuperIntentArrow
import com.yunzia.hyperstar.ui.component.itemGroup
import com.yunzia.hyperstar.ui.component.pager.NavPager
import com.yunzia.hyperstar.ui.navigation.LocalNavigator

@Composable
fun ReferencesScreen() {
    val navController = LocalNavigator.current
    val context = LocalContext.current
    NavPager(
        activityTitle = stringResource(R.string.references_title),
        navController = navController,
    ) {
        itemGroup(
            title = "Apache-2.0",
            position = SuperGroupPosition.FIRST
        ) {
            SuperIntentArrow(
                title = "miuix-kotlin-multiplatform",
                summary = "YuKongA",
                context = context,
                url = "https://github.com/miuix-kotlin-multiplatform/miuix"
            )

            SuperIntentArrow(
                title = "Xposed",
                summary = "rovo89,Tungstwenty",
                context = context,
                url = "https://github.com/rovo89/XposedBridge"
            )

            SuperIntentArrow(
                title = "XposedBridge",
                summary = "rovo89",
                context = context,
                url = "https://github.com/rovo89/XposedBridge"
            )

            SuperIntentArrow(
                title = "Haze",
                summary = "Chris Banes",
                context = context,
                url = "https://github.com/chrisbanes/haze"
            )

            SuperIntentArrow(
                title = "Gson",
                summary = "Android Open Source Project, Google Inc.",
                context = context,
                url = "https://github.com/google/gson"
            )

            SuperIntentArrow(
                title = "OkHttp",
                summary = "squarejesse",
                context = context,
                url = "https://github.com/square/okhttp"
            )

            SuperIntentArrow(
                title = "AndroidLiquidGlass",
                summary = "Kyant",
                context = context,
                url = "https://github.com/Kyant0/AndroidLiquidGlass"
            )
        }
        itemGroup(title = "AGPL-3.0") {

            SuperIntentArrow(
                title = "HyperCeiler",
                summary = "ReChronoRain",
                context = context,
                url = "https://github.com/ReChronoRain/HyperCeiler"
            )

        }
        itemGroup(
            title = "No License",
            position = SuperGroupPosition.LAST
        ) {

            SuperIntentArrow(
                title = "IAmNotADeveloper",
                summary = "ReChronoRain",
                context = context,
                url = "https://github.com/xfqwdsj/IAmNotADeveloper"
            )

        }
    }
}