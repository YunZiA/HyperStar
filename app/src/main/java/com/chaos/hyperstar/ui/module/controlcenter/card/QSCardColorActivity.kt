package com.chaos.hyperstar.ui.module.controlcenter.card

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.chaos.hyperstar.ui.base.BaseActivity

class QSCardColorActivity : BaseActivity() {
    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        QSCardColorPager(this)
    }

    override fun initData() {

    }


}