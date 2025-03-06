package com.yunzia.hyperstar.ui.pagers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.NavPager
import com.yunzia.hyperstar.ui.base.SuperIntentArrow
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.ui.base.firstClasses

@Composable
fun TranslatorPager(
    navController: NavController,
    currentStartDestination: MutableState<String>,
) {
    NavPager(
        activityTitle = stringResource(R.string.translator),
        navController = navController,
        parentRoute = currentStartDestination,
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
        classes(
            title = "Русский"
        ) {
            SuperIntentArrow(
                title = "Алексей",
                summary = "Telegram@Osean22",
                navController = navController,
                url = "https://t.me/Osean22"
            )
        }
        classes(
            title = "tiếng việt"
        ) {
            SuperIntentArrow(
                title = "Natsukawa Masuzu",
                summary = "Telegram@Minggg07",
                navController = navController,
                url = "https://t.me/Minggg07"
            )
        }
        classes(
            title = "Español (México)"
        ) {
            SuperIntentArrow(
                title = "Julio César",
                summary = "Telegram@jj24_0",
                navController = navController,
                url = "https://t.me/jj24_0"
            )
        }
        classes(
            title = "Português (Brasil)"
        ) {
            SuperIntentArrow(
                title = "Adaias Junior",
                summary = "Telegram@Miel11s",
                navController = navController,
                url = "https://t.me/Miel11s"
            )

        }
    }
}