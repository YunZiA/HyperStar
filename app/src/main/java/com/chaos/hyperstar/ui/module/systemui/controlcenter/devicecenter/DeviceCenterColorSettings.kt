package com.chaos.hyperstar.ui.module.systemui.controlcenter.devicecenter

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.chaos.hyperstar.ui.base.BaseActivity
import com.chaos.hyperstar.ui.module.systemui.controlcenter.ControlCenterColorPager

class DeviceCenterColorSettings : BaseActivity() {

    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        DeviceCenterColorPager(this)
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

}