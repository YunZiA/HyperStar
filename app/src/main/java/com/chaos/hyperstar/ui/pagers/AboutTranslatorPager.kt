package com.chaos.hyperstar.ui.pagers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.BaseActivity
import com.chaos.hyperstar.ui.base.NavPager
import com.chaos.hyperstar.ui.base.SuperIntentArrow
import com.chaos.hyperstar.ui.base.firstClasses

@Composable
fun TranslatorPager(
    navController: NavController,
) {
    NavPager(
        activityTitle = stringResource(R.string.translator),
        navController = navController,
    ) {
        firstClasses(
            title = "English"
        ) {
            SuperIntentArrow(
                title = "cafayeli",
                summary = "Telegram@cafayeli",
                navController = navController,
                url = "https://t.me/cafayeli"
            )
            SuperIntentArrow(
                title = "Natsukawa Masuzu",
                summary = "Telegram@Minggg07",
                navController = navController,
                url = "https://t.me/Minggg07"
            )
        }
    }
}