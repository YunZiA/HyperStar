package com.chaos.hyperstar.ui.module.controlcenter.list

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.chaos.hyperstar.ui.base.BaseActivity

class QsListViewSettings : BaseActivity() {
    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        QsListViewPager(this)
    }

    override fun initData(savedInstanceState: Bundle?) {

    }


}
