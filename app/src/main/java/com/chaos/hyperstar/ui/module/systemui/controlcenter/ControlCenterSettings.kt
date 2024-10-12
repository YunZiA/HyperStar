package com.chaos.hyperstar.ui.module.systemui.controlcenter

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.chaos.hyperstar.ui.base.BaseActivity

class ControlCenterSettings : BaseActivity() {

    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        ControlCenterPager(this)
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

}

