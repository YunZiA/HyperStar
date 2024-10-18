package com.chaos.hyperstar.ui.pagers

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.NavPager
import com.chaos.hyperstar.ui.base.SuperIntentArrow
import com.chaos.hyperstar.ui.base.classes
import com.chaos.hyperstar.ui.base.firstClasses

@Composable
fun ReferencesPager(
    navController: NavController,
) {
    NavPager(
        activityTitle = stringResource(R.string.references_title),
        navController = navController,
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
        }
        classes(title = "AGPL-3.0") {

            SuperIntentArrow(
                title = "HyperCeiler",
                summary = "ReChronoRain",
                navController = navController,
                url = "https://github.com/ReChronoRain/HyperCeiler"
            )

        }
    }
}