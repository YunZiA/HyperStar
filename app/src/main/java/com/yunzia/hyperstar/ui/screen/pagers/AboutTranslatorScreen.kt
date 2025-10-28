package com.yunzia.hyperstar.ui.screen.pagers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.SuperIntentArrow
import com.yunzia.hyperstar.ui.component.itemGroup
import com.yunzia.hyperstar.ui.component.pager.NavPager

@Composable
fun TranslatorScreen(
    navController: NavController,
    currentStartDestination: MutableState<String>,
) {
    NavPager(
        activityTitle = stringResource(R.string.translator),
        navController = navController,
        parentRoute = currentStartDestination,
    ) {
        itemGroup(
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
        this.itemGroup(
            title = "Русский"
        ) {
            SuperIntentArrow(
                title = "Алексей",
                summary = "Telegram@Osean22",
                navController = navController,
                url = "https://t.me/Osean22"
            )
        }
        this.itemGroup(
            title = "tiếng việt"
        ) {
            SuperIntentArrow(
                title = "Natsukawa Masuzu",
                summary = "Telegram@Minggg07",
                navController = navController,
                url = "https://t.me/Minggg07"
            )
        }
        this.itemGroup(
            title = "Español (México)"
        ) {
            SuperIntentArrow(
                title = "Julio César",
                summary = "Telegram@jj24_0",
                navController = navController,
                url = "https://t.me/jj24_0"
            )
        }
        this.itemGroup(
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