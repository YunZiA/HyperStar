package com.chaos.hyperstar.ui.module.controlcenter


import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.chaos.hyperstar.ui.base.BaseActivity
import com.chaos.hyperstar.ui.module.controlcenter.ui.ControlCenterPager

class ControlCenterColorSettings : BaseActivity() {

    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        ControlCenterColorPager(this)
    }

    override fun initData() {

    }

}