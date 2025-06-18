package com.yunzia.hyperstar.ui.screen.pagers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.SuperIntentArrow
import com.yunzia.hyperstar.ui.component.classes
import com.yunzia.hyperstar.ui.component.firstClasses
import com.yunzia.hyperstar.ui.component.pager.NavPager

@Composable
fun ReferencesScreen(
    navController: NavController,
    currentStartDestination: MutableState<String>,
) {
    NavPager(
        activityTitle = stringResource(R.string.references_title),
        navController = navController,
        parentRoute = currentStartDestination,
    ) {
        firstClasses(
            title = "Apache-2.0"
        ) {
            SuperIntentArrow(
                title = "miuix-kotlin-multiplatform",
                summary = "YuKongA",
                navController = navController,
                url = "https://github.com/miuix-kotlin-multiplatform/miuix"
            )

            SuperIntentArrow(
                title = "Xposed",
                summary = "rovo89,Tungstwenty",
                navController = navController,
                url = "https://github.com/rovo89/XposedBridge"
            )

            SuperIntentArrow(
                title = "XposedBridge",
                summary = "rovo89",
                navController = navController,
                url = "https://github.com/rovo89/XposedBridge"
            )

            SuperIntentArrow(
                title = "Haze",
                summary = "Chris Banes",
                navController = navController,
                url = "https://github.com/chrisbanes/haze"
            )

            SuperIntentArrow(
                title = "Gson",
                summary = "Android Open Source Project, Google Inc.",
                navController = navController,
                url = "https://github.com/google/gson"
            )

            SuperIntentArrow(
                title = "OkHttp",
                summary = "squarejesse",
                navController = navController,
                url = "https://github.com/square/okhttp"
            )
        }
        classes(title = "AGPL-3.0") {

            SuperIntentArrow(
                title = "HyperCeiler",
                summary = "ReChronoRain",
                navController = navController,
                url = "https://github.com/ReChronoRain/HyperCeiler"
            )

        }
        classes(title = "No License") {

            SuperIntentArrow(
                title = "IAmNotADeveloper",
                summary = "ReChronoRain",
                navController = navController,
                url = "https://github.com/xfqwdsj/IAmNotADeveloper"
            )

        }
    }
}