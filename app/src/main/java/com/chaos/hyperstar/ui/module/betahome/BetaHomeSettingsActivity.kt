package com.chaos.hyperstar.ui.module.betahome


import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.chaos.hyperstar.ui.base.BaseActivity

class BetaHomeSettingsActivity : BaseActivity() {
    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        BetaHomePager(this)
    }

    override fun initData(savedInstanceState: Bundle?) {

    }
}