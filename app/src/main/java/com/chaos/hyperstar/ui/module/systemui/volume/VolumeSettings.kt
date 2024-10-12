package com.chaos.hyperstar.ui.module.systemui.volume

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.chaos.hyperstar.ui.base.BaseActivity

class VolumeSettings : BaseActivity() {
    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        VolumePager(this)
    }

    override fun initData(savedInstanceState: Bundle?) {

    }



}