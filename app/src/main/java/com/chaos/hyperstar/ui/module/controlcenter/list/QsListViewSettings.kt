package com.chaos.hyperstar.ui.module.controlcenter.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.chaos.hyperstar.ui.base.BaseActivity
import com.chaos.hyperstar.ui.module.controlcenter.list.ui.QsListViewPager

class QsListViewSettings : BaseActivity() {
    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        QsListViewPager(this)
    }

    override fun initData() {

    }


}
