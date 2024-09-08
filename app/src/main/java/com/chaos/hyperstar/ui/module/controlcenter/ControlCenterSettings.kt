package com.chaos.hyperstar.ui.module.controlcenter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.chaos.hyperstar.ui.base.BaseActivity
import com.chaos.hyperstar.ui.module.controlcenter.ui.ControlCenterPager

class ControlCenterSettings : BaseActivity() {

    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        ControlCenterPager(this)
    }

    override fun initData() {

    }

}

