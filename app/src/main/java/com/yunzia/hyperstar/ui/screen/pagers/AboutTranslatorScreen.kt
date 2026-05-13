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
fun TranslatorScreen() {
    val navController = LocalNavigator.current
    val context = LocalContext.current
    PreferenceListPage(
        title = stringResource(R.string.translator),
        navController = navController
    ) {
        itemGroup("English") {
            IntentPreference(
                title = "cafayeli",
                summary = "Telegram@cafayeli",
                context = context,
                url = "https://t.me/cafayeli"
            )
            IntentPreference(
                title = "Natsukawa Masuzu",
                summary = "Telegram@Minggg07",
                context = context,
                url = "https://t.me/Minggg07"
            )
        }
        itemGroup("Русский") {
            IntentPreference(
                title = "Алексей",
                summary = "Telegram@Osean22",
                context = context,
                url = "https://t.me/Osean22"
            )
        }
        itemGroup("tiếng việt") {
            IntentPreference(
                title = "Natsukawa Masuzu",
                summary = "Telegram@Minggg07",
                context = context,
                url = "https://t.me/Minggg07"
            )
        }
        itemGroup("Español (México)") {
            IntentPreference(
                title = "Julio César",
                summary = "Telegram@jj24_0",
                context = context,
                url = "https://t.me/jj24_0"
            )
        }
        itemGroup("Português (Brasil)") {
            IntentPreference(
                title = "Adaias Junior",
                summary = "Telegram@Miel11s",
                context = context,
                url = "https://t.me/Miel11s"
            )
        }
    }
}
