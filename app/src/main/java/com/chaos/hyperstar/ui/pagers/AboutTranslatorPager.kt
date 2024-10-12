package com.chaos.hyperstar.ui.pagers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ActivityPager
import com.chaos.hyperstar.ui.base.BaseActivity
import com.chaos.hyperstar.ui.base.SuperIntentArrow
import com.chaos.hyperstar.ui.base.firstClasses

class AboutTranslatorPager : BaseActivity() {

    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        TranslatorPager(this)
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

}

@Composable
fun TranslatorPager(
    activity: ComponentActivity,
) {
    ActivityPager(
        activityTitle = stringResource(R.string.translator),
        activity = activity,
    ) {
        firstClasses(
            title = "English"
        ) {
            SuperIntentArrow(
                //leftIcon = R.drawable.dd,
                title = "cafayeli",
                summary = "Telegram@cafayeli",
                activity = activity,
                url = "https://t.me/cafayeli"
            )
            SuperIntentArrow(
                //leftIcon = R.drawable.dd,
                title = "Natsukawa Masuzu",
                summary = "Telegram@Minggg07",
                activity = activity,
                url = "https://t.me/Minggg07"
            )
        }
    }
}