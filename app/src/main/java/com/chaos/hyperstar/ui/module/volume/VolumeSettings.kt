package com.chaos.hyperstar.ui.module.volume

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.chaos.hyperstar.ui.base.BaseActivity
import com.chaos.hyperstar.ui.module.controlcenter.list.ui.QsListViewPager
import com.chaos.hyperstar.ui.module.ui.theme.HyperStarTheme
import com.chaos.hyperstar.utils.PreferencesUtil

class VolumeSettings : BaseActivity() {
    @Composable
    override fun InitView(colorMode: MutableState<Int>?) {
        VolumePager(this)
    }

    override fun initData() {

    }



}