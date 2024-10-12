package com.chaos.hyperstar.ui.module.systemui.controlcenter.media

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.chaos.hyperstar.ui.base.BaseActivity

class MediaSettingsActivity : BaseActivity() {
    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        MediaSettingsPager(this)
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

}
