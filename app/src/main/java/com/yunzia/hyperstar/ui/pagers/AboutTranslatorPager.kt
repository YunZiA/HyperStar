package com.yunzia.hyperstar.ui.pagers

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.NavPager
import com.yunzia.hyperstar.ui.base.SuperIntentArrow
import com.yunzia.hyperstar.ui.base.firstClasses

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