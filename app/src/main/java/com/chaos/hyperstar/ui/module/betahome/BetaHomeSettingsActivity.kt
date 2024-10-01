package com.chaos.hyperstar.ui.module.betahome


import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.chaos.hyperstar.ui.base.BaseActivity
import com.chaos.hyperstar.ui.module.volume.VolumePager

class BetaHomeSettingsActivity : BaseActivity() {
    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        BetaHomePager(this)
    }

    override fun initData() {

    }
}