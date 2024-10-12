package com.chaos.hyperstar.ui.module.systemui.controlcenter.list

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.chaos.hyperstar.ui.base.BaseActivity

class QSListColorActivity : BaseActivity() {
    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        QSListColorPager(this)
    }

    override fun initData(savedInstanceState: Bundle?) {

    }


}