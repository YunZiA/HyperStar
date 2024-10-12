package com.chaos.hyperstar.ui.module.systemui.controlcenter.slider

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.chaos.hyperstar.ui.base.BaseActivity

class ToggleSliderColorsSettings : BaseActivity() {

    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        ToggleSliderColorsPager(this)
    }

    override fun initData(savedInstanceState: Bundle?) {

    }
}