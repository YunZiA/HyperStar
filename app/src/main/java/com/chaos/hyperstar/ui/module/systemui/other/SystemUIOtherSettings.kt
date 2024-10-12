package com.chaos.hyperstar.ui.module.systemui.other

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.chaos.hyperstar.ui.base.BaseActivity
import com.chaos.hyperstar.ui.module.systemui.controlcenter.ControlCenterPager

class SystemUIOtherSettings : BaseActivity() {

    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        SystemUIOtherPager(this)
    }

    override fun initData(savedInstanceState: Bundle?) {

    }
}