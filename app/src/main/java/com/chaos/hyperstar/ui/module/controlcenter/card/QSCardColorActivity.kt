package com.chaos.hyperstar.ui.module.controlcenter.card

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.chaos.hyperstar.ui.base.BaseActivity
import top.yukonga.miuix.kmp.extra.SuperDialog

class QSCardColorActivity : BaseActivity() {
    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        QSCardColorPager(this)
    }

    override fun initData(savedInstanceState: Bundle?) {

    }


}